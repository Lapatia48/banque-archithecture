package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

import entity.*;
import service.*;
import virement.VirementRemote;

import java.util.List;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import javax.naming.Context;
import javax.naming.InitialContext;
import compteCourant.CompteCourantRemote;

// Import pour les appels HTTP simples
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class CompteCourantController {

    private CompteCourantRemote getEJB() {
        try {
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            props.put(Context.PROVIDER_URL, "http-remoting://localhost:8081");
            
            Context context = new InitialContext(props);
            
            return (CompteCourantRemote) context.lookup(
                "ejb:/compte-courant-ejb-1.0.0/CompteCourantBean!compteCourant.CompteCourantRemote"
            );
        } catch (Exception e) {
            throw new RuntimeException("Erreur de connexion à l'EJB: " + e.getMessage(), e);
        }
    }

    @Autowired
    private UtilisateurService utilisateurService;
    
    @Autowired
    private BanquierSessionService banquierSessionService;

    @Autowired
    private ChangeSessionService changeSessionService;

    // Méthode pour appeler le WebService REST sans dépendance supplémentaire
    private List<String> getDevisesWebService() {
        try {
            URL url = new URL("http://localhost:2222/change-service/api/change/devises");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int status = connection.getResponseCode();
            
            if (status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                
                in.close();
                connection.disconnect();
                
                // Parser la réponse JSON
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(content.toString(), List.class);
                
            } else {
                System.err.println("Erreur WebService devises - Status: " + status);
                return new ArrayList<>();
            }
            
        } catch (Exception e) {
            System.err.println("Erreur appel WebService devises: " + e.getMessage());
            return new ArrayList<>(); // Retourne liste vide en cas d'erreur
        }
    }

    // Méthode pour convertir via WebService REST (version robuste)
    private Double convertirViaWebService(String devise, Double montant) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://localhost:2222/change-service/api/change/convertir");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            // Préparer le body JSON
            String jsonInputString = "{\"devise\":\"" + devise + "\",\"montant\":" + montant + "}";
            
            // Envoyer la requête
            try (var outputStream = connection.getOutputStream()) {
                outputStream.write(jsonInputString.getBytes("UTF-8"));
            }
            
            int status = connection.getResponseCode();
            
            if (status == 200) {
                String responseContent;
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    responseContent = in.lines().reduce("", (acc, line) -> acc + line);
                }
                
                System.out.println("Réponse WebService brute: '" + responseContent + "'");
                
                // Nettoyer la réponse
                responseContent = responseContent.trim();
                
                // Essayer différents formats de réponse
                try {
                    // Format 1: Nombre direct (4500.0)
                    return Double.parseDouble(responseContent);
                } catch (NumberFormatException e1) {
                    try {
                        // Format 2: JSON simple {"montant_converti": 4500.0}
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, Object> jsonResponse = mapper.readValue(responseContent, Map.class);
                        
                        // Chercher différentes clés possibles
                        String[] possibleKeys = {"montant_converti", "resultat", "montant", "value"};
                        for (String key : possibleKeys) {
                            if (jsonResponse.containsKey(key)) {
                                Object value = jsonResponse.get(key);
                                if (value instanceof Number) {
                                    return ((Number) value).doubleValue();
                                }
                            }
                        }
                        
                        throw new RuntimeException("Aucune clé numérique trouvée dans la réponse: " + jsonResponse);
                        
                    } catch (Exception e2) {
                        throw new RuntimeException("Format de réponse non reconnu: '" + responseContent + "'");
                    }
                }
                
            } else {
                String errorMessage = "Erreur HTTP " + status;
                try (BufferedReader errorIn = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    String errorContent = errorIn.lines().reduce("", (acc, line) -> acc + line);
                    if (!errorContent.isEmpty()) {
                        errorMessage += " - " + errorContent;
                    }
                } catch (Exception e) {
                    // Ignorer si pas de stream d'erreur
                }
                throw new RuntimeException(errorMessage);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur conversion WebService: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @GetMapping("/getBanquier")
    public String getBanquier(Model model, HttpSession session) {
        try {
            if (!banquierSessionService.estConnecte()) {
                model.addAttribute("erreur", "Aucun banquier connecté");
                return "redirect:/login";
            }
            
            banquier.Banquier banquier = banquierSessionService.getBanquier();
            model.addAttribute("banquier", banquier);
            return "banquier";
            
        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur lors de la récupération du profil: " + e.getMessage());
            return "redirect:/accueil";
        }
    }

    private boolean verifierSecurite(HttpSession session, Model model, String typeCompte) {
        try {
            if (!banquierSessionService.estConnecte()) {
                model.addAttribute("erreur", "Banquier non connecté");
                return false;
            }
            
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
            if (utilisateur == null) {
                model.addAttribute("erreur", "Client non sélectionné");
                return false;
            }
            
            if (!banquierSessionService.aNiveau(typeCompte, utilisateur.getIdentifiant())) {
                model.addAttribute("erreur", "Niveau insuffisant pour accéder à ce compte");
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur de sécurité: " + e.getMessage());
            return false;
        }
    }

    @GetMapping("/compte-courant")
    public String compteCourant(HttpSession session, Model model) {
        try {
            if (!verifierSecurite(session, model, "courant")) {
                return "accueil";
            }
            
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
            String identifiant = utilisateur.getIdentifiant();

            // Appeler l'EJB pour récupérer le solde
            CompteCourantRemote ejb = getEJB();
            Double solde = ejb.getSolde(identifiant);

            // Liste utilisateurs pour virement
            List<Utilisateur> utilisateurs = utilisateurService.getAllUtilisateurs().stream()
                    .filter(u -> !u.getIdentifiant().equals(identifiant))
                    .toList();
            
            // ✅ COMBINER les devises EJB et WebService dans une seule liste
            List<String> devisesEjb = changeSessionService.getDevisesDisponibles();
            List<String> devisesWebService = getDevisesWebService();
            
            // Fusionner les deux listes
            List<String> toutesDevises = new ArrayList<>();
            toutesDevises.addAll(devisesEjb);
            toutesDevises.addAll(devisesWebService);
            
            // Ajouter les attributs au modèle
            model.addAttribute("utilisateurs", utilisateurs);
            model.addAttribute("solde", solde);
            model.addAttribute("banquier", banquierSessionService.getBanquier());
            model.addAttribute("utilisateur", utilisateur);
            model.addAttribute("devises", toutesDevises); // ✅ UNE SEULE liste combinée

        } catch (Exception e) {
            model.addAttribute("erreur", "Impossible de récupérer le solde: " + e.getMessage());
        }

        return "compte-courant/compteCourant";
    }

    @PostMapping("/depot-courant")
    public String faireDepotCourant(
            @RequestParam("montant") Double montant,
            @RequestParam("devise") String devise,
            @RequestParam(value = "details", required = false) String details,
            HttpSession session, Model model) {
        
        try {
            if (!verifierSecurite(session, model, "courant") || 
                !banquierSessionService.aRole("courant", ((Utilisateur) session.getAttribute("utilisateur")).getIdentifiant())) {
                model.addAttribute("error", "Rôle insuffisant pour effectuer un dépôt");
                return compteCourant(session, model);
            }
            
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
            String identifiant = utilisateur.getIdentifiant();

            Double montantEnAriary;
            String sourceConversion;
            
            // ✅ DÉTECTER automatiquement si c'est une devise EJB ou WebService
            List<String> devisesEjb = changeSessionService.getDevisesDisponibles();
            
            if (devisesEjb.contains(devise)) {
                // Conversion via EJB
                montantEnAriary = changeSessionService.convertirVersAriary(devise, montant);
                sourceConversion = "EJB";
            } else {
                // Conversion via WebService
                montantEnAriary = convertirViaWebService(devise, montant);
                sourceConversion = "WS";
            }

            // Appeler l'EJB pour faire le dépôt avec le montant converti
            CompteCourantRemote ejb = getEJB();
            String resultat = ejb.faireDepot(identifiant, montantEnAriary, 
                "Dépôt (" + sourceConversion + ") de " + montant + " " + devise + " (converti en " + montantEnAriary + " MGA) par " + 
                banquierSessionService.getBanquier().getIdentifiant() + 
                (details != null ? ": " + details : ""));
            
            model.addAttribute("message", resultat);

            // Mettre à jour le solde
            Double nouveauSolde = ejb.getSolde(identifiant);
            model.addAttribute("solde", nouveauSolde);

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du dépôt: " + e.getMessage());
        }

        return compteCourant(session, model);
    }

    @PostMapping("/retrait-courant")
    public String faireRetraitCourant(
            @RequestParam("montant") Double montant,
            @RequestParam("devise") String devise,
            @RequestParam(value = "details", required = false) String details,
            HttpSession session, Model model) {
        
        try {
            if (!verifierSecurite(session, model, "courant") || 
                !banquierSessionService.aRole("courant", ((Utilisateur) session.getAttribute("utilisateur")).getIdentifiant())) {
                model.addAttribute("error", "Rôle insuffisant pour effectuer un retrait");
                return compteCourant(session, model);
            }
            
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
            String identifiant = utilisateur.getIdentifiant();

            Double montantEnAriary;
            String sourceConversion;
            
            // ✅ DÉTECTER automatiquement si c'est une devise EJB ou WebService
            List<String> devisesEjb = changeSessionService.getDevisesDisponibles();
            
            if (devisesEjb.contains(devise)) {
                // Conversion via EJB
                montantEnAriary = changeSessionService.convertirVersAriary(devise, montant);
                sourceConversion = "EJB";
            } else {
                // Conversion via WebService
                montantEnAriary = convertirViaWebService(devise, montant);
                sourceConversion = "WS";
            }

            // Vérifier solde (en MGA)
            Double solde = getEJB().getSolde(identifiant);
            if (solde < montantEnAriary) {
                model.addAttribute("error", "Solde insuffisant pour le retrait de " + montant + " " + devise);
                return compteCourant(session, model);
            }

            // Appeler l'EJB pour faire le retrait avec le montant converti
            CompteCourantRemote ejb = getEJB();
            String resultat = ejb.faireRetrait(identifiant, montantEnAriary, 
                "Retrait (" + sourceConversion + ") de " + montant + " " + devise + " (converti en " + montantEnAriary + " MGA) par " + 
                banquierSessionService.getBanquier().getIdentifiant() + 
                (details != null ? ": " + details : ""));
            
            model.addAttribute("message", resultat);

            // Mettre à jour le solde
            Double nouveauSolde = ejb.getSolde(identifiant);
            model.addAttribute("solde", nouveauSolde);

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du retrait: " + e.getMessage());
        }

        return compteCourant(session, model);
    }

    @PostMapping("/virement-courant")
    public String creerVirementCourant(
            @RequestParam("identifiantDest") String identifiantDest,
            @RequestParam("montant") Double montant,
            @RequestParam("devise") String devise,
            @RequestParam(value = "details", required = false) String details,
            HttpSession session, Model model) {
        
        try {
            if (!banquierSessionService.aRolePourAction("courant", "creerVirement")) {
                model.addAttribute("error", "Rôle insuffisant pour effectuer un virement");
                return compteCourant(session, model);
            }
            
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
            String identifiantSource = utilisateur.getIdentifiant();

            Double montantEnAriary;
            String sourceConversion;
            Double fraisAriary = 0.0;
            
            // ✅ DÉTECTER automatiquement si c'est une devise EJB ou WebService
            List<String> devisesEjb = changeSessionService.getDevisesDisponibles();
            VirementRemote virementEjb = getVirementEJB();
            
            if (devisesEjb.contains(devise)) {
                // Conversion via EJB
                montantEnAriary = changeSessionService.convertirVersAriary(devise, montant);
                sourceConversion = "EJB";

                // frais pour montant en ariary
                fraisAriary = virementEjb.calculerFraisPourVirement(montantEnAriary);

            } else {
                // Conversion via WebService
                montantEnAriary = convertirViaWebService(devise, montant);
                sourceConversion = "WS";

                // frais pour montant en ariary
                fraisAriary = virementEjb.calculerFraisPourVirement(montantEnAriary);

            }

            // Vérifier solde (en MGA) - On garde cette vérification
            Double soldeSource = getEJB().getSolde(identifiantSource);
            if (soldeSource < montantEnAriary) {
                model.addAttribute("error", "Solde insuffisant pour le virement de " + montant + " " + devise);
                return compteCourant(session, model);
            }

            // ✅ NOUVEAU : Créer le virement via l'EJB Virement au lieu d'exécuter immédiatement
            String createdBy = banquierSessionService.getBanquier().getIdentifiant();
            
            virement.metier.Virement virement = virementEjb.creerVirement(
                identifiantSource, 
                identifiantDest, 
                montantEnAriary,
                fraisAriary, // ✅ FRAIS CALCULÉ
                "MGA", // Toujours stocker en MGA
                "Virement (" + sourceConversion + ") de " + montant + " " + devise + 
                " (converti en " + montantEnAriary + " MGA, frais: " + fraisAriary + " MGA) par " + createdBy + 
                (details != null ? ": " + details : ""),
                createdBy
            );

            model.addAttribute("message", "Virement créé avec succès (ID: " + virement.getIdVirement() + ") - En attente de validation");
            
            // Récupérer le solde actuel depuis la base de données
            Double soldeActuel = getEJB().getSolde(identifiantSource);
            model.addAttribute("solde", soldeActuel);

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la création du virement: " + e.getMessage());
        }

        return compteCourant(session, model);
    }

    // Ajouter cette méthode pour obtenir l'EJB Virement
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


    @GetMapping("/operations-courant")
    public String listOperationsCourant(HttpSession session, Model model) {
        try {
            if (!verifierSecurite(session, model, "courant")) {
                return "redirect:/accueil";
            }
            
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
            String identifiant = utilisateur.getIdentifiant();
            
            CompteCourantRemote ejb = getEJB();
            List<Object[]> operations = ejb.getOperations(identifiant);
            
            model.addAttribute("operations", operations);
            model.addAttribute("banquier", banquierSessionService.getBanquier());
            model.addAttribute("utilisateur", utilisateur);

        } catch (Exception e) {
            model.addAttribute("erreur", "Impossible de récupérer les opérations: " + e.getMessage());
        }
        return "compte-courant/listOperationsCourant";
    }
}