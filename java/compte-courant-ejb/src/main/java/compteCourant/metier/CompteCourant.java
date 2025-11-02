package compteCourant.metier;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class CompteCourant implements Serializable {
    private Long idCompte;
    private String identifiantUtilisateur;
    private String libelle;
    private Double solde;
    private Integer niveau;
    
    // Constructeurs
    public CompteCourant() {}
    
    public CompteCourant(String identifiantUtilisateur, String libelle, Double solde, Integer niveau) {
        this.identifiantUtilisateur = identifiantUtilisateur;
        this.libelle = libelle;
        this.solde = solde;
        this.niveau = niveau;
    }
    
    // Méthodes métier
    public Operation deposer(Double montant, String details, String typeOperation) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        this.solde += montant;
        
        return new Operation(
            this.identifiantUtilisateur,
            "courant",
            typeOperation != null ? typeOperation : "depot",
            montant,
            details != null ? details : "Dépôt compte courant"
        );
    }
    
    public Operation retirer(Double montant, String details, String typeOperation) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        if (montant > solde) {
            throw new IllegalStateException("Solde insuffisant");
        }
        this.solde -= montant;
        
        return new Operation(
            this.identifiantUtilisateur,
            "courant",
            typeOperation != null ? typeOperation : "retrait",
            -montant,
            details != null ? details : "Retrait compte courant"
        );
    }
    
    public boolean soldeSuffisant(Double montant) {
        return this.solde >= montant;
    }
    
    // Getters et Setters
    public Long getIdCompte() { return idCompte; }
    public void setIdCompte(Long idCompte) { this.idCompte = idCompte; }
    
    public String getIdentifiantUtilisateur() { return identifiantUtilisateur; }
    public void setIdentifiantUtilisateur(String identifiantUtilisateur) { this.identifiantUtilisateur = identifiantUtilisateur; }
    
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    
    public Double getSolde() { return solde; }
    public void setSolde(Double solde) { this.solde = solde; }
    
    public Integer getNiveau() { return niveau; }
    public void setNiveau(Integer niveau) { this.niveau = niveau; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompteCourant that = (CompteCourant) o;
        return Objects.equals(idCompte, that.idCompte);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idCompte);
    }
    
    @Override
    public String toString() {
        return "CompteCourant{" +
                "idCompte=" + idCompte +
                ", identifiantUtilisateur='" + identifiantUtilisateur + '\'' +
                ", libelle='" + libelle + '\'' +
                ", solde=" + solde +
                ", niveau=" + niveau +
                '}';
    }
}