package banquier;

import jakarta.ejb.Stateful;
import jakarta.ejb.Remove;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.io.Serializable;
import java.util.List;

@Stateful
public class BanquierBean implements BanquierRemote, Serializable {
    
    @PersistenceContext(unitName = "banqueBanquier")
    private EntityManager em;
    
    private Banquier banquierConnecte = null;
    private boolean sessionActive = false;
    
    
    @PostConstruct
    public void init() {
        this.sessionActive = true;
    }
    
    @PreDestroy
    public void cleanup() {
        this.banquierConnecte = null;
        this.sessionActive = false;
    }

    @Override
    @Remove
    public void remove() {
        this.banquierConnecte = null;
        this.sessionActive = false;
    }
    
    // code metier
  
    
    @Override
    public boolean create(String identifiant, String motDePasse) {
        if (!sessionActive) {
            throw new IllegalStateException("Session EJB non active");
        }
        
        try {
            String sql = "SELECT ub.id_banquier, ub.identifiant, ub.nom, ub.mot_de_passe, " +
                        "ub.id_direction, ub.role, d.niveau " +
                        "FROM Utilisateur_Banquier ub " +
                        "JOIN Direction d ON ub.id_direction = d.id_direction " +
                        "WHERE ub.identifiant = ?1 AND ub.mot_de_passe = ?2";
            
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, identifiant);
            query.setParameter(2, motDePasse);
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            
            if (!results.isEmpty()) {
                Object[] row = results.get(0);
                
                this.banquierConnecte = new Banquier(
                    (String) row[1],  // identifiant
                    (String) row[2],  // nom
                    (String) row[3],  // mot_de_passe
                    ((Number) row[4]).intValue(),  // id_direction
                    ((Number) row[5]).intValue(),  // role
                    ((Number) row[6]).intValue()   // niveau
                );
                
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la connexion: " + e.getMessage());
        }
    }
    
    @Override
    public boolean aNiveau(String typeCompte, String identifiantClient) {
        if (!estConnecte()) {
            return false;
        }
        
        try {
            String sql = "SELECT niveau FROM Comptes " +
                        "WHERE identifiant = ?1 AND type_compte = ?2";
            
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, identifiantClient);
            query.setParameter(2, typeCompte);
            
            @SuppressWarnings("unchecked")
            List<Integer> niveaux = query.getResultList();
            
            if (niveaux.isEmpty()) {
                return false;
            }
            
            int niveauRequis = niveaux.get(0);
            int niveauBanquier = banquierConnecte.getNiveau();
            
            return niveauBanquier >= niveauRequis;
            
        } catch (Exception e) {
            System.out.println("Erreur aNiveau: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean aRole(String typeCompte, String identifiantClient) {
        if (!estConnecte()) {
            return false;
        }
        
        try {
            if (!aNiveau(typeCompte, identifiantClient)) {
                return false;
            }
            
            String sql = "SELECT ar.role_necessaire FROM ActionRole ar " +
                        "WHERE ar.nom_table = ?1 AND ar.action = 'consultation'";
            
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, typeCompte.toLowerCase());
            
            @SuppressWarnings("unchecked")
            List<Integer> roles = query.getResultList();
            
            if (roles.isEmpty()) {
                return true;
            }
            
            int roleNecessaire = roles.get(0);
            int roleBanquier = banquierConnecte.getRole();
            
            return roleBanquier >= roleNecessaire;
            
        } catch (Exception e) {
            System.out.println("Erreur vérification rôle: " + e.getMessage());
            return false;
        }
    }    
    
    @Override
    public Banquier getBanquierConnecte() {
        if (!estConnecte()) {
            throw new IllegalStateException("Aucun banquier connecté dans cette session EJB");
        }
        return this.banquierConnecte;
    }
    
    @Override
    public boolean estConnecte() {
        return this.sessionActive && this.banquierConnecte != null;
    }
    

    //code utilistaire

    public String getEtatSession() {
        return "EJB Stateful [sessionActive=" + sessionActive + 
               ", banquier=" + (banquierConnecte != null ? banquierConnecte.getIdentifiant() : "null") + 
               ", hashCode=" + this.hashCode() + "]";
    }
}