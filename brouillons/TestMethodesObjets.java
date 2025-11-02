import java.util.List;
import java.util.Properties;
import java.util.Optional;
import javax.naming.Context;
import javax.naming.InitialContext;
import compteCourant.CompteCourantRemote;
import compteCourant.metier.CompteCourant;
import compteCourant.metier.Virement;

public class TestMethodesObjets {
    public static void main(String[] args) {
        try {
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            props.put(Context.PROVIDER_URL, "http-remoting://localhost:8081");
            
            Context context = new InitialContext(props);
            CompteCourantRemote ejb = (CompteCourantRemote) context.lookup(
                "ejb:/compte-courant-ejb-1.0.0/CompteCourantBean!compteCourant.CompteCourantRemote"
            );
            
            System.out.println("=== TEST MÉTHODES OBJETS PURES ===");
            
            // Test complet avec les nouvelles méthodes
            Optional<CompteCourant> compte = ejb.getCompte("laplap");
            if (compte.isPresent()) {
                CompteCourant cc = compte.get();
                System.out.println("Compte trouvé: " + cc.getLibelle());
                System.out.println("Solde initial: " + cc.getSolde());
                
                // Test virement avec création d'objet
                Virement virement = ejb.creerVirement("laplap", "rixrix", 25.0, "MGA", "Virement test objets");
                System.out.println("Virement créé: " + virement);
                
                // Exécution du virement
                String result = ejb.faireVirement(
                    virement.getIdentifiantSource(),
                    virement.getIdentifiantDestination(), 
                    virement.getMontant(),
                    virement.getDetails()
                );
                System.out.println("Résultat virement: " + result);
                
                // Vérification du solde mis à jour
                compte = ejb.getCompte("laplap");
                if (compte.isPresent()) {
                    System.out.println("Nouveau solde: " + compte.get().getSolde());
                }
            }
            
            context.close();
            
        } catch (Exception e) {
            System.out.println("ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}