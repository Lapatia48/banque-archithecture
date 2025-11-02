package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

// import pour webapi
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

// import pour ejb
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import compteCourant.CompteCourantRemote;

import org.springframework.beans.factory.annotation.Autowired;

import entity.*;
import service.*;

@Controller
public class CentralController {

    @Autowired
    private PourcentageService pourcentageService;

    private final String API_DEPOT_URL = "http://localhost:5279";
    private final String API_PRET_URL = "http://localhost:5107";
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

    @GetMapping("/getBanquieer")
    public String getBanquieer(){return ("central/banquier");}
    @GetMapping("/retourBanquieer")
    public String retourBanquieer(){return ("redirect:/entrer");}


    @GetMapping("/central")
    public String vueCentrale(HttpSession session, Model model) {
        try {
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
            String identifiant = utilisateur.getIdentifiant();

            // Récupérer les soldes des 3 comptes
            double soldeDepot = getSoldeCompte(identifiant, "depot", API_DEPOT_URL);
            double soldePret = getSoldeCompte(identifiant, "pret", API_PRET_URL);
            
            CompteCourantRemote ejb = getEJB();
            double soldeCourant = ejb.getSolde(identifiant);

            // Calculer la fortune totale
            double fortuneTotale = (soldeDepot + soldeCourant) + soldePret; // soldePret est négatif

            // Récupérer tous les pourcentages
            List<Pourcentage> pourcentagesDepot = pourcentageService.getAllPourcentages().stream()
                    .filter(p -> "depot".equals(p.getTypeCompte()))
                    .toList();
            
            List<Pourcentage> pourcentagesPret = pourcentageService.getAllPourcentages().stream()
                    .filter(p -> "pret".equals(p.getTypeCompte()))
                    .toList();

            model.addAttribute("soldeDepot", soldeDepot);
            model.addAttribute("soldePret", soldePret);
            model.addAttribute("soldeCourant", soldeCourant);
            model.addAttribute("fortuneTotale", fortuneTotale);
            model.addAttribute("pourcentagesDepot", pourcentagesDepot);
            model.addAttribute("pourcentagesPret", pourcentagesPret);
            model.addAttribute("identifiant", identifiant);

        } catch (Exception e) {
            model.addAttribute("erreur", "Impossible de récupérer les données: " + e.getMessage());
        }

        return "central/central";
    }

    @PostMapping("/simulerFortune")
    public String simulerFortune(
            @RequestParam("date") String date,
            @RequestParam("periodeDepot") int periodeDepot,
            @RequestParam("periodePret") int periodePret,
            HttpSession session,
            Model model) {

        try {
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
            String identifiant = utilisateur.getIdentifiant();

            // Récupérer les soldes actuels
            double soldeDepot = getSoldeCompte(identifiant, "depot", API_DEPOT_URL);
            double soldePret = getSoldeCompte(identifiant, "pret", API_PRET_URL);
            
            CompteCourantRemote ejb = getEJB();
            double soldeCourant = ejb.getSolde(identifiant);

            // Simuler les gains du dépôt
            double depotFutur = simulerGain(identifiant, date, periodeDepot, API_DEPOT_URL);
            
            // Simuler la dette du prêt
            double pretFutur = simulerDette(identifiant, date, periodePret, API_PRET_URL);

            // Calculer la fortune future
            double fortuneFuture = (depotFutur + soldeCourant) + pretFutur; // pretFutur est négatif

            // Récupérer les pourcentages pour l'affichage
            List<Pourcentage> pourcentagesDepot = pourcentageService.getAllPourcentages().stream()
                    .filter(p -> "depot".equals(p.getTypeCompte()))
                    .toList();
            
            List<Pourcentage> pourcentagesPret = pourcentageService.getAllPourcentages().stream()
                    .filter(p -> "pret".equals(p.getTypeCompte()))
                    .toList();

            model.addAttribute("soldeDepot", soldeDepot);
            model.addAttribute("soldePret", soldePret);
            model.addAttribute("soldeCourant", soldeCourant);
            model.addAttribute("fortuneTotale", (soldeDepot + soldeCourant) + soldePret);
            
            model.addAttribute("depotFutur", depotFutur);
            model.addAttribute("pretFutur", pretFutur);
            model.addAttribute("fortuneFuture", fortuneFuture);
            model.addAttribute("simulationDate", date);
            model.addAttribute("periodeDepot", periodeDepot);
            model.addAttribute("periodePret", periodePret);
            
            model.addAttribute("pourcentagesDepot", pourcentagesDepot);
            model.addAttribute("pourcentagesPret", pourcentagesPret);

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la simulation : " + e.getMessage());
        }

        return "central/central";
    }

    private double getSoldeCompte(String identifiant, String typeCompte, String apiUrl) throws Exception {
        String url = apiUrl + "/solde/" + identifiant;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return Double.parseDouble(response.body());
    }

    private double simulerGain(String identifiant, String date, int periode, String apiUrl) throws Exception {
        String url = apiUrl + "/simulationGain?identifiant=" + URLEncoder.encode(identifiant, StandardCharsets.UTF_8)
                    + "&date=" + URLEncoder.encode(date, StandardCharsets.UTF_8)
                    + "&periode=" + URLEncoder.encode(String.valueOf(periode), StandardCharsets.UTF_8);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return Double.parseDouble(response.body());
    }

    private double simulerDette(String identifiant, String date, int periode, String apiUrl) throws Exception {
        String url = apiUrl + "/simulationDette?identifiant=" + URLEncoder.encode(identifiant, StandardCharsets.UTF_8)
                    + "&date=" + URLEncoder.encode(date, StandardCharsets.UTF_8)
                    + "&periode=" + URLEncoder.encode(String.valueOf(periode), StandardCharsets.UTF_8);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return Double.parseDouble(response.body());
    }
}