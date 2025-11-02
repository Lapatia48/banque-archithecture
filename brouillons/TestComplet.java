import java.util.List;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import compteCourant.CompteCourantRemote;

public class TestComplet {
    public static void main(String[] args) {
        try {
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            props.put(Context.PROVIDER_URL, "http-remoting://localhost:8081");
            
            Context context = new InitialContext(props);
            
            CompteCourantRemote ejb = (CompteCourantRemote) context.lookup(
                "ejb:/compte-courant-ejb-1.0.0/CompteCourantBean!compteCourant.CompteCourantRemote"
            );
            
            System.out.println("=== TEST COMPTE COURANT EJB ===");
            
            // 1. Test solde initial
            Double solde = ejb.getSolde("laplap");
            System.out.println("1. Solde initial: " + solde);
            
            // 2. Test dépôt
            String resultDepot = ejb.faireDepot("laplap", 150.0, "Test dépôt");
            System.out.println("2. " + resultDepot);
            
            // 3. Vérifier nouveau solde
            solde = ejb.getSolde("laplap");
            System.out.println("3. Solde après dépôt: " + solde);
            
            // 4. Test retrait
            String resultRetrait = ejb.faireRetrait("laplap", 50.0, "Test retrait");
            System.out.println("4. " + resultRetrait);
            
            // 5. Solde final
            solde = ejb.getSolde("laplap");
            System.out.println("5. Solde final: " + solde);

            // 6. essai virement interne
            String resultVirementInterne = ejb.faireVirement("laplap", "rixrix", 30.0, "Test virement interne");
            System.out.println("6. " + resultVirementInterne);

            // 7. Test récupération des opérations
            System.out.println("7. Liste des opérations:");
            List<Object[]> operations = ejb.getOperations("laplap");
            for (Object[] operation : operations) {
                System.out.println("   - Date: " + operation[0] + 
                                ", Compte: " + operation[1] + 
                                ", Type: " + operation[2] + 
                                ", Montant: " + operation[3] + 
                                ", Détails: " + operation[4]);
            }

            // 8. Vérification du nombre d'opérations
            System.out.println("8. Nombre total d'opérations: " + operations.size());
            
            context.close();
            
        } catch (Exception e) {
            System.out.println("ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}