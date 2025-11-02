package banquier;

import java.io.Serializable;

public class Banquier implements Serializable {
    private String identifiant;
    private String nom;
    private String motDePasse;
    private Integer idDirection;
    private Integer role;
    private Integer niveau;
    
    // Constructeurs
    public Banquier() {}
    
    public Banquier(String identifiant, String nom, String motDePasse, Integer idDirection, Integer role) {
        this.identifiant = identifiant;
        this.nom = nom;
        this.motDePasse = motDePasse;
        this.idDirection = idDirection;
        this.role = role;
    }
    
    public Banquier(String identifiant, String nom, String motDePasse, Integer idDirection, Integer role, Integer niveau) {
        this.identifiant = identifiant;
        this.nom = nom;
        this.motDePasse = motDePasse;
        this.idDirection = idDirection;
        this.role = role;
        this.niveau = niveau;
    }
    
    // Getters et Setters
    public String getIdentifiant() { return identifiant; }
    public void setIdentifiant(String identifiant) { this.identifiant = identifiant; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    
    public Integer getIdDirection() { return idDirection; }
    public void setIdDirection(Integer idDirection) { this.idDirection = idDirection; }
    
    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }
    
    public Integer getNiveau() { return niveau; }
    public void setNiveau(Integer niveau) { this.niveau = niveau; }
    
    @Override
    public String toString() {
        return "Banquier{" +
                "identifiant='" + identifiant + '\'' +
                ", nom='" + nom + '\'' +
                ", idDirection=" + idDirection +
                ", role=" + role +
                ", niveau=" + niveau +
                '}';
    }
}