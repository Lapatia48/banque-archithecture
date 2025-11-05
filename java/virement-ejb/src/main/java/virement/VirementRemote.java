package virement;

import virement.metier.ValidationVirement;
import virement.metier.Virement;
import jakarta.ejb.Remote;
import java.util.List;
import java.util.Optional;

@Remote
public interface VirementRemote {
    Virement creerVirement(String identifiantSource, String identifiantDestination, Double montant, Double frais, String devise, String details, String createdBy);
    Virement validerVirement(Long idVirement);
    Virement executerVirement(Long idVirement);
    Virement annulerVirement(Long idVirement, String motif);
    Virement refuserVirement(Long idVirement, String motif);
    Virement modifierVirement(Long idVirement, Double nouveauMontant, String nouveauxDetails);
    List<Virement> getVirementsEnAttente();
    List<Virement> getTousVirements();
    Optional<Virement> getVirementById(Long idVirement);

    Double calculerFraisPourVirement(Double montant);
    // Nouvelles méthodes pour ValidationVirement
    ValidationVirement creerValidationVirement(Virement virement);
    List<ValidationVirement> getValidationsByVirement(Long idVirement);
    List<ValidationVirement> getValidationsByUtilisateur(String utilisateur);
}