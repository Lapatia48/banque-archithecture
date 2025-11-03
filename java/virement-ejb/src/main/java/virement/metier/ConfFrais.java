package virement.metier;

import java.io.Serializable;
import java.util.Objects;

public class ConfFrais implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String typeCompte;
    private Double montantInf;
    private Double montantSup;
    private Double fraisForf;
    private Double fraisPourc;
    
    // Constructeurs
    public ConfFrais() {}
    
    public ConfFrais(String typeCompte, Double montantInf, Double montantSup, Double fraisForf, Double fraisPourc) {
        this.typeCompte = typeCompte;
        this.montantInf = montantInf;
        this.montantSup = montantSup;
        this.fraisForf = fraisForf;
        this.fraisPourc = fraisPourc;
    }
    
    // Méthode utilitaire pour calculer les frais pour un montant donné
    public Double calculerFrais(Double montantVirement) {
        // Vérifier si le montant se trouve dans l'intervalle de cette configuration
        if (montantVirement >= montantInf && montantVirement <= montantSup) {
            Double frais = 0.0;
            // Si frais forfaitaire existe, l'ajouter
            if (fraisForf != null && fraisForf > 0) {
                frais += fraisForf;
            }     
            // Si frais en pourcentage existe, calculer et ajouter
            if (fraisPourc != null && fraisPourc > 0) {
                frais += montantVirement * (fraisPourc / 100);
            } 
            return frais;
        }
        return 0.0;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTypeCompte() { return typeCompte; }
    public void setTypeCompte(String typeCompte) { this.typeCompte = typeCompte; }
    
    public Double getMontantInf() { return montantInf; }
    public void setMontantInf(Double montantInf) { this.montantInf = montantInf; }
    
    public Double getMontantSup() { return montantSup; }
    public void setMontantSup(Double montantSup) { this.montantSup = montantSup; }
    
    public Double getFraisForf() { return fraisForf; }
    public void setFraisForf(Double fraisForf) { this.fraisForf = fraisForf; }
    
    public Double getFraisPourc() { return fraisPourc; }
    public void setFraisPourc(Double fraisPourc) { this.fraisPourc = fraisPourc; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfFrais confFrais = (ConfFrais) o;
        return Objects.equals(id, confFrais.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "ConfFrais{" +
                "id=" + id +
                ", typeCompte='" + typeCompte + '\'' +
                ", montantInf=" + montantInf +
                ", montantSup=" + montantSup +
                ", fraisForf=" + fraisForf +
                ", fraisPourc=" + fraisPourc +
                '}';
    }
}