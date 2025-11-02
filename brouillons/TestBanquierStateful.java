import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import banquier.BanquierRemote;

public class TestBanquierStateful {
    
    // Variables en dehors des méthodes pour être sûr
    private static BanquierRemote sessionEjb = null;
    private static Context context = null;
    
    /**
     * Méthode 1: Connexion à l'EJB Stateful
     */
    public static void connexionEjb() {
        try {
            System.out.println("=== MÉTHODE 1: CONNEXION EJB ===");
            
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            props.put(Context.PROVIDER_URL, "http-remoting://localhost:8081");
            
            context = new InitialContext(props);
            
            // Création d'une NOUVELLE instance Stateful
            sessionEjb = (BanquierRemote) context.lookup(
                "ejb:/banquier-ejb-1.0.0/BanquierBean!banquier.BanquierRemote"
            );
            
            System.out.println("✅ EJB Stateful créé: " + sessionEjb);
            
            // Login avec admin
            boolean connecte = sessionEjb.login("admin", "admin123");
            
            if (connecte) {
                System.out.println("✅ Login ADMIN réussi");
                System.out.println("✅ Session EJB activée");
            } else {
                System.out.println("❌ Login échoué");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Erreur connexion EJB: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Méthode 2: Vérification du contenu de la session EJB
     */
    // public static void checkSessionEjb() {
    //     try {
    //         System.out.println("\n=== MÉTHODE 2: CHECK SESSION EJB ===");
            
    //         if (sessionEjb == null) {
    //             System.out.println("❌ Aucun EJB Stateful disponible");
    //             return;
    //         }
            
    //         // Vérifier si la session est active
    //         boolean estConnecte = sessionEjb.estConnecte();
    //         System.out.println("Session EJB active: " + estConnecte);
            
    //         if (estConnecte) {
    //             // Récupérer les infos du banquier depuis la session EJB
    //             var banquier = sessionEjb.getBanquierConnecte();
    //             System.out.println("✅ Banquier dans session EJB:");
    //             System.out.println("   - Identifiant: " + banquier.getIdentifiant());
    //             System.out.println("   - Nom: " + banquier.getNom());
    //             System.out.println("   - Niveau: " + banquier.getNiveau());
    //             System.out.println("   - Rôle: " + banquier.getRole());
                
    //             // Tester les permissions
    //             System.out.println("\n🔍 Test permissions session EJB:");
    //             boolean niveau = sessionEjb.aNiveau("courant", "laplap");
    //             boolean role = sessionEjb.aRole("courant", "laplap");
    //             System.out.println("   - Niveau compte courant: " + niveau);
    //             System.out.println("   - Rôle compte courant: " + role);
                
    //             // Test opération
    //             System.out.println("\n💼 Test opération session EJB:");
    //             if (niveau && role) {
    //                 System.out.println("   ✅ Session EJB autorise les opérations");
    //             } else {
    //                 System.out.println("   ❌ Session EJB limite les opérations");
    //             }
                
    //         } else {
    //             System.out.println("❌ Aucun banquier dans la session EJB");
    //         }
            
    //     } catch (Exception e) {
    //         System.out.println("❌ Erreur check session EJB: " + e.getMessage());
    //         e.printStackTrace();
    //     }
    // }
    
    /**
     * Méthode 3: Déconnexion et destruction de l'EJB
     */
    public static void deconnexionEjb() {
        try {
            System.out.println("\n=== MÉTHODE 3: DÉCONNEXION EJB ===");
            
            if (sessionEjb != null) {
                // Destruction de l'EJB Stateful
                sessionEjb.logout();
                System.out.println("✅ EJB Stateful détruit via @Remove");
                
                // Vérifier que l'EJB est bien détruit
                try {
                    boolean encoreConnecte = sessionEjb.estConnecte();
                    System.out.println("❌ ERREUR: EJB toujours connecté: " + encoreConnecte);
                } catch (Exception e) {
                    System.out.println("✅ COMPORTEMENT ATTENDU: " + e.getClass().getSimpleName());
                    System.out.println("✅ EJB correctement détruit - Plus accessible");
                }
                
                sessionEjb = null;
            }
            
            // Fermer le contexte
            if (context != null) {
                context.close();
                System.out.println("✅ Contexte JNDI fermé");
                context = null;
            }
            
        } catch (Exception e) {
            System.out.println("❌ Erreur déconnexion EJB: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * MAIN - Appel séquentiel des méthodes
     */
    public static void main(String[] args) {
        System.out.println("🎯 TEST COMPLET EJB STATEFUL - SESSION BANQUIER");
        System.out.println("===============================================\n");
        
        // Étape 1: Connexion
        connexionEjb();
        
        // // Étape 2: Vérification session
        // checkSessionEjb();
        
        // // Étape 3: Déconnexion
        // deconnexionEjb();
        
        // // Étape 4: Vérification post-déconnexion
        // System.out.println("\n=== VÉRIFICATION POST-DÉCONNEXION ===");
        // try {
        //     if (sessionEjb != null) {
        //         sessionEjb.estConnecte();
        //         System.out.println("❌ ERREUR: EJB toujours accessible");
        //     } else {
        //         System.out.println("✅ EJB correctement nettoyé: null");
        //     }
        // } catch (Exception e) {
        //     System.out.println("✅ COMPORTEMENT ATTENDU: Exception après destruction");
        // }
        
        // System.out.println("\n🎉 TEST TERMINÉ AVEC SUCCÈS");
    }
}