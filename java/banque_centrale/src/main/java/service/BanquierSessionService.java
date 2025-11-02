package service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import banquier.BanquierRemote;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;

@Service
@SessionScope
public class BanquierSessionService {
    
    private BanquierRemote banquierEjb;
    
    @PostConstruct
    public void init() {
        this.banquierEjb = lookupBanquierEjb();
    }
    
    @PreDestroy
    public void cleanup() {
        logout(); // S'assurer que l'EJB est détruit à la fin de la session HTTP
    }
    
    public boolean login(String identifiant, String motDePasse) {
        try {
            return banquierEjb.create(identifiant, motDePasse);
        } catch (Exception e) {
            throw new RuntimeException("Erreur de connexion: " + e.getMessage(), e);
        }
    }
    
    public void logout() {
        if (banquierEjb != null) {
            try {
                banquierEjb.remove(); // Détruit l'EJB Stateful
            } catch (Exception e) {
                // Log l'erreur mais ne pas bloquer
                System.err.println("Erreur lors du logout: " + e.getMessage());
            }
            banquierEjb = null;
        }
    }
    
    public boolean estConnecte() {
        return banquierEjb != null && banquierEjb.estConnecte();
    }
    
    public banquier.Banquier getBanquier() {
        if (!estConnecte()) {
            throw new IllegalStateException("Aucun banquier connecté");
        }
        return banquierEjb.getBanquierConnecte();
    }
    
    // Méthodes de vérification des permissions
    public boolean aNiveau(String typeCompte, String identifiantClient) {
        if (!estConnecte()) {
            return false;
        }
        return banquierEjb.aNiveau(typeCompte, identifiantClient);
    }
    
    public boolean aRole(String typeCompte, String identifiantClient) {
        if (!estConnecte()) {
            return false;
        }
        return banquierEjb.aRole(typeCompte, identifiantClient);
    }
    
    private BanquierRemote lookupBanquierEjb() {
        try {
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            props.put(Context.PROVIDER_URL, "http-remoting://localhost:8081");
            
            Context context = new InitialContext(props);
            
            return (BanquierRemote) context.lookup(
                "ejb:/banquier-ejb-1.0.0/BanquierBean!banquier.BanquierRemote"
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur de connexion à l'EJB Banquier: " + e.getMessage(), e);
        }
    }
}