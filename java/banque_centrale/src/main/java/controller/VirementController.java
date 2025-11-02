package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpSession;

import entity.Utilisateur;
import service.BanquierSessionService;
import virement.VirementRemote;
import virement.metier.Virement;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;

@Controller
@RequestMapping("/virement")
public class VirementController {

    private VirementRemote getVirementEJB() {
        try {
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            props.put(Context.PROVIDER_URL, "http-remoting://localhost:8081");
            
            Context context = new InitialContext(props);
            
            return (VirementRemote) context.lookup(
                "ejb:/virement-ejb-1.0.0/VirementBean!virement.VirementRemote"
            );
        } catch (Exception e) {
            throw new RuntimeException("Erreur de connexion à l'EJB Virement: " + e.getMessage());
        }
    }
    
    @Autowired
    private BanquierSessionService banquierSessionService;

    @GetMapping("/list")
    public String listVirements(HttpSession session, Model model) {
        try {
            if (!banquierSessionService.estConnecte()) {
                model.addAttribute("erreur", "Banquier non connecté");
                return "redirect:/login";
            }
            
            VirementRemote ejb = getVirementEJB();
            List<Virement> virementsEnAttente = ejb.getVirementsEnAttente();
            List<Virement> tousVirements = ejb.getTousVirements();
            
            model.addAttribute("virementsEnAttente", virementsEnAttente);
            model.addAttribute("tousVirements", tousVirements);
            model.addAttribute("banquier", banquierSessionService.getBanquier());
            
        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur récupération virements: " + e.getMessage());
        }
        
        return "virement/listeVirements";
    }

    @PostMapping("/creer")
    public String creerVirement(
            @RequestParam("identifiantSource") String identifiantSource,
            @RequestParam("identifiantDest") String identifiantDest,
            @RequestParam("montant") Double montant,
            @RequestParam("devise") String devise,
            @RequestParam(value = "details", required = false) String details,
            HttpSession session, Model model) {
        
        try {
            if (!banquierSessionService.estConnecte()) {
                model.addAttribute("erreur", "Banquier non connecté");
                return "redirect:/login";
            }
            
            String createdBy = banquierSessionService.getBanquier().getIdentifiant();
            VirementRemote ejb = getVirementEJB();
            
            Virement virement = ejb.creerVirement(identifiantSource, identifiantDest, montant, devise, details, createdBy);
            
            model.addAttribute("message", "Virement créé avec succès (ID: " + virement.getIdVirement() + ")");
            model.addAttribute("virement", virement);
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur création virement: " + e.getMessage());
        }
        
        return listVirements(session, model);
    }

    @PostMapping("/valider/{id}")
    public String validerVirement(@PathVariable("id") Long idVirement, HttpSession session, Model model) {
        try {
            if (!banquierSessionService.estConnecte()) {
                model.addAttribute("erreur", "Banquier non connecté");
                return "redirect:/login";
            }
            
            VirementRemote ejb = getVirementEJB();
            Virement virement = ejb.validerVirement(idVirement);
            
            model.addAttribute("message", "Virement validé avec succès");
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur validation virement: " + e.getMessage());
        }
        
        return listVirements(session, model);
    }

// Dans VirementController - ajouter ces méthodes :

    @GetMapping("/details/{id}")
    public String detailsVirement(@PathVariable("id") Long idVirement, HttpSession session, Model model) {
        try {
            
            VirementRemote ejb = getVirementEJB();
            List<Virement> tousVirements = ejb.getTousVirements();
            
            Optional<Virement> virementOpt = tousVirements.stream()
                .filter(v -> v.getIdVirement().equals(idVirement))
                .findFirst();
                
            model.addAttribute("virement", virementOpt.get());
            model.addAttribute("banquier", banquierSessionService.getBanquier());

            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/virement/list";
        }
        
        return "virement/detailsVirement";
    }

    // Méthode pour récupérer les données du virement sans objet complexe
    private Map<String, Object> getVirementData(Long idVirement) {
        // Implémentation directe en JDBC ou via une méthode simplifiée
        // Pour l'instant, retournons null et gérons l'erreur
        return null;
    }

    @PostMapping("/executer/{id}")
    public String executerVirement(@PathVariable("id") Long idVirement, HttpSession session, Model model) {
        try {
            if (!banquierSessionService.estConnecte()) {
                model.addAttribute("erreur", "Banquier non connecté");
                return "redirect:/login";
            }
            
            VirementRemote ejb = getVirementEJB();
            Virement virement = ejb.executerVirement(idVirement);
            
            model.addAttribute("message", "Virement #" + virement.getIdVirement() + " exécuté avec succès");
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur exécution: " + e.getMessage());
        }
        
        return listVirements(session, model);
    }

    @PostMapping("/annuler/{id}")
    public String annulerVirement(@PathVariable("id") Long idVirement, 
                                 @RequestParam("motif") String motif,
                                 HttpSession session, Model model) {
        try {
            if (!banquierSessionService.estConnecte()) {
                model.addAttribute("erreur", "Banquier non connecté");
                return "redirect:/login";
            }
            
            VirementRemote ejb = getVirementEJB();
            Virement virement = ejb.annulerVirement(idVirement, motif);
            
            model.addAttribute("message", "Virement annulé avec succès");
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur annulation virement: " + e.getMessage());
        }
        
        return listVirements(session, model);
    }

    @PostMapping("/refuser/{id}")
    public String refuserVirement(@PathVariable("id") Long idVirement,
                                 @RequestParam("motif") String motif,
                                 HttpSession session, Model model) {
        try {
            if (!banquierSessionService.estConnecte()) {
                model.addAttribute("erreur", "Banquier non connecté");
                return "redirect:/login";
            }
            
            VirementRemote ejb = getVirementEJB();
            Virement virement = ejb.refuserVirement(idVirement, motif);
            
            model.addAttribute("message", "Virement refusé avec succès");
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur refus virement: " + e.getMessage());
        }
        
        return listVirements(session, model);
    }

    @PostMapping("/modifier/{id}")
    public String modifierVirement(@PathVariable("id") Long idVirement,
                                  @RequestParam("nouveauMontant") Double nouveauMontant,
                                  @RequestParam("nouveauxDetails") String nouveauxDetails,
                                  HttpSession session, Model model) {
        try {
            if (!banquierSessionService.estConnecte()) {
                model.addAttribute("erreur", "Banquier non connecté");
                return "redirect:/login";
            }
            
            VirementRemote ejb = getVirementEJB();
            Virement virement = ejb.modifierVirement(idVirement, nouveauMontant, nouveauxDetails);
            
            model.addAttribute("message", "Virement modifié avec succès");
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur modification virement: " + e.getMessage());
        }
        
        return listVirements(session, model);
    }
}