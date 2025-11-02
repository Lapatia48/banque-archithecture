package controller;

import entity.Pourcentage;
import service.PourcentageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pourcentage")
public class PourcentageController {

    private final PourcentageService pourcentageService;

    public PourcentageController(PourcentageService pourcentageService) {
        this.pourcentageService = pourcentageService;
    }

    // GET - Afficher le formulaire avec tous les pourcentages
    @GetMapping("/form")
    public String showPourcentageForm(Model model) {
        List<Pourcentage> pourcentages = pourcentageService.getAllPourcentages();
        
        // Grouper par type de compte
        Map<String, List<Pourcentage>> pourcentagesParType = pourcentages.stream()
            .collect(Collectors.groupingBy(Pourcentage::getTypeCompte));
        
        model.addAttribute("pourcentagesParType", pourcentagesParType);
        // model.addAttribute("pourcentagesList", pourcentages); // Backup au cas où
        return "pourcentage";
    }

    // POST - Mettre à jour un pourcentage ET période
    @PostMapping("/update")
    public String updatePourcentage(@RequestParam("typeCompte") String typeCompte,
                                  @RequestParam("anciennePeriode") int anciennePeriode,
                                  @RequestParam("nouvellePeriode") int nouvellePeriode,
                                  @RequestParam("pourcentage") double pourcentage,
                                  Model model) {
        
        System.out.println("Updating - Type: " + typeCompte + 
                         ", Ancienne Periode: " + anciennePeriode + 
                         ", Nouvelle Periode: " + nouvellePeriode + 
                         ", Nouveau Pourcentage: " + pourcentage);
        
        boolean success = pourcentageService.updatePourcentage(typeCompte, anciennePeriode, nouvellePeriode, pourcentage);
        
        if (success) {
            model.addAttribute("message", "Pourcentage et période mis à jour avec succès!");
        } else {
            model.addAttribute("error", "Erreur lors de la mise à jour. Vérifiez que la période n'existe pas déjà.");
        }
        
        // Recharger la liste mise à jour et regrouper
        List<Pourcentage> pourcentages = pourcentageService.getAllPourcentages();
        Map<String, List<Pourcentage>> pourcentagesParType = pourcentages.stream()
            .collect(Collectors.groupingBy(Pourcentage::getTypeCompte));
        
        model.addAttribute("pourcentagesParType", pourcentagesParType);
        model.addAttribute("pourcentagesList", pourcentages);
        
        return "pourcentage";
    }
}