package service;

import entity.Pourcentage;
import repository.PourcentageRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PourcentageService {

    private final PourcentageRepository pourcentageRepository;

    public PourcentageService(PourcentageRepository pourcentageRepository) {
        this.pourcentageRepository = pourcentageRepository;
    }

    // Méthode pour mettre à jour un pourcentage ET période
    public boolean updatePourcentage(String typeCompte, int anciennePeriode, int nouvellePeriode, double nouveauPourcentage) {
        try {
            int rowsUpdated = pourcentageRepository.updatePourcentage(typeCompte, anciennePeriode, nouvellePeriode, nouveauPourcentage);
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Méthode pour récupérer tous les pourcentages TRIÉS
    public List<Pourcentage> getAllPourcentages() {
        return pourcentageRepository.findAllOrdered();
    }

}