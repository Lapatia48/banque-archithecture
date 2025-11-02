using Npgsql;

var builder = WebApplication.CreateBuilder(args);
var app = builder.Build();

string connString = "Host=localhost;Username=postgres;Password=lapatia1706;Database=banque_prog_2";


// Endpoint pour afficher le solde d’un compte Dépôt
app.MapGet("/solde/{identifiant}", async (string identifiant) =>
{
    using var conn = new NpgsqlConnection(connString);
    await conn.OpenAsync();

    string sql = @"
        SELECT solde 
        FROM Comptes 
        WHERE identifiant = @identifiant AND type_compte = 'depot';";

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


// Endpoint POST pour effectuer un dépôt
app.MapPost("/depot", async (HttpRequest request) =>
{
    // Lire le body du formulaire
    var form = await request.ReadFormAsync();

    string identifiant = form["identifiant"]!;
    decimal montant = decimal.Parse(form["montant"]!);
    string compte = form["compte"]!;
    string action = form["action"]!;
    string detail = form["detail"]!;

    using var conn = new NpgsqlConnection(connString);
    await conn.OpenAsync();
    using var transaction = conn.BeginTransaction();

    try
    {
        // 1️⃣ Insert dans Operations
        string insertOp = @"
            INSERT INTO Operations (identifiant, type_operation, compte, montant, details)
            VALUES (@identifiant, @action, @compte, @montant, @detail);";

        using var cmd1 = new NpgsqlCommand(insertOp, conn, transaction);
        cmd1.Parameters.AddWithValue("identifiant", identifiant);
        cmd1.Parameters.AddWithValue("action", action);
        cmd1.Parameters.AddWithValue("compte", compte);
        cmd1.Parameters.AddWithValue("montant", montant);
        cmd1.Parameters.AddWithValue("detail", detail);
        await cmd1.ExecuteNonQueryAsync();

        // 2️⃣ Update dans Comptes
        string updateCompte = @"
            UPDATE Comptes
            SET solde = solde + @montant
            WHERE identifiant = @identifiant AND type_compte = @compte;";

        using var cmd2 = new NpgsqlCommand(updateCompte, conn, transaction);
        cmd2.Parameters.AddWithValue("identifiant", identifiant);
        cmd2.Parameters.AddWithValue("compte", compte);
        cmd2.Parameters.AddWithValue("montant", montant);
        int rows = await cmd2.ExecuteNonQueryAsync();

        if (rows == 0)
            throw new Exception("Aucun compte trouvé pour l'utilisateur et le type de compte.");

        await transaction.CommitAsync();

        return Results.Ok($"Dépôt de {montant} effectué pour {identifiant} !");
    }
    catch (Exception ex)
    {
        await transaction.RollbackAsync();
        return Results.BadRequest("Erreur : " + ex.Message);
    }
});


// Endpoint pour simuler le gain futur des dépôts
app.MapGet("/simulationGain", async (string identifiant, DateTime date, int periode) =>
{
    using var conn = new NpgsqlConnection(connString);
    await conn.OpenAsync();

    try
    {
        // 1️⃣ Récupérer le montant actuel du dépôt
        string sqlDepot = "SELECT solde FROM Comptes WHERE identifiant = @identifiant AND type_compte = 'depot'";
        using var cmdDepot = new NpgsqlCommand(sqlDepot, conn);
        cmdDepot.Parameters.AddWithValue("identifiant", identifiant);
        
        var depotResult = await cmdDepot.ExecuteScalarAsync();
        if (depotResult == null || depotResult == DBNull.Value)
        {
            return Results.Ok(0.0);
        }

        decimal depotActuel = Convert.ToDecimal(depotResult);
        
        // Si pas de dépôt, retourner 0
        if (depotActuel <= 0)
        {
            return Results.Ok(0.0);
        }

        // Console.WriteLine($"Dépôt actuel: {depotActuel}");

        // 2️⃣ Vérifier que la période existe dans la base pour les dépôts
        string sqlVerifPeriode = @"
            SELECT pourcentage 
            FROM Pourcentage 
            WHERE type_compte = 'depot' 
            AND periode = @periode";

        using var cmdVerifPeriode = new NpgsqlCommand(sqlVerifPeriode, conn);
        cmdVerifPeriode.Parameters.AddWithValue("periode", periode);
        
        var pourcentageResult = await cmdVerifPeriode.ExecuteScalarAsync();
        
        if (pourcentageResult == null || pourcentageResult == DBNull.Value)
        {
            return Results.BadRequest($"Période {periode} mois non disponible pour les dépôts.");
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

        // Si pas de durée, retourner le dépôt actuel
        if (moisDifference <= 0)
        {
            return Results.Ok((double)depotActuel);
        }

        // 4️⃣ Calculer le nombre de périodes complètes
        int nbPeriodes = moisDifference / periode;
        // Console.WriteLine($"Périodes complètes: {nbPeriodes}");

        // 5️⃣ Calculer le gain futur avec intérêts composés
        double gainFutur = (double)depotActuel;
        
        for (int i = 0; i < nbPeriodes; i++)
        {
            gainFutur *= (1 + taux / 100.0);
        }

        // Console.WriteLine($"Gain futur après {nbPeriodes} périodes de {periode} mois: {gainFutur}");

        return Results.Ok(gainFutur);
    }
    catch (Exception ex)
    {
        return Results.BadRequest("Erreur lors de la simulation de gain: " + ex.Message);
    }
});



// Endpoint GET pour lister les dépôts d'un utilisateur
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
              AND compte = 'depot'
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
