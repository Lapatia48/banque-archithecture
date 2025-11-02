package compteCourant.metier;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Operation implements Serializable {
    private Long idOperation;
    private String identifiantUtilisateur;
    private String typeCompte;
    private String typeOperation;
    private Double montant;
    private LocalDateTime dateOperation;
    private String details;
    
    // Constructeurs
    public Operation() {}
    
    public Operation(String identifiantUtilisateur, String typeCompte, String typeOperation, 
                    Double montant, String details) {
        this.identifiantUtilisateur = identifiantUtilisateur;
        this.typeCompte = typeCompte;
        this.typeOperation = typeOperation;
        this.montant = montant;
        this.details = details;
        this.dateOperation = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getIdOperation() { return idOperation; }
    public void setIdOperation(Long idOperation) { this.idOperation = idOperation; }
    
    public String getIdentifiantUtilisateur() { return identifiantUtilisateur; }
    public void setIdentifiantUtilisateur(String identifiantUtilisateur) { this.identifiantUtilisateur = identifiantUtilisateur; }
    
    public String getTypeCompte() { return typeCompte; }
    public void setTypeCompte(String typeCompte) { this.typeCompte = typeCompte; }
    
    public String getTypeOperation() { return typeOperation; }
    public void setTypeOperation(String typeOperation) { this.typeOperation = typeOperation; }
    
    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }
    
    public LocalDateTime getDateOperation() { return dateOperation; }
    public void setDateOperation(LocalDateTime dateOperation) { this.dateOperation = dateOperation; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        return Objects.equals(idOperation, operation.idOperation);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idOperation);
    }
    
    @Override
    public String toString() {
        return "Operation{" +
                "idOperation=" + idOperation +
                ", identifiantUtilisateur='" + identifiantUtilisateur + '\'' +
                ", typeCompte='" + typeCompte + '\'' +
                ", typeOperation='" + typeOperation + '\'' +
                ", montant=" + montant +
                ", dateOperation=" + dateOperation +
                ", details='" + details + '\'' +
                '}';
    }
}