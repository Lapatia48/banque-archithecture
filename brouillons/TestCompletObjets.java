import java.util.List;
import java.util.Properties;
import java.util.Optional;
import javax.naming.Context;
import javax.naming.InitialContext;
import compteCourant.CompteCourantRemote;
import compteCourant.metier.CompteCourant;
import compteCourant.metier.Virement;
import compteCourant.metier.Operation;

public class TestCompletObjets {
    public static void main(String[] args) {
        try {
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            props.put(Context.PROVIDER_URL, "http-remoting://localhost:8081");
            
            Context context = new InitialContext(props);
            
            CompteCourantRemote ejb = (CompteCourantRemote) context.lookup(
                "ejb:/compte-courant-ejb-1.0.0/CompteCourantBean!compteCourant.CompteCourantRemote"
            );
            
            System.out.println("=== TEST AVEC OBJETS MÉTIER ===");
            
            // 1. Test récupération de l'objet CompteCourant
            System.out.println("1. Récupération de l'objet CompteCourant:");
            Optional<CompteCourant> compteOpt = ejb.getCompte("laplap");
            if (compteOpt.isPresent()) {
                CompteCourant compte = compteOpt.get();
                System.out.println("   - ID: " + compte.getIdCompte());
                System.out.println("   - Utilisateur: " + compte.getIdentifiantUtilisateur());
                System.out.println("   - Libellé: " + compte.getLibelle());
                System.out.println("   - Solde: " + compte.getSolde());
                System.out.println("   - Niveau: " + compte.getNiveau());
            } else {
                System.out.println("   ❌ Compte non trouvé");
            }
            
            // 2. Test solde initial (méthode classique)
            Double solde = ejb.getSolde("laplap");
            System.out.println("2. Solde initial (méthode classique): " + solde);
            
            // 3. Test dépôt avec objet
            System.out.println("3. Test dépôt avec objet:");
            String resultDepot = ejb.faireDepot("laplap", 200.0, "Test dépôt avec objets");
            System.out.println("   " + resultDepot);
            
            // 4. Vérifier nouveau solde via objet
            compteOpt = ejb.getCompte("laplap");
            if (compteOpt.isPresent()) {
                CompteCourant compteApresDepot = compteOpt.get();
                System.out.println("4. Solde après dépôt (via objet): " + compteApresDepot.getSolde());
            }
            
            // 5. Test retrait avec objet
            System.out.println("5. Test retrait avec objet:");
            String resultRetrait = ejb.faireRetrait("laplap", 75.0, "Test retrait avec objets");
            System.out.println("   " + resultRetrait);
            
            // 6. Test création d'objet Virement
            System.out.println("6. Test création objet Virement:");
            Virement virement = ejb.creerVirement("laplap", "rixrix", 40.0, "MGA", "Test virement objet");
            System.out.println("   Virement créé:");
            System.out.println("   - Source: " + virement.getIdentifiantSource());
            System.out.println("   - Destination: " + virement.getIdentifiantDestination());
            System.out.println("   - Montant: " + virement.getMontant());
            System.out.println("   - Devise: " + virement.getDevise());
            System.out.println("   - Détails: " + virement.getDetails());
            System.out.println("   - Statut: " + virement.getStatut());
            
            // 7. Test virement avec objet
            System.out.println("7. Test exécution virement:");
            String resultVirement = ejb.faireVirement("laplap", "rixrix", 40.0, "Test virement objet");
            System.out.println("   " + resultVirement);
            
            // 8. Test récupération des opérations comme objets
            System.out.println("8. Liste des opérations comme objets:");
            List<Operation> operationsObjets = ejb.getOperationsObjet("laplap");
            for (Operation operation : operationsObjets) {
                System.out.println("   - ID: " + operation.getIdOperation());
                System.out.println("     Date: " + operation.getDateOperation());
                System.out.println("     Type: " + operation.getTypeOperation());
                System.out.println("     Montant: " + operation.getMontant());
                System.out.println("     Détails: " + operation.getDetails());
            }
            
            // 9. Vérification du nombre d'opérations
            System.out.println("9. Nombre total d'opérations (objets): " + operationsObjets.size());
            
            // 10. Test compatibilité avec ancienne méthode
            System.out.println("10. Test compatibilité avec ancienne méthode:");
            List<Object[]> operationsAnciennes = ejb.getOperations("laplap");
            System.out.println("   Nombre d'opérations (ancienne méthode): " + operationsAnciennes.size());
            
            // 11. Solde final via objet
            compteOpt = ejb.getCompte("laplap");
            if (compteOpt.isPresent()) {
                CompteCourant compteFinal = compteOpt.get();
                System.out.println("11. Solde final (via objet): " + compteFinal.getSolde());
                
                // Test méthode métier sur l'objet
                System.out.println("12. Test méthodes métier sur l'objet:");
                System.out.println("   - Solde suffisant pour 100: " + compteFinal.soldeSuffisant(100.0));
                System.out.println("   - Solde suffisant pour 500: " + compteFinal.soldeSuffisant(500.0));
            }
            
            // 12. Test sur le compte destination
            System.out.println("13. Vérification compte destination:");
            Optional<CompteCourant> compteDestOpt = ejb.getCompte("rixrix");
            if (compteDestOpt.isPresent()) {
                CompteCourant compteDest = compteDestOpt.get();
                System.out.println("   - Solde compte destination: " + compteDest.getSolde());
            }
            
            context.close();
            
            System.out.println("=== TEST TERMINÉ AVEC SUCCÈS ===");
            
        } catch (Exception e) {
            System.out.println("ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}