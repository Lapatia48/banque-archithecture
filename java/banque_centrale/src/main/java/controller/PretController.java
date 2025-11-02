package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import entity.Utilisateur;
import entity.Pourcentage;
import service.PourcentageService;

@Controller
public class PretController {

    private final String API_BASE_URL = "http://localhost:5107";
    private final PourcentageService pourcentageService;

    public PretController(PourcentageService pourcentageService) {
        this.pourcentageService = pourcentageService;
    }

    @GetMapping("/pret")
    public String comptePret(HttpSession session, Model model) {
        try {
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
            String identifiant = utilisateur.getIdentifiant();

            // Récupérer le solde du compte prêt
            String url = API_BASE_URL + "/solde/" + URLEncoder.encode(identifiant, StandardCharsets.UTF_8);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String solde = response.body().replace("\"", "");

            // Récupérer les pourcentages pour les prêts
            List<Pourcentage> pourcentagesPret = pourcentageService.getAllPourcentages().stream()
                    .filter(p -> "pret".equals(p.getTypeCompte()))
                    .toList();

            model.addAttribute("solde", solde);
            model.addAttribute("pourcentagesPret", pourcentagesPret);

        } catch (Exception e) {
            model.addAttribute("erreur", "Impossible de récupérer le solde du prêt: " + e.getMessage());
        }

        return "pret/pret";
    }

    @PostMapping("/demanderPret")
    public String demanderPret(
            @RequestParam("montant") String montant,
            @RequestParam("periode") int periode,
            HttpSession session,
            Model model) {

        try {
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
            String identifiant = utilisateur.getIdentifiant();

            // Construire le body x-www-form-urlencoded
            String body = "identifiant=" + URLEncoder.encode(identifiant, StandardCharsets.UTF_8)
                        + "&montant=" + URLEncoder.encode(montant, StandardCharsets.UTF_8)
                        + "&periode=" + URLEncoder.encode(String.valueOf(periode), StandardCharsets.UTF_8);

            // Appeler l'API .NET
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/demanderPret"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String message = response.body().replace("\"", "");
            model.addAttribute("message", message);

            // Mettre à jour le solde après la demande
            String soldeUrl = API_BASE_URL + "/solde/" + URLEncoder.encode(identifiant, StandardCharsets.UTF_8);
            HttpRequest soldeRequest = HttpRequest.newBuilder()
                    .uri(URI.create(soldeUrl))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> soldeResponse = client.send(soldeRequest, HttpResponse.BodyHandlers.ofString());
            model.addAttribute("solde", soldeResponse.body().replace("\"", ""));

            // Recharger les pourcentages
            List<Pourcentage> pourcentagesPret = pourcentageService.getAllPourcentages().stream()
                    .filter(p -> "pret".equals(p.getTypeCompte()))
                    .toList();
            model.addAttribute("pourcentagesPret", pourcentagesPret);

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la demande de prêt : " + e.getMessage());
            e.printStackTrace(); // Pour debug
        }

        return "pret/pret";
    }

    @PostMapping("/rembourserPret")
    public String rembourserPret(
            @RequestParam("montant") String montant,
            HttpSession session,
            Model model) {

        try {
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
            String identifiant = utilisateur.getIdentifiant();

            // Construire le body x-www-form-urlencoded
            String body = "identifiant=" + URLEncoder.encode(identifiant, StandardCharsets.UTF_8)
                        + "&montant=" + URLEncoder.encode(montant, StandardCharsets.UTF_8);

            // Appeler l'API .NET
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/rembourserPret"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String message = response.body().replace("\"", "");
            model.addAttribute("message", message);

            // Mettre à jour le solde après le remboursement
            String soldeUrl = API_BASE_URL + "/solde/" + URLEncoder.encode(identifiant, StandardCharsets.UTF_8);
            HttpRequest soldeRequest = HttpRequest.newBuilder()
                    .uri(URI.create(soldeUrl))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> soldeResponse = client.send(soldeRequest, HttpResponse.BodyHandlers.ofString());
            model.addAttribute("solde", soldeResponse.body().replace("\"", ""));

            // Recharger les pourcentages
            List<Pourcentage> pourcentagesPret = pourcentageService.getAllPourcentages().stream()
                    .filter(p -> "pret".equals(p.getTypeCompte()))
                    .toList();
            model.addAttribute("pourcentagesPret", pourcentagesPret);

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du remboursement : " + e.getMessage());
            e.printStackTrace(); // Pour debug
        }

        return "pret/pret";
    }

    @GetMapping("/listOperationPret")
    public String listOperationPret(HttpSession session, Model model) {
        try {
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
            String identifiant = utilisateur.getIdentifiant();

            // Appeler l'API .NET
            String url = API_BASE_URL + "/listOperation/" + URLEncoder.encode(identifiant, StandardCharsets.UTF_8);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String operationsJson = response.body();

            model.addAttribute("operations", operationsJson);

        } catch (Exception e) {
            model.addAttribute("erreur", "Impossible de récupérer les opérations de prêt: " + e.getMessage());
        }

        return "pret/listOperationPret";
    }

    @PostMapping("/simulerPret")
    public String simulerPret(
            @RequestParam("date") String date,
            @RequestParam("periode") int periode,
            HttpSession session,
            Model model) {

        try {
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
            String identifiant = utilisateur.getIdentifiant();

            // Appeler l'API de simulation
            String url = API_BASE_URL + "/simulationDette?identifiant=" + URLEncoder.encode(identifiant, StandardCharsets.UTF_8)
                        + "&date=" + URLEncoder.encode(date, StandardCharsets.UTF_8)
                        + "&periode=" + URLEncoder.encode(String.valueOf(periode), StandardCharsets.UTF_8);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String simulation = response.body().replace("\"", "");

            model.addAttribute("simulation", simulation);
            model.addAttribute("simulationDate", date);
            model.addAttribute("simulationPeriode", periode);

            // Recharger les données
            String soldeUrl = API_BASE_URL + "/solde/" + URLEncoder.encode(identifiant, StandardCharsets.UTF_8);
            HttpRequest soldeRequest = HttpRequest.newBuilder()
                    .uri(URI.create(soldeUrl))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> soldeResponse = client.send(soldeRequest, HttpResponse.BodyHandlers.ofString());
            model.addAttribute("solde", soldeResponse.body().replace("\"", ""));

            List<Pourcentage> pourcentagesPret = pourcentageService.getAllPourcentages().stream()
                    .filter(p -> "pret".equals(p.getTypeCompte()))
                    .toList();
            model.addAttribute("pourcentagesPret", pourcentagesPret);

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la simulation : " + e.getMessage());
        }

        return "pret/pret";
    }
}