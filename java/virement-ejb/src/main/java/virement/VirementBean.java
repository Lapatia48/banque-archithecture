package virement;

import virement.metier.Virement;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class VirementBean implements VirementRemote {
    
    @PersistenceContext(unitName = "virement")
    private EntityManager em;
    
    @Override
    public Virement creerVirement(String identifiantSource, String identifiantDestination, 
                                Double montant, String devise, String details, String createdBy) {
        try {
            Virement virement = new Virement(identifiantSource, identifiantDestination, montant, devise, details, createdBy);
            
            // Sauvegarder en base
            String sql = "INSERT INTO Virements (identifiant_source, identifiant_destination, montant, devise, details, date_creation, statut, created_by) " +
                        "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8)";
            
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, virement.getIdentifiantSource());
            query.setParameter(2, virement.getIdentifiantDestination());
            query.setParameter(3, virement.getMontant());
            query.setParameter(4, virement.getDevise());
            query.setParameter(5, virement.getDetails());
            query.setParameter(6, Timestamp.valueOf(virement.getDateCreation()));
            query.setParameter(7, virement.getStatut());
            query.setParameter(8, virement.getCreatedBy());
            query.executeUpdate();
            
            // Récupérer l'ID généré
            Query idQuery = em.createNativeQuery("SELECT currval('virements_id_virement_seq')");
            Long id = ((Number) idQuery.getSingleResult()).longValue();
            virement.setIdVirement(id);
            
            return virement;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur création virement: " + e.getMessage());
        }
    }
    
    @Override
    public Virement validerVirement(Long idVirement) {
        try {
            Virement virement = getVirementById(idVirement)
                .orElseThrow(() -> new RuntimeException("Virement non trouvé"));
            
            virement.valider();
            mettreAJourVirement(virement);
            
            return virement;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur validation virement: " + e.getMessage());
        }
    }
    
    @Override
    public Virement executerVirement(Long idVirement) {
        try {
            Virement virement = getVirementById(idVirement)
                .orElseThrow(() -> new RuntimeException("Virement non trouvé"));
            
            // Vérifier le solde source avant exécution
            String soldeSql = "SELECT solde FROM Comptes WHERE identifiant = ?1 AND type_compte = 'courant'";
            Query soldeQuery = em.createNativeQuery(soldeSql);
            soldeQuery.setParameter(1, virement.getIdentifiantSource());
            Double solde = ((Number) soldeQuery.getSingleResult()).doubleValue();
            
            if (solde < virement.getMontant()) {
                throw new RuntimeException("Solde insuffisant pour exécuter le virement");
            }
            
            virement.executer();
            mettreAJourVirement(virement);
            
            // Effectuer les opérations comptables
            effectuerOperationsComptables(virement);
            
            return virement;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur exécution virement: " + e.getMessage());
        }
    }
    
    @Override
    public Virement annulerVirement(Long idVirement, String motif) {
        try {
            Virement virement = getVirementById(idVirement)
                .orElseThrow(() -> new RuntimeException("Virement non trouvé"));
            
            virement.annuler(motif);
            mettreAJourVirement(virement);
            
            return virement;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur annulation virement: " + e.getMessage());
        }
    }
    
    @Override
    public Virement refuserVirement(Long idVirement, String motif) {
        try {
            Virement virement = getVirementById(idVirement)
                .orElseThrow(() -> new RuntimeException("Virement non trouvé"));
            
            virement.refuser(motif);
            mettreAJourVirement(virement);
            
            return virement;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur refus virement: " + e.getMessage());
        }
    }
    
    @Override
    public Virement modifierVirement(Long idVirement, Double nouveauMontant, String nouveauxDetails) {
        try {
            Virement virement = getVirementById(idVirement)
                .orElseThrow(() -> new RuntimeException("Virement non trouvé"));
            
            virement.modifier(nouveauMontant, nouveauxDetails);
            mettreAJourVirement(virement);
            
            return virement;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur modification virement: " + e.getMessage());
        }
    }
    
    @Override
    public List<Virement> getVirementsEnAttente() {
        try {
            String sql = "SELECT * FROM Virements WHERE statut = 1 ORDER BY date_creation DESC";
            return executerRequeteVirements(sql);
        } catch (Exception e) {
            throw new RuntimeException("Erreur récupération virements en attente: " + e.getMessage());
        }
    }
    
    @Override
    public List<Virement> getTousVirements() {
        try {
            String sql = "SELECT * FROM Virements ORDER BY date_creation DESC";
            return executerRequeteVirements(sql);
        } catch (Exception e) {
            throw new RuntimeException("Erreur récupération tous les virements: " + e.getMessage());
        }
    }
    
    @Override
    public Optional<Virement> getVirementById(Long idVirement) {
        try {
            String sql = "SELECT * FROM Virements WHERE id_virement = ?1";
            List<Virement> virements = executerRequeteVirements(sql, idVirement);
            return virements.isEmpty() ? Optional.empty() : Optional.of(virements.get(0));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    // Méthodes privées
    private void mettreAJourVirement(Virement virement) {
        String sql = "UPDATE Virements SET montant = ?1, details = ?2, date_execution = ?3, statut = ?4, motif_refus = ?5 " +
                    "WHERE id_virement = ?6";
        
        Query query = em.createNativeQuery(sql);
        query.setParameter(1, virement.getMontant());
        query.setParameter(2, virement.getDetails());
        query.setParameter(3, virement.getDateExecution() != null ? Timestamp.valueOf(virement.getDateExecution()) : null);
        query.setParameter(4, virement.getStatut());
        query.setParameter(5, virement.getMotifRefus());
        query.setParameter(6, virement.getIdVirement());
        query.executeUpdate();
    }
    
    private void effectuerOperationsComptables(Virement virement) {
        // Retrait du compte source
        String retraitSql = "UPDATE Comptes SET solde = solde - ?1 WHERE identifiant = ?2 AND type_compte = 'courant'";
        Query retraitQuery = em.createNativeQuery(retraitSql);
        retraitQuery.setParameter(1, virement.getMontant());
        retraitQuery.setParameter(2, virement.getIdentifiantSource());
        retraitQuery.executeUpdate();
        
        // Dépôt sur le compte destination
        String depotSql = "UPDATE Comptes SET solde = solde + ?1 WHERE identifiant = ?2 AND type_compte = 'courant'";
        Query depotQuery = em.createNativeQuery(depotSql);
        depotQuery.setParameter(1, virement.getMontant());
        depotQuery.setParameter(2, virement.getIdentifiantDestination());
        depotQuery.executeUpdate();
        
        // Enregistrer les opérations
        String operationSql = "INSERT INTO Operations (identifiant, compte, type_operation, montant, details, date_operation) " +
                             "VALUES (?1, 'courant', ?2, ?3, ?4, ?5)";
        
        // Opération de retrait
        Query opRetrait = em.createNativeQuery(operationSql);
        opRetrait.setParameter(1, virement.getIdentifiantSource());
        opRetrait.setParameter(2, "virement_valide");
        opRetrait.setParameter(3, -virement.getMontant());
        opRetrait.setParameter(4, "Virement vers " + virement.getIdentifiantDestination() + ": " + virement.getDetails());
        opRetrait.setParameter(5, Timestamp.valueOf(LocalDateTime.now()));
        opRetrait.executeUpdate();
        
        // Opération de dépôt
        Query opDepot = em.createNativeQuery(operationSql);
        opDepot.setParameter(1, virement.getIdentifiantDestination());
        opDepot.setParameter(2, "virement_valide");
        opDepot.setParameter(3, virement.getMontant());
        opDepot.setParameter(4, "Virement de " + virement.getIdentifiantSource() + ": " + virement.getDetails());
        opDepot.setParameter(5, Timestamp.valueOf(LocalDateTime.now()));
        opDepot.executeUpdate();
    }
    
    private List<Virement> executerRequeteVirements(String sql, Object... params) {
        Query query = em.createNativeQuery(sql);
        for (int i = 0; i < params.length; i++) {
            query.setParameter(i + 1, params[i]);
        }
        
        List<Object[]> results = query.getResultList();
        List<Virement> virements = new ArrayList<>();
        
        for (Object[] result : results) {
            Virement virement = new Virement();
            virement.setIdVirement(((Number) result[0]).longValue());
            virement.setIdentifiantSource((String) result[1]);
            virement.setIdentifiantDestination((String) result[2]);
            virement.setMontant(((Number) result[3]).doubleValue());
            virement.setDevise((String) result[4]);
            virement.setDetails((String) result[5]);
            virement.setDateCreation(((Timestamp) result[6]).toLocalDateTime());
            if (result[7] != null) {
                virement.setDateExecution(((Timestamp) result[7]).toLocalDateTime());
            }
            virement.setStatut(((Number) result[8]).intValue());
            virement.setMotifRefus((String) result[9]);
            virement.setCreatedBy((String) result[10]);
            virements.add(virement);
        }
        
        return virements;
    }
}