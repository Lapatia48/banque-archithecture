package change;

public class ConversionRequest {
    private String devise;
    private Double montant;
    
    // Constructeur par défaut
    public ConversionRequest() {}
    
    // Constructeur avec paramètres
    public ConversionRequest(String devise, Double montant) {
        this.devise = devise;
        this.montant = montant;
    }
    
    // Getters et setters (IMPORTANT : nom correct)
    public String getDevise() { 
        return devise; 
    }
    
    public void setDevise(String devise) { 
        this.devise = devise; 
    }
    
    public Double getMontant() { 
        return montant; 
    }
    
    public void setMontant(Double montant) { 
        this.montant = montant; 
    }
}