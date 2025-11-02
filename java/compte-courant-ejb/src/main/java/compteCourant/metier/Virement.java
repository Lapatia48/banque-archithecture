package compteCourant.metier;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Virement implements Serializable {
    private String identifiantSource;
    private String identifiantDestination;
    private Double montant;
    private String devise;
    private String details;
    private LocalDateTime dateExecution;
    private String statut;
    
    // Constructeurs
    public Virement() {}
    
    public Virement(String identifiantSource, String identifiantDestination, Double montant, String devise, String details) {
        this.identifiantSource = identifiantSource;
        this.identifiantDestination = identifiantDestination;
        this.montant = montant;
        this.devise = devise;
        this.details = details;
        this.dateExecution = LocalDateTime.now();
        this.statut = "INITIALISE";
    }
    
    // Méthodes métier
    public void valider(CompteCourant compteSource) {
        if (identifiantSource == null || identifiantDestination == null) {
            throw new IllegalStateException("Les identifiants source et destination sont requis");
        }
        if (montant <= 0) {
            throw new IllegalStateException("Le montant doit être positif");
        }
        if (compteSource != null && !compteSource.soldeSuffisant(montant)) {
            throw new IllegalStateException("Solde insuffisant pour effectuer le virement");
        }
        this.statut = "VALIDE";
    }
    
    public Operation[] executer(CompteCourant compteSource, CompteCourant compteDest) {
        if (!"VALIDE".equals(statut)) {
            throw new IllegalStateException("Le virement doit être validé avant exécution");
        }
        
        // Créer les opérations
        Operation operationRetrait = compteSource.retirer(
            this.montant, 
            "Virement vers " + identifiantDestination + (details != null ? ": " + details : ""),
            "virement"
        );
        
        Operation operationDepot = compteDest.deposer(
            this.montant, 
            "Virement de " + identifiantSource + (details != null ? ": " + details : ""),
            "virement"
        );
        
        this.statut = "EXECUTE";
        this.dateExecution = LocalDateTime.now();
        
        return new Operation[]{operationRetrait, operationDepot};
    }
    
    public void annuler() {
        this.statut = "ANNULE";
    }
    
    // Getters et Setters
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
    
    public LocalDateTime getDateExecution() { return dateExecution; }
    public void setDateExecution(LocalDateTime dateExecution) { this.dateExecution = dateExecution; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Virement virement = (Virement) o;
        return Objects.equals(identifiantSource, virement.identifiantSource) &&
                Objects.equals(identifiantDestination, virement.identifiantDestination) &&
                Objects.equals(montant, virement.montant) &&
                Objects.equals(dateExecution, virement.dateExecution);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(identifiantSource, identifiantDestination, montant, dateExecution);
    }
    
    @Override
    public String toString() {
        return "Virement{" +
                "identifiantSource='" + identifiantSource + '\'' +
                ", identifiantDestination='" + identifiantDestination + '\'' +
                ", montant=" + montant +
                ", devise='" + devise + '\'' +
                ", details='" + details + '\'' +
                ", dateExecution=" + dateExecution +
                ", statut='" + statut + '\'' +
                '}';
    }
}