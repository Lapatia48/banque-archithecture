package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import entity.*;
import service.*;

import java.util.List;

@Controller
public class DepotController {

    @Autowired
    private PourcentageService pourcentageService;

    private final String API_BASE_URL = "http://localhost:5279";

    @GetMapping("/depot")
    public String compteDepot(HttpSession session, Model model) {
        try {
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
            String identifiant = utilisateur.getIdentifiant();

            // Récupérer le solde du compte dépôt
            String url = API_BASE_URL + "/solde/" + identifiant;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String solde = response.body();

            // Récupérer les pourcentages pour les dépôts
            List<Pourcentage> pourcentagesDepot = pourcentageService.getAllPourcentages().stream()
                    .filter(p -> "depot".equals(p.getTypeCompte()))
                    .toList();
            model.addAttribute("pourcentagesDepot", pourcentagesDepot); // Nouvel attribut
                    
            model.addAttribute("solde", solde);
            model.addAttribute("identifiant", identifiant);

        } catch (Exception e) {
            model.addAttribute("erreur", "Impossible de récupérer le solde: " + e.getMessage());
        }

        return "depot/depot";
    }



    @PostMapping("/faireDepot")
    public String faireDepot(
            @RequestParam("montant") String montant,
            @RequestParam("detail") String detail,
            HttpSession session,
            Model model) {

        try {
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
            String identifiant = utilisateur.getIdentifiant();

            // Construire le body x-www-form-urlencoded
            String body = "identifiant=" + URLEncoder.encode(identifiant, StandardCharsets.UTF_8)
                        + "&montant=" + URLEncoder.encode(montant, StandardCharsets.UTF_8)
                        + "&compte=depot"
                        + "&action=depot"
                        + "&detail=" + URLEncoder.encode(detail, StandardCharsets.UTF_8);

            // Appeler l'API .NET
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/depot"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String message = response.body().replace("\"", "");
            model.addAttribute("message", message);

            // Mettre à jour le solde après le dépôt
            String soldeUrl = API_BASE_URL + "/solde/" + URLEncoder.encode(identifiant, StandardCharsets.UTF_8);
            HttpRequest soldeRequest = HttpRequest.newBuilder()
                    .uri(URI.create(soldeUrl))
                    .GET()
                    .build();
            HttpResponse<String> soldeResponse = client.send(soldeRequest, HttpResponse.BodyHandlers.ofString());
            model.addAttribute("solde", soldeResponse.body());

            // Récupérer les pourcentages pour les dépôts
            List<Pourcentage> pourcentagesDepot = pourcentageService.getAllPourcentages().stream()
                    .filter(p -> "depot".equals(p.getTypeCompte()))
                    .toList();
            model.addAttribute("pourcentagesDepot", pourcentagesDepot); // Nouvel attribut

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du dépôt : " + e.getMessage());
        }

        return "depot/depot";
    }

    @GetMapping("/simulerGain")  // Changé de POST à GET
    public String simulerGain(
            @RequestParam("date") String date,
            @RequestParam("periode") int periode,
            HttpSession session,
            Model model) {

        try {
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
            String identifiant = utilisateur.getIdentifiant();

            // Appeler l'API de simulation (GET)
            String url = API_BASE_URL + "/simulationGain?identifiant=" + URLEncoder.encode(identifiant, StandardCharsets.UTF_8)
                        + "&date=" + URLEncoder.encode(date, StandardCharsets.UTF_8)
                        + "&periode=" + URLEncoder.encode(String.valueOf(periode), StandardCharsets.UTF_8);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String simulation = response.body();

            model.addAttribute("simulation", simulation);
            model.addAttribute("simulationDate", date);
            model.addAttribute("simulationPeriode", periode);

            // Récupérer aussi le solde actuel pour l'affichage
            String soldeUrl = API_BASE_URL + "/solde/" + URLEncoder.encode(identifiant, StandardCharsets.UTF_8);
            HttpRequest soldeRequest = HttpRequest.newBuilder()
                    .uri(URI.create(soldeUrl))
                    .GET()
                    .build();
            HttpResponse<String> soldeResponse = client.send(soldeRequest, HttpResponse.BodyHandlers.ofString());
            model.addAttribute("solde", soldeResponse.body());

            // Récupérer les pourcentages pour les dépôts
            List<Pourcentage> pourcentagesDepot = pourcentageService.getAllPourcentages().stream()
                    .filter(p -> "depot".equals(p.getTypeCompte()))
                    .toList();
            model.addAttribute("pourcentagesDepot", pourcentagesDepot); // Nouvel attribut


        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la simulation : " + e.getMessage());
        }

        return "depot/depot";
    }

    @GetMapping("/listOperationDepot")
    public String listOperationDepot(HttpSession session, Model model) {
        try {
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
            String identifiant = utilisateur.getIdentifiant();

            // Appeler l'API .NET
            String url = API_BASE_URL + "/listOperation/" + identifiant;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String operationsJson = response.body();

            model.addAttribute("operations", operationsJson);

        } catch (Exception e) {
            model.addAttribute("erreur", "Impossible de récupérer les opérations de dépôt: " + e.getMessage());
        }

        return "depot/listOperationDepot";
    }
}