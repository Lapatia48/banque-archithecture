import java.util.List;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import compteCourant.CompteCourantRemote;
import banquier.BanquierRemote;

public class TestBanquierComplet {
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
            
            System.out.println("=== TEST BANQUIER COMPLET ===");
            
            // Test avec ADMIN (role 4)
            System.out.println("\n--- TEST 1: ADMIN (role 4) ---");
            BanquierRemote banquierEjb1 = (BanquierRemote) context.lookup(
                "ejb:/banquier-ejb-1.0.0/BanquierBean!banquier.BanquierRemote"
            );
            testBanquier(banquierEjb1, compteEjb, "admin", "admin123", "laplap");
            
            // Test avec MANAGER (role 2) - NOUVELLE instance
            System.out.println("\n--- TEST 2: MANAGER (role 2) ---");
            BanquierRemote banquierEjb2 = (BanquierRemote) context.lookup(
                "ejb:/banquier-ejb-1.0.0/BanquierBean!banquier.BanquierRemote"
            );
            testBanquier(banquierEjb2, compteEjb, "manager", "mdp123", "laplap");
            
            // Test avec AGENT (role 1) - NOUVELLE instance
            System.out.println("\n--- TEST 3: AGENT (role 1) ---");
            BanquierRemote banquierEjb3 = (BanquierRemote) context.lookup(
                "ejb:/banquier-ejb-1.0.0/BanquierBean!banquier.BanquierRemote"
            );
            testBanquier(banquierEjb3, compteEjb, "agent", "mdp123", "laplap");
            
            context.close();
            
        } catch (Exception e) {
            System.out.println("ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testBanquier(BanquierRemote banquierEjb, CompteCourantRemote compteEjb, 
                                   String identifiantBanquier, String mdpBanquier, String identifiantClient) {
        try {
            // Connexion du banquier
            boolean connecte = banquierEjb.create(identifiantBanquier, mdpBanquier);
            if (!connecte) {
                System.out.println("❌ Échec connexion pour: " + identifiantBanquier);
                return;
            }
            
            System.out.println("✅ Banquier connecté: " + identifiantBanquier);
            System.out.println("Banquier info: " + banquierEjb.getBanquierConnecte());
            
            // Vérification du NIVEAU pour le compte COURANT
            String typeCompte = "courant";
            boolean aNiveau = banquierEjb.aNiveau(typeCompte, identifiantClient);
            System.out.println("📊 Vérification niveau pour " + typeCompte + ": " + aNiveau);
            
            if (aNiveau) {
                // Opérations autorisées avec niveau suffisant
                
                // 1. Récupération solde (toujours autorisé avec niveau)
                Double solde = compteEjb.getSolde(identifiantClient);
                System.out.println("1. Solde client: " + solde);
                
                // 2. Récupération opérations (toujours autorisé avec niveau)
                List<Object[]> operations = compteEjb.getOperations(identifiantClient);
                System.out.println("2. Nombre d'opérations: " + operations.size());
                
                // Vérification du ROLE pour les actions sur le compte COURANT
                boolean aRole = banquierEjb.aRole(typeCompte, identifiantClient);
                System.out.println("🎭 Vérification rôle pour '" + typeCompte + "': " + aRole);
                
                if (aRole) {
                    // Opérations nécessitant un rôle spécifique
                    
                    // 3. Test dépôt
                    try {
                        String resultDepot = compteEjb.faireDepot(identifiantClient, 100.0, "Dépôt par " + identifiantBanquier);
                        System.out.println("3. " + resultDepot);
                    } catch (Exception e) {
                        System.out.println("3. ❌ Dépôt refusé: " + e.getMessage());
                    }
                    
                    // 4. Test retrait
                    try {
                        String resultRetrait = compteEjb.faireRetrait(identifiantClient, 50.0, "Retrait par " + identifiantBanquier);
                        System.out.println("4. " + resultRetrait);
                    } catch (Exception e) {
                        System.out.println("4. ❌ Retrait refusé: " + e.getMessage());
                    }
                    
                    // 5. Test virement
                    try {
                        String resultVirement = compteEjb.faireVirement(identifiantClient, "rixrix", 25.0, "Virement par " + identifiantBanquier);
                        System.out.println("5. " + resultVirement);
                    } catch (Exception e) {
                        System.out.println("5. ❌ Virement refusé: " + e.getMessage());
                    }
                    
                    // 6. Vérification solde final
                    solde = compteEjb.getSolde(identifiantClient);
                    System.out.println("6. Solde final: " + solde);
                    
                } else {
                    System.out.println("🚫 Rôle insuffisant pour les actions sur le compte " + typeCompte);
                    System.out.println("   Seules la consultation du solde et des opérations sont autorisées");
                }
                
            } else {
                System.out.println("🚫 Niveau insuffisant pour le compte " + typeCompte + " - Aucun accès autorisé");
            }
            
            // Déconnexion
            banquierEjb.remove();
            System.out.println("🔒 Banquier déconnecté: " + identifiantBanquier);
            
        } catch (Exception e) {
            System.out.println("❌ Erreur pendant le test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}