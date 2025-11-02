package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import entity.*;
import repository.*;
import service.*;


import org.springframework.stereotype.Controller;


@Controller
public class MainController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private BanquierSessionService banquierSessionService;


    @GetMapping("/entrer")
    public String hello(Model model){
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        banquierSessionService.logout();
        session.invalidate(); 
        return "login"; 
    }

    @GetMapping("/accueil")
    public String accueil(Model model) {
        // Vérifier qu'un banquier est connecté
        if (!banquierSessionService.estConnecte()) {
            model.addAttribute("erreur", "Veuillez vous connecter en tant que banquier");
            return "login";
        }
        
        // Ajouter les infos du banquier au modèle
        model.addAttribute("banquier", banquierSessionService.getBanquier());
        return "accueil"; 
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam("banquierIdentifiant") String banquierIdentifiant,
                        @RequestParam("banquierPassword") String banquierPassword,
                        Model model,
                        HttpSession session) {

        Utilisateur utilisateur = utilisateurService.login(username, password);
        boolean banquierConnecte = banquierSessionService.login(banquierIdentifiant, banquierPassword);

        if (utilisateur != null && banquierConnecte) {

            session.setAttribute("utilisateur", utilisateur); // Stocker dans la session
            return "accueil"; 
        } else {
            // Login échoué → message d’erreur
            model.addAttribute("erreur", "Nom d’utilisateur ou mot de passe incorrect");
            return "login"; 
        }
    }



}


