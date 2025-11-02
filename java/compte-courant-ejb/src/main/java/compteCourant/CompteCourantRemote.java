package compteCourant;

import compteCourant.metier.CompteCourant;
import compteCourant.metier.Virement;
import compteCourant.metier.Operation;
import jakarta.ejb.Remote;
import java.util.List;
import java.util.Optional;

@Remote
public interface CompteCourantRemote {
    
    // Méthodes existantes (conservées pour compatibilité)
    Double getSolde(String identifiant);
    String faireDepot(String identifiant, Double montant, String details);
    String faireDepotVirement(String identifiant, Double montant, String details);
    String faireRetrait(String identifiant, Double montant, String details);
    String faireRetraitVirement(String identifiant, Double montant, String details);
    List<Object[]> getOperations(String identifiant);
    String faireVirement(String identifiantSource, String identifiantDest, Double montant, String details);
    
    // Nouvelles méthodes orientées objet
    Optional<CompteCourant> getCompte(String identifiant);
    Virement creerVirement(String identifiantSource, String identifiantDest, 
                          Double montant, String devise, String details);
    List<Operation> getOperationsObjet(String identifiant);
}