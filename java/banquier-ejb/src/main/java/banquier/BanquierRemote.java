package banquier;

import jakarta.ejb.Remote;

@Remote
public interface BanquierRemote {
    
    boolean create(String identifiant, String motDePasse);
    void remove();
    
    // AJOUT du typeCompte en paramètre
    boolean aNiveau(String typeCompte, String identifiantClient);
    
    // Garde typeCompte
    boolean aRole(String typeCompte, String identifiantBanquier);
    
    Banquier getBanquierConnecte();
    boolean estConnecte();

    // pour le virement
    boolean aRolePourAction(String nomTable, String action);
}