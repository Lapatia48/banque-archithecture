package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Utilisateurs")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // SERIAL en PostgreSQL
    @Column(name = "id_utilisateur")
    private Long idUtilisateur;

    private String nom;

    @Column(name = "mot_de_passe")
    private String motDePasse;

    @Column(name = "identifiant")
    private String identifiant;

    // ----- Constructeurs -----
    public Utilisateur() {}

    public Utilisateur(String nom, String motDePasse, String identifiant) {
        this.nom = nom;
        this.motDePasse = motDePasse;
        this.identifiant = identifiant;
    }

    // ----- Getters & Setters -----
    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getIdentifiant() {
        return identifiant;
    }
    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }
}
