package virement.metier;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Virement implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long idVirement;
    private String identifiantSource;
    private String identifiantDestination;
    private Double montant;
    private String devise;
    private String details;
    private LocalDateTime dateCreation;
    private LocalDateTime dateExecution;
    private String statut; 
    private String motifRefus;
    private String createdBy;
    
    // Constructeurs
    public Virement() {}
    
    public Virement(String identifiantSource, String identifiantDestination, Double montant, 
                   String devise, String details, String createdBy) {
        this.identifiantSource = identifiantSource;
        this.identifiantDestination = identifiantDestination;
        this.montant = montant;
        this.devise = devise;
        this.details = details;
        this.createdBy = createdBy;
        this.dateCreation = LocalDateTime.now();
        this.statut = "EN_ATTENTE";
    }
    
    // Méthodes métier qui retournent l'objet modifié
    public Virement valider() {
        if (!"EN_ATTENTE".equals(this.statut)) {
            throw new IllegalStateException("Seuls les virements en attente peuvent être validés");
        }
        this.statut = "VALIDE";
        return this;
    }
    
    public Virement executer() {
        if (!"VALIDE".equals(this.statut)) {
            throw new IllegalStateException("Seuls les virements validés peuvent être exécutés");
        }
        this.statut = "EXECUTE";
        this.dateExecution = LocalDateTime.now();
        return this;
    }
    
    public Virement annuler(String motif) {
        if ("EXECUTE".equals(this.statut)) {
            throw new IllegalStateException("Impossible d'annuler un virement déjà exécuté");
        }
        this.statut = "ANNULE";
        this.motifRefus = motif;
        return this;
    }
    
    public Virement refuser(String motif) {
        if (!"EN_ATTENTE".equals(this.statut)) {
            throw new IllegalStateException("Seuls les virements en attente peuvent être refusés");
        }
        this.statut = "REFUSE";
        this.motifRefus = motif;
        return this;
    }
    
    public Virement modifier(Double nouveauMontant, String nouveauxDetails) {
        if (!"EN_ATTENTE".equals(this.statut)) {
            throw new IllegalStateException("Seuls les virements en attente peuvent être modifiés");
        }
        if (nouveauMontant != null && nouveauMontant > 0) {
            this.montant = nouveauMontant;
        }
        if (nouveauxDetails != null) {
            this.details = nouveauxDetails;
        }
        return this;
    }
    
    // Getters et Setters
    public Long getIdVirement() { return idVirement; }
    public void setIdVirement(Long idVirement) { this.idVirement = idVirement; }
    
    public String getIdentifiantSource() { return identifiantSource; }
    public void setIdentifiantSource(String identifiantSource) { this.identifiantSource = identifiantSource; }
    
    public String getIdentifiantDestination() { return identifiantDestination; }
    public void setIdentifiantDestination(String identifiantDestination) { this.identifiantDestination = identifiantDestination; }
    
    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }
    
    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    public LocalDateTime getDateExecution() { return dateExecution; }
    public void setDateExecution(LocalDateTime dateExecution) { this.dateExecution = dateExecution; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public String getMotifRefus() { return motifRefus; }
    public void setMotifRefus(String motifRefus) { this.motifRefus = motifRefus; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Virement virement = (Virement) o;
        return Objects.equals(idVirement, virement.idVirement);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idVirement);
    }
    
    @Override
    public String toString() {
        return "Virement{" +
                "idVirement=" + idVirement +
                ", identifiantSource='" + identifiantSource + '\'' +
                ", identifiantDestination='" + identifiantDestination + '\'' +
                ", montant=" + montant +
                ", devise='" + devise + '\'' +
                ", details='" + details + '\'' +
                ", dateCreation=" + dateCreation +
                ", statut='" + statut + '\'' +
                '}';
    }
}