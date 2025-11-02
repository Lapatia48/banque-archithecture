package service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import change.ChangeRemote;

import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;

@Service
@SessionScope
public class ChangeSessionService {
    
    private ChangeRemote changeEjb;
    
    @PostConstruct
    public void init() {
        this.changeEjb = lookupChangeEjb();
    }
    
    public Double convertirVersAriary(String devise, Double montant) throws Exception {
        return changeEjb.convertirVersAriary(devise, montant);
    }
    
    public List<String> getDevisesDisponibles() {
        return changeEjb.getDevisesDisponibles();
    }
    
    // Version pour WildFly local (port 8081) - COMMENTÉE

    private ChangeRemote lookupChangeEjb() {
        try {
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            props.put(Context.PROVIDER_URL, "http-remoting://localhost:8081");
            
            Context context = new InitialContext(props);
            
            return (ChangeRemote) context.lookup(
                "ejb:/change-ejb-1.0.0/ChangeBean!change.ChangeRemote"
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur de connexion à l'EJB Change: " + e.getMessage(), e);
        }
    }
    
    // private ChangeRemote lookupChangeEjb() {
    //     try {
    //         Properties props = new Properties();
    //         props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
    //         props.put(Context.PROVIDER_URL, "http-remoting://localhost:2222");
            
    //         // AJOUTER L'AUTHENTIFICATION - c'est crucial !
    //         props.put(Context.SECURITY_PRINCIPAL, "admin");
    //         props.put(Context.SECURITY_CREDENTIALS, "admin123");
            
    //         // Options de connexion importantes
    //         props.put("jboss.naming.client.ejb.context", true);
    //         props.put("org.jboss.ejb.client.scoped.context", true);
            
    //         Context context = new InitialContext(props);
            
    //         return (ChangeRemote) context.lookup(
    //             "ejb:/change-ejb-1.0.0/ChangeBean!change.ChangeRemote"
    //         );
            
    //     } catch (Exception e) {
    //         throw new RuntimeException("Erreur de connexion à l'EJB Change: " + e.getMessage(), e);
    //     }
    // }
}