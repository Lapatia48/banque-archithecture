import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import change.ChangeRemote;
import org.wildfly.security.auth.client.AuthenticationConfiguration;
import org.wildfly.security.auth.client.AuthenticationContext;
import org.wildfly.security.auth.client.MatchRule;

public class TestDocker {
    public static void main(String[] args) {
        try {
            // Configuration d'authentification
            AuthenticationConfiguration config = AuthenticationConfiguration.empty()
                .useName("ejbuser")
                .usePassword("ejbpassword123")
                .useRealm("ApplicationRealm")
                .useDefaultProviders();
            
            AuthenticationContext authContext = AuthenticationContext.empty()
                .with(MatchRule.ALL, config);
            
            AuthenticationContext.getContextManager().setGlobalDefault(authContext);
            
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            props.put(Context.PROVIDER_URL, "http-remoting://localhost:8081");
            
            // Options de connexion importantes
            props.put("jboss.naming.client.ejb.context", true);
            props.put("org.jboss.ejb.client.scoped.context", true);
            
            Context context = new InitialContext(props);
            
            ChangeRemote changeEjb = (ChangeRemote) context.lookup(
                "ejb:/change-ejb-1.0.0/ChangeBean!change.ChangeRemote"
            );
            
            System.out.println("✅ CONNEXION RÉUSSIE au EJB Docker!");
            System.out.println("🐳 Conteneur Docker: localhost:8081");
            
            // Test des devises
            System.out.println("\n💱 Test des devises:");
            var devises = changeEjb.getDevisesDisponibles();
            System.out.println("Devises disponibles: " + devises);
            
            // Test conversion
            Double montantEur = 100.0;
            Double enAriary = changeEjb.convertirVersAriary("EUR", montantEur);
            System.out.println("Conversion: " + montantEur + " EUR = " + enAriary + " MGA");
            
            context.close();
            
        } catch (Exception e) {
            System.out.println("❌ ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}