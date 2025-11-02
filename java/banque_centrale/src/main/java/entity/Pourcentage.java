package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Pourcentage")
public class Pourcentage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pourcentage")
    private Long idPourcentage;

    @Column(name = "type_compte")
    private String typeCompte;

    @Column(name = "periode")
    private int periode;

    @Column(name = "pourcentage")
    private double pourcentage;

    // Constructeurs, getters, setters (inchangés)
    public Pourcentage() {}

    public Pourcentage(String typeCompte, int periode, double pourcentage) {
        this.typeCompte = typeCompte;
        this.periode = periode;
        this.pourcentage = pourcentage;
    }

    // Getters/Setters (ajouter getIdPourcentage() et setIdPourcentage())
    public Long getIdPourcentage() { return idPourcentage; }
    public void setIdPourcentage(Long idPourcentage) { this.idPourcentage = idPourcentage; }
    
    public String getTypeCompte() { return typeCompte; }
    public void setTypeCompte(String typeCompte) { this.typeCompte = typeCompte; }
    
    public int getPeriode() { return periode; }
    public void setPeriode(int periode) { this.periode = periode; }
    
    public double getPourcentage() { return pourcentage; }
    public void setPourcentage(double pourcentage) { this.pourcentage = pourcentage; }
}