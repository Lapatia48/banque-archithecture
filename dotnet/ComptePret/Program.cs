using Npgsql;

var builder = WebApplication.CreateBuilder(args);
var app = builder.Build();

string connString = "Host=localhost;Username=postgres;Password=lapatia1706;Database=banque_prog_2";

// Endpoint pour afficher le solde d'un compte pret
app.MapGet("/solde/{identifiant}", async (string identifiant) =>
{
    using var conn = new NpgsqlConnection(connString);
    await conn.OpenAsync();

    string sql = @"
        SELECT solde 
        FROM Comptes 
        WHERE identifiant = @identifiant AND type_compte = 'pret';";

    using (var cmd = new NpgsqlCommand(sql, conn))
    {
        cmd.Parameters.AddWithValue("identifiant", identifiant);
        object result = await cmd.ExecuteScalarAsync();

        if (result != null && result != DBNull.Value)
        {
            return Results.Ok(Convert.ToDouble(result));
        }
        else
        {
            return Results.Ok(0.0);
        }
    }
});

//endpoint pour demander pret
app.MapPost("/demanderPret", async (HttpRequest request) =>
{
    var form = await request.ReadFormAsync();
    string identifiant = form["identifiant"]!;
    decimal montant = decimal.Parse(form["montant"]!);
    int periode = int.Parse(form["periode"]!);

    using var conn = new NpgsqlConnection(connString);
    await conn.OpenAsync();
    using var transaction = conn.BeginTransaction();

    try
    {
        // 1️⃣ Vérifier s'il y a un pret en cours
        string checkReste = "SELECT solde FROM Comptes WHERE identifiant = @identifiant AND type_compte='pret'";
        using var cmdCheck = new NpgsqlCommand(checkReste, conn, transaction);
        cmdCheck.Parameters.AddWithValue("identifiant", identifiant);

        var resteActuel = await cmdCheck.ExecuteScalarAsync();
        if (resteActuel != null && resteActuel != DBNull.Value)
        {
            decimal reste = Convert.ToDecimal(resteActuel);
            if (reste < 0)
            {
                await transaction.RollbackAsync();
                return Results.BadRequest("Veuillez d'abord rembourser votre pret précédent.");
            }
        }

        // 2️⃣ Récupérer le pourcentage pour la période spécifiée
        string sqlPourcentage = @"
            SELECT pourcentage 
            FROM Pourcentage 
            WHERE type_compte = 'pret' 
            AND periode = @periode";

        using var cmdPourcentage = new NpgsqlCommand(sqlPourcentage, conn, transaction);
        cmdPourcentage.Parameters.AddWithValue("periode", periode);
        
        var pourcentageResult = await cmdPourcentage.ExecuteScalarAsync();
        
        if (pourcentageResult == null || pourcentageResult == DBNull.Value)
        {
            await transaction.RollbackAsync();
            return Results.BadRequest($"Période de {periode} mois non disponible pour les prets.");
        }

        decimal tauxPourcentage = Convert.ToDecimal(pourcentageResult);

        // 3️⃣ Calculer le montant pondéré
        decimal montantPondere = montant * (1 + tauxPourcentage / 100);

        // 4️⃣ Insérer dans Operations pour le compte pret (montant négatif pour dette)
        string insertOp = @"
            INSERT INTO Operations (identifiant, type_operation, compte, montant, details)
            VALUES (@identifiant, 'pret', 'pret', @montant, @details)";

        using var cmdOp = new NpgsqlCommand(insertOp, conn, transaction);
        cmdOp.Parameters.AddWithValue("identifiant", identifiant);
        cmdOp.Parameters.AddWithValue("montant", -montantPondere); // Négatif pour dette avec intérêts
        cmdOp.Parameters.AddWithValue("details", $"pret de {montant} sur {periode} mois a {tauxPourcentage}% - Total a rembourser: {montantPondere}");
        await cmdOp.ExecuteNonQueryAsync();

        // 5️⃣ Mettre a jour le compte courant (argent reçu)
        string updateCompteCourant = @"
            UPDATE Comptes 
            SET solde = solde + @montant 
            WHERE identifiant = @identifiant AND type_compte = 'courant'";

        using var cmdCompte = new NpgsqlCommand(updateCompteCourant, conn, transaction);
        cmdCompte.Parameters.AddWithValue("identifiant", identifiant);
        cmdCompte.Parameters.AddWithValue("montant", montant);
        int rows = await cmdCompte.ExecuteNonQueryAsync();

        if (rows == 0)
            throw new Exception("Compte courant non trouvé pour cet utilisateur.");

        // 6️⃣ Insérer dans Operations pour le compte courant (crédit)
        string insertOpc = @"
            INSERT INTO Operations (identifiant, type_operation, compte, montant, details)
            VALUES (@identifiant, 'pret', 'courant', @montant, @details)";

        using var cmdOpc = new NpgsqlCommand(insertOpc, conn, transaction);
        cmdOpc.Parameters.AddWithValue("identifiant", identifiant);
        cmdOpc.Parameters.AddWithValue("montant", montant); // Positif pour crédit
        cmdOpc.Parameters.AddWithValue("details", $"pret de {montant} sur {periode} mois reçu - Taux: {tauxPourcentage}%");
        await cmdOpc.ExecuteNonQueryAsync();

        // 7️⃣ Mettre a jour le compte pret (montant négatif pour la dette avec intérêts)
        string updateComptePret = @"
            UPDATE Comptes 
            SET solde = solde - @montantPondere 
            WHERE identifiant = @identifiant AND type_compte = 'pret'";

        using var cmdComptePret = new NpgsqlCommand(updateComptePret, conn, transaction);
        cmdComptePret.Parameters.AddWithValue("identifiant", identifiant);
        cmdComptePret.Parameters.AddWithValue("montantPondere", montantPondere);
        int rowsPret = await cmdComptePret.ExecuteNonQueryAsync();


        await transaction.CommitAsync();
        return Results.Ok($"pret de {montant} accordé sur {periode} mois! Taux: {tauxPourcentage}%. Total a rembourser: {montantPondere}");
    }
    catch (Exception ex)
    {
        await transaction.RollbackAsync();
        return Results.BadRequest("Erreur lors de la demande de pret: " + ex.Message);
    }
});

// Endpoint pour rembourser un pret
app.MapPost("/rembourserPret", async (HttpRequest request) =>
{
    var form = await request.ReadFormAsync();
    string identifiant = form["identifiant"]!;
    decimal montantRemboursement = decimal.Parse(form["montant"]!);

    using var conn = new NpgsqlConnection(connString);
    await conn.OpenAsync();
    using var transaction = conn.BeginTransaction();

    try
    {
        // 1️⃣ Verifier le solde du compte pret (dette)
        string checkDette = "SELECT solde FROM Comptes WHERE identifiant = @identifiant AND type_compte = 'pret'";
        using var cmdDette = new NpgsqlCommand(checkDette, conn, transaction);
        cmdDette.Parameters.AddWithValue("identifiant", identifiant);

        var detteResult = await cmdDette.ExecuteScalarAsync();
        if (detteResult == null || detteResult == DBNull.Value)
        {
            await transaction.RollbackAsync();
            return Results.BadRequest("Aucun pret en cours pour cet utilisateur.");
        }

        decimal detteActuelle = Convert.ToDecimal(detteResult);

        // Si pas de dette
        if (detteActuelle >= 0)
        {
            await transaction.RollbackAsync();
            return Results.BadRequest("Aucune dette a rembourser.");
        }

        // Convertir la dette en positif pour les calculs
        decimal dettePositive = Math.Abs(detteActuelle);

        // 2️⃣ Verifier le solde du compte courant
        string checkCompteCourant = "SELECT solde FROM Comptes WHERE identifiant = @identifiant AND type_compte = 'courant'";
        using var cmdCompteCourant = new NpgsqlCommand(checkCompteCourant, conn, transaction);
        cmdCompteCourant.Parameters.AddWithValue("identifiant", identifiant);

        var soldeCourantResult = await cmdCompteCourant.ExecuteScalarAsync();
        if (soldeCourantResult == null || soldeCourantResult == DBNull.Value)
        {
            await transaction.RollbackAsync();
            return Results.BadRequest("Compte courant non trouve.");
        }

        decimal soldeCourant = Convert.ToDecimal(soldeCourantResult);

        // 3️⃣ Verifications de capacite de remboursement
        if (montantRemboursement > soldeCourant)
        {
            await transaction.RollbackAsync();
            return Results.BadRequest($"Incapacite a rembourser. Solde courant insuffisant: {soldeCourant}");
        }

        // 4️⃣ Ajuster le montant si necessaire
        decimal montantEffectif = montantRemboursement;
        if (montantRemboursement > dettePositive)
        {
            montantEffectif = dettePositive;
        }

        // 5️⃣ Mettre a jour le compte courant (deduction)
        string updateCompteCourant = @"
            UPDATE Comptes 
            SET solde = solde - @montant 
            WHERE identifiant = @identifiant AND type_compte = 'courant'";

        using var cmdUpdateCourant = new NpgsqlCommand(updateCompteCourant, conn, transaction);
        cmdUpdateCourant.Parameters.AddWithValue("identifiant", identifiant);
        cmdUpdateCourant.Parameters.AddWithValue("montant", montantEffectif);
        await cmdUpdateCourant.ExecuteNonQueryAsync();

        // 6️⃣ Mettre a jour le compte pret (reduction de la dette)
        string updateComptePret = @"
            UPDATE Comptes 
            SET solde = solde + @montant 
            WHERE identifiant = @identifiant AND type_compte = 'pret'";

        using var cmdUpdatePret = new NpgsqlCommand(updateComptePret, conn, transaction);
        cmdUpdatePret.Parameters.AddWithValue("identifiant", identifiant);
        cmdUpdatePret.Parameters.AddWithValue("montant", montantEffectif);
        await cmdUpdatePret.ExecuteNonQueryAsync();

        // 7️⃣ Inserer l'operation de remboursement pour le compte pret
        string insertOpPret = @"
            INSERT INTO Operations (identifiant, type_operation, compte, montant, details)
            VALUES (@identifiant, 'remboursement', 'pret', @montant, @details)";

        using var cmdOpPret = new NpgsqlCommand(insertOpPret, conn, transaction);
        cmdOpPret.Parameters.AddWithValue("identifiant", identifiant);
        cmdOpPret.Parameters.AddWithValue("montant", montantEffectif); // Positif pour remboursement
        cmdOpPret.Parameters.AddWithValue("details", $"Remboursement de {montantEffectif} sur pret. Dette restante: {dettePositive - montantEffectif}");
        await cmdOpPret.ExecuteNonQueryAsync();

        // 8️⃣ Inserer l'operation de debit pour le compte courant
        string insertOpCourant = @"
            INSERT INTO Operations (identifiant, type_operation, compte, montant, details)
            VALUES (@identifiant, 'remboursement', 'courant', @montant, @details)";

        using var cmdOpCourant = new NpgsqlCommand(insertOpCourant, conn, transaction);
        cmdOpCourant.Parameters.AddWithValue("identifiant", identifiant);
        cmdOpCourant.Parameters.AddWithValue("montant", -montantEffectif); // Negatif pour debit
        cmdOpCourant.Parameters.AddWithValue("details", $"Remboursement pret: {montantEffectif}");
        await cmdOpCourant.ExecuteNonQueryAsync();

        await transaction.CommitAsync();

        // Message de confirmation
        decimal nouvelleDette = dettePositive - montantEffectif;
        string message = $"Remboursement de {montantEffectif} effectue. ";

        if (nouvelleDette == 0)
        {
            message += "pret entièrement rembourse!";
        }
        else
        {
            message += $"Dette restante: {nouvelleDette}";
        }

        if (montantRemboursement > dettePositive)
        {
            message += $"\nNote: Seulement {dettePositive} a ete preleve (montant superieur a la dette).";
        }

        return Results.Ok(message);
    }
    catch (Exception ex)
    {
        await transaction.RollbackAsync();
        return Results.BadRequest("Erreur lors du remboursement: " + ex.Message);
    }
});

// Endpoint pour simuler la dette future - NOUVELLE LOGIQUE
app.MapGet("/simulationDette", async (string identifiant, DateTime date, int periode) =>
{
    using var conn = new NpgsqlConnection(connString);
    await conn.OpenAsync();

    try
    {
        // 1️⃣ Récupérer le montant actuel de la dette
        string sqlDette = "SELECT solde FROM Comptes WHERE identifiant = @identifiant AND type_compte = 'pret'";
        using var cmdDette = new NpgsqlCommand(sqlDette, conn);
        cmdDette.Parameters.AddWithValue("identifiant", identifiant);
        
        var detteResult = await cmdDette.ExecuteScalarAsync();
        if (detteResult == null || detteResult == DBNull.Value)
        {
            return Results.Ok(0.0);
        }

        decimal detteActuelle = Convert.ToDecimal(detteResult);
        
        if (detteActuelle >= 0)
        {
            return Results.Ok(0.0);
        }

        decimal dettePositive = Math.Abs(detteActuelle);
        // Console.WriteLine($"Dette actuelle: {dettePositive}");

        // 2️⃣ Vérifier que la période existe dans la base
        string sqlVerifPeriode = @"
            SELECT pourcentage 
            FROM Pourcentage 
            WHERE type_compte = 'pret' 
            AND periode = @periode";

        using var cmdVerifPeriode = new NpgsqlCommand(sqlVerifPeriode, conn);
        cmdVerifPeriode.Parameters.AddWithValue("periode", periode);
        
        var pourcentageResult = await cmdVerifPeriode.ExecuteScalarAsync();
        
        if (pourcentageResult == null || pourcentageResult == DBNull.Value)
        {
            return Results.BadRequest($"Période {periode} mois non disponible pour les prêts.");
        }

        double taux = Convert.ToDouble(pourcentageResult);
        // Console.WriteLine($"Taux pour {periode} mois: {taux}%");

        // 3️⃣ Calculer l'écart en mois entre aujourd'hui et la date cible
        DateTime aujourdHui = DateTime.Now;
        int moisDifference = (date.Year - aujourdHui.Year) * 12 + (date.Month - aujourdHui.Month);
        
        if (date.Day < aujourdHui.Day)
        {
            moisDifference--;
        }

        // Console.WriteLine($"Écart en mois: {moisDifference}");

        // Si pas de retard, retourner la dette actuelle
        if (moisDifference <= 0)
        {
            return Results.Ok(-(double)dettePositive);
        }

        // 4️⃣ Calculer le nombre de périodes complètes de retard
        int nbPeriodesRetard = moisDifference / periode;
        // Console.WriteLine($"Périodes de retard: {nbPeriodesRetard}");

        // 5️⃣ Calculer la dette future avec intérêts composés
        double detteFuture = (double)dettePositive;
        
        for (int i = 0; i < nbPeriodesRetard; i++)
        {
            detteFuture *= (1 + taux / 100.0);
        }

        // Console.WriteLine($"Dette future après {nbPeriodesRetard} périodes de {periode} mois: {detteFuture}");

        return Results.Ok(-detteFuture);
    }
    catch (Exception ex)
    {
        return Results.BadRequest("Erreur lors de la simulation: " + ex.Message);
    }
});

// Endpoint GET pour lister les operations de pret d'un utilisateur
app.MapGet("/listOperation/{identifiant}", async (string identifiant) =>
{
    using var conn = new NpgsqlConnection(connString);
    await conn.OpenAsync();

    try
    {
        string query = @"
            SELECT date_operation, montant, compte, type_operation, details
            FROM Operations
            WHERE identifiant = @identifiant
              AND compte = 'pret'
            ORDER BY date_operation DESC;";

        using var cmd = new NpgsqlCommand(query, conn);
        cmd.Parameters.AddWithValue("identifiant", identifiant);

        var resultList = new List<object>();

        using var reader = await cmd.ExecuteReaderAsync();
        while (await reader.ReadAsync())
        {
            resultList.Add(new
            {
                date_operation = reader.GetDateTime(0),
                montant = reader.GetDecimal(1),
                compte = reader.GetString(2),
                type_operation = reader.GetString(3),
                details = reader.IsDBNull(4) ? null : reader.GetString(4)
            });
        }

        return Results.Ok(resultList);
    }
    catch (Exception ex)
    {
        return Results.BadRequest("Erreur : " + ex.Message);
    }
});

app.Run();