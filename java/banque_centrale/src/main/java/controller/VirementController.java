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

    @PostMapping("/valider/{id}")
    public String validerVirement(@PathVariable("id") Long idVirement, HttpSession session, Model model) {
        try {
            if (!banquierSessionService.estConnecte()) {
                model.addAttribute("erreur", "Banquier non connecté");
                return "redirect:/login";
            }
            if (!banquierSessionService.aRolePourAction("courant", "validerVirement")) {
                model.addAttribute("error", "Rôle insuffisant pour valider un virement");
                return listVirements(session, model);
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

    @PostMapping("/executer/{id}")
    public String executerVirement(@PathVariable("id") Long idVirement, HttpSession session, Model model) {
        try {
            if (!banquierSessionService.estConnecte()) {
                model.addAttribute("erreur", "Banquier non connecté");
                return "redirect:/login";
            }
            if (!banquierSessionService.aRolePourAction("courant", "executerVirement")) {
                model.addAttribute("error", "Rôle insuffisant pour executer un virement");
                return listVirements(session, model);
            }
            
            VirementRemote ejb = getVirementEJB();
            
            //  VÉRIFICATION LIMITE JOURNALIÈRE avec détails
            Map<String, Object> verification = ejb.verifierLimiteJournaliereAvecDetails(idVirement);
            Boolean limiteRespectee = (Boolean) verification.get("limiteRespectee");
            
            if (!limiteRespectee) {
                // Récupérer tous les détails pour le message d'erreur
                Double limiteJournaliere = (Double) verification.get("limiteJournaliere");
                Double sommeVirementsAujourdhui = (Double) verification.get("sommeVirementsAujourdhui");
                Double montantVirement = (Double) verification.get("montantVirement");
                Double totalApresVirement = (Double) verification.get("totalApresVirement");
                String identifiant = (String) verification.get("identifiant");
                
                String messageErreur = String.format(
                    " Limite journalière dépassée pour %s!%n" +
                    "• Déjà viré aujourd'hui: %,.2f MGA%n" +
                    "• Montant du virement: %,.2f MGA%n" +
                    "• Total après virement: %,.2f MGA%n" +
                    "• Limite autorisée: %,.2f MGA",
                    identifiant, 
                    sommeVirementsAujourdhui, 
                    montantVirement, 
                    totalApresVirement, 
                    limiteJournaliere
                );
                
                model.addAttribute("error", messageErreur);
                return listVirements(session, model);
            }
            
            // ✅ EXÉCUTER le virement si la limite est respectée
            Virement virement = ejb.executerVirement(idVirement);
            
            // Récupérer les détails pour le message de succès
            Double sommeVirementsAujourdhui = (Double) verification.get("sommeVirementsAujourdhui");
            Double montantVirement = (Double) verification.get("montantVirement");
            Double totalApresVirement = sommeVirementsAujourdhui + montantVirement;
            Double limiteJournaliere = (Double) verification.get("limiteJournaliere");
            String identifiant = (String) verification.get("identifiant");
            
            String messageSucces;
            if (limiteJournaliere > 0) {
                messageSucces = String.format(
                    " Virement #%d exécuté avec succès!%n" +
                    "• Client: %s%n" +
                    "• Montant: %,.2f MGA%n" +
                    "• Total viré aujourd'hui: %,.2f / %,.2f MGA",
                    virement.getIdVirement(),
                    identifiant,
                    montantVirement,
                    totalApresVirement,
                    limiteJournaliere
                );
            } else {
                messageSucces = String.format(
                    " Virement #%d exécuté avec succès!%n" +
                    "• Client: %s%n" +
                    "• Montant: %,.2f MGA%n" +
                    "• Total viré aujourd'hui: %,.2f MGA (limite illimitée)",
                    virement.getIdVirement(),
                    identifiant,
                    montantVirement,
                    totalApresVirement
                );
            }
            
            model.addAttribute("message", messageSucces);
            
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
            if (!banquierSessionService.aRolePourAction("courant", "annulerVirement")) {
                model.addAttribute("error", "Rôle insuffisant pour annuler un virement");
                return listVirements(session, model);
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
            if (!banquierSessionService.aRolePourAction("courant", "refuserVirement")) {
                model.addAttribute("error", "Rôle insuffisant pour refuser un virement");
                return listVirements(session, model);
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


    @PostMapping("/annuler-execution/{id}")
    public String annulerExecutionVirement(@PathVariable("id") Long idVirement,
                                        @RequestParam("motif") String motif,
                                        HttpSession session, Model model) {
        try {
            if (!banquierSessionService.estConnecte()) {
                model.addAttribute("erreur", "Banquier non connecté");
                return "redirect:/login";
            }
            if (!banquierSessionService.aRolePourAction("courant", "rollbackVirement")) {
                model.addAttribute("error", "Rôle insuffisant pour roollback un virement");
                return listVirements(session, model);
            }
            
            VirementRemote ejb = getVirementEJB();
            Virement virement = ejb.annulerExecutionVirement(idVirement, motif);
            
            model.addAttribute("message", 
                "Virement #" + virement.getIdVirement() + " annulé avec succès après exécution. " +
                "Montant remboursé: " + (virement.getMontant() + virement.getFraisDeVirement()) + " MGA");
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur annulation exécution: " + e.getMessage());
        }
        
        return listVirements(session, model);
    }
}