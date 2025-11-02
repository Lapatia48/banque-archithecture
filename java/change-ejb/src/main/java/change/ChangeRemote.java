package change;

import jakarta.ejb.Remote;
import java.util.List;
import java.util.Map;

@Remote
public interface ChangeRemote {
    
    // Convertir un montant d'une devise vers Ariary
    Double convertirVersAriary(String devise, Double montant) throws Exception;
    
    // Obtenir la liste des devises disponibles
    List<String> getDevisesDisponibles();
    
    // Obtenir le taux de change actuel pour une devise
    Double getTauxChange(String devise) throws Exception;
    
    // Vérifier si une devise est valide (dans la période)
    boolean estDeviseValide(String devise);
    
    // Obtenir toutes les informations des devises
    Map<String, Map<String, Object>> getInfoDevises();
}