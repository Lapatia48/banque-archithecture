package virement.metier;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ValidationVirement implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long idObjet; // Référence à id_virement
    private String utilisateur; // identifiant source
    private LocalDateTime dateValidation;
    
    // Constructeurs
    public ValidationVirement() {}
    
    public ValidationVirement(Long idObjet, String utilisateur) {
        this.idObjet = idObjet;
        this.utilisateur = utilisateur;
        this.dateValidation = LocalDateTime.now();
    }
    
    // Méthode pour créer un ValidationVirement à partir d'un Virement
    public static ValidationVirement createValidationVirement(Virement v) {
        if (v == null || !Integer.valueOf(21).equals(v.getStatut())) {
            return null; // On passe si statut != 21
        }
        
        // Créer et retourner l'objet ValidationVirement
        return new ValidationVirement(v.getIdVirement(), v.getIdentifiantSource());
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getIdObjet() { return idObjet; }
    public void setIdObjet(Long idObjet) { this.idObjet = idObjet; }
    
    public String getUtilisateur() { return utilisateur; }
    public void setUtilisateur(String utilisateur) { this.utilisateur = utilisateur; }
    
    public LocalDateTime getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationVirement that = (ValidationVirement) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "ValidationVirement{" +
                "id=" + id +
                ", idObjet=" + idObjet +
                ", utilisateur='" + utilisateur + '\'' +
                ", dateValidation=" + dateValidation +
                '}';
    }
}