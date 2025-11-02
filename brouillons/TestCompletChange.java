import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import compteCourant.CompteCourantRemote;
import banquier.BanquierRemote;
import change.ChangeRemote;

public class TestCompletChange {
    public static void main(String[] args) {
        try {
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            props.put(Context.PROVIDER_URL, "http-remoting://localhost:8081");
            
            Context context = new InitialContext(props);
            
            // Récupération des EJB
            CompteCourantRemote compteEjb = (CompteCourantRemote) context.lookup(
                "ejb:/compte-courant-ejb-1.0.0/CompteCourantBean!compteCourant.CompteCourantRemote"
            );
            
            ChangeRemote changeEjb = (ChangeRemote) context.lookup(
                "ejb:/change-ejb-1.0.0/ChangeBean!change.ChangeRemote"
            );
            
            System.out.println("=== TEST COMPLET AVEC CHANGE ===");
            
            // Test avec ADMIN
            System.out.println("\n--- TEST 1: ADMIN + CHANGE ---");
            BanquierRemote banquierEjb1 = (BanquierRemote) context.lookup(
                "ejb:/banquier-ejb-1.0.0/BanquierBean!banquier.BanquierRemote"
            );
            testAvecChange(banquierEjb1, compteEjb, changeEjb, "admin", "admin123", "laplap");
            
            context.close();
            
        } catch (Exception e) {
            System.out.println("ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testAvecChange(BanquierRemote banquierEjb, CompteCourantRemote compteEjb, 
                                     ChangeRemote changeEjb, String identifiantBanquier, 
                                     String mdpBanquier, String identifiantClient) {
        try {
            // Connexion du banquier
            boolean connecte = banquierEjb.create(identifiantBanquier, mdpBanquier);
            if (!connecte) {
                System.out.println("❌ Échec connexion pour: " + identifiantBanquier);
                return;
            }
            
            System.out.println("✅ Banquier connecté: " + identifiantBanquier);
            
            // Vérification niveau
            if (!banquierEjb.aNiveau("courant", identifiantClient)) {
                System.out.println("🚫 Niveau insuffisant");
                return;
            }
            
            // 1. Test service de change
            System.out.println("\n💱 TEST SERVICE CHANGE:");
            
            // Liste des devises
            var devises = changeEjb.getDevisesDisponibles();
            System.out.println("Devises disponibles: " + devises);
            
            // Info complètes
            var infosDevises = changeEjb.getInfoDevises();
            System.out.println("Infos devises:");
            infosDevises.forEach((code, info) -> {
                System.out.println("  - " + code + ": " + info);
            });
            
            // 2. Tests de conversion
            System.out.println("\n🔄 TESTS CONVERSION:");
            
            // Test EUR
            try {
                Double montantEur = 100.0;
                Double enAriary = changeEjb.convertirVersAriary("EUR", montantEur);
                System.out.println("EUR → MGA: " + montantEur + " EUR = " + enAriary + " MGA");
            } catch (Exception e) {
                System.out.println("❌ Conversion EUR: " + e.getMessage());
            }
            
            // Test USD
            try {
                Double montantUsd = 150.0;
                Double enAriary = changeEjb.convertirVersAriary("USD", montantUsd);
                System.out.println("USD → MGA: " + montantUsd + " USD = " + enAriary + " MGA");
            } catch (Exception e) {
                System.out.println("❌ Conversion USD: " + e.getMessage());
            }
            
            // Test devise expirée (JPY)
            try {
                Double montantJpy = 5000.0;
                Double enAriary = changeEjb.convertirVersAriary("JPY", montantJpy);
                System.out.println("JPY → MGA: " + montantJpy + " JPY = " + enAriary + " MGA");
            } catch (Exception e) {
                System.out.println("✅ Comportement attendu JPY: " + e.getMessage());
            }
            
            // 3. Test dépôt avec conversion
            System.out.println("\n💰 DÉPÔT AVEC CONVERSION:");
            
            if (banquierEjb.aRole("courant", identifiantClient)) {
                // Dépôt en Euro
                Double montantDepotEur = 50.0;
                Double montantEnAriary = changeEjb.convertirVersAriary("EUR", montantDepotEur);
                
                String resultDepot = compteEjb.faireDepot(identifiantClient, montantEnAriary, 
                    "Dépôt de " + montantDepotEur + " EUR (converti en " + montantEnAriary + " MGA)");
                System.out.println("Dépôt: " + resultDepot);
                
                // Vérification solde
                Double solde = compteEjb.getSolde(identifiantClient);
                System.out.println("Solde final: " + solde + " MGA");
            }
            
            // Déconnexion
            banquierEjb.remove();
            System.out.println("\n🔒 Banquier déconnecté: " + identifiantBanquier);
            
        } catch (Exception e) {
            System.out.println("❌ Erreur pendant le test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}