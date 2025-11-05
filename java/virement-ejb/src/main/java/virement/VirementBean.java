package virement;

import virement.metier.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Stateless
public class VirementBean implements VirementRemote {
    
    @PersistenceContext(unitName = "virement")
    private EntityManager em;
    
    @Override
    public Virement creerVirement(String identifiantSource, String identifiantDestination, 
                                        Double montant, Double fraisDeVirement, 
                                        String devise, String details, String createdBy) {
        try {
            Virement virement = new Virement(identifiantSource, identifiantDestination, montant, devise, details, createdBy);
            virement.setFraisDeVirement(fraisDeVirement); // ✅ SET DES FRAIS
            
            // ✅ MODIFICATION SQL : Ajout de fraisDeVirement
            String sql = "INSERT INTO Virements (identifiant_source, identifiant_destination, " +
                        "montant, fraisDeVirement, devise, details, date_creation, statut, created_by) " +
                        "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9)";
            
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, virement.getIdentifiantSource());
            query.setParameter(2, virement.getIdentifiantDestination());
            query.setParameter(3, virement.getMontant());
            query.setParameter(4, virement.getFraisDeVirement()); // ✅ FRAIS
            query.setParameter(5, virement.getDevise());
            query.setParameter(6, virement.getDetails());
            query.setParameter(7, Timestamp.valueOf(virement.getDateCreation()));
            query.setParameter(8, virement.getStatut());
            query.setParameter(9, virement.getCreatedBy());
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
    
   // Modification de la méthode executerVirement pour créer automatiquement la validation
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
            
            // Créer la validation virement
            creerValidationVirement(virement);
            
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
            if (result[4] != null) {
                virement.setFraisDeVirement(((Number) result[4]).doubleValue());
            } else {
                virement.setFraisDeVirement(0.0);
            }
            virement.setDevise((String) result[5]); 
            virement.setDetails((String) result[6]);
            virement.setDateCreation(((Timestamp) result[7]).toLocalDateTime()); 
            if (result[8] != null) {
                virement.setDateExecution(((Timestamp) result[8]).toLocalDateTime());
            }
            virement.setStatut(((Number) result[9]).intValue()); 
            virement.setMotifRefus((String) result[10]); 
            virement.setCreatedBy((String) result[11]); 
            
            virements.add(virement);
        }
        
        return virements;
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

    @Override
    public Double calculerFraisPourVirement(Double montant) {
        if (montant == null || montant <= 0) {
            return 0.0;
        }

        try {
            String sql = "SELECT * FROM conf_frais WHERE ?1 BETWEEN montant_inf AND montant_sup AND type_compte = 'courant'";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, montant);
            
            List<Object[]> results = query.getResultList();
            if (!results.isEmpty()) {
                Object[] result = results.get(0);
                ConfFrais confFrais = new ConfFrais();
                confFrais.setId(((Number) result[0]).longValue());
                confFrais.setTypeCompte((String) result[1]);
                confFrais.setMontantInf(((Number) result[2]).doubleValue());
                confFrais.setMontantSup(((Number) result[3]).doubleValue());
                confFrais.setFraisForf(result[4] != null ? ((Number) result[4]).doubleValue() : 0.0);
                confFrais.setFraisPourc(result[5] != null ? ((Number) result[5]).doubleValue() : 0.0);
                
                // Appel de la méthode calculerFrais de l'objet
                return confFrais.calculerFrais(montant);
            }
            return 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private void effectuerOperationsComptables(Virement virement) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("virement");
        EntityManager tempEm = emf.createEntityManager();
        EntityTransaction transaction = null;

        try{
            transaction = tempEm.getTransaction();
            transaction.begin();

            // retrait sur source
            String retraitSql = "UPDATE Comptes SET solde = solde - ?1 WHERE identifiant = ?2 AND type_compte = 'courant'";
            Query retraitQuery = em.createNativeQuery(retraitSql);
            retraitQuery.setParameter(1, virement.getMontant()+virement.getFraisDeVirement());
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
        catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Erreur lors des opérations comptables: " + e.getMessage(), e);
        } finally {
            tempEm.close();
            emf.close();
        }
        
    }
    

    @Override
    public ValidationVirement creerValidationVirement(Virement virement) {
        try {
            ValidationVirement validation = ValidationVirement.createValidationVirement(virement);
            if (validation == null) {
                return null; // Statut != 21
            }
            
            // Persister en base
            String sql = "INSERT INTO ValidationVirement (id_objet, utilisateur, date_validation) " +
                        "VALUES (?1, ?2, ?3)";
            
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, validation.getIdObjet());
            query.setParameter(2, validation.getUtilisateur());
            query.setParameter(3, Timestamp.valueOf(validation.getDateValidation()));
            query.executeUpdate();
            
            // Récupérer l'ID généré
            Query idQuery = em.createNativeQuery("SELECT currval('validationvirement_id_seq')");
            Long id = ((Number) idQuery.getSingleResult()).longValue();
            validation.setId(id);
            
            return validation;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur création validation virement: " + e.getMessage());
        }
    }
    
    @Override
    public List<ValidationVirement> getValidationsByVirement(Long idVirement) {
        try {
            String sql = "SELECT * FROM ValidationVirement WHERE id_objet = ?1 ORDER BY date_validation DESC";
            return executerRequeteValidations(sql, idVirement);
        } catch (Exception e) {
            throw new RuntimeException("Erreur récupération validations par virement: " + e.getMessage());
        }
    }
    
    @Override
    public List<ValidationVirement> getValidationsByUtilisateur(String utilisateur) {
        try {
            String sql = "SELECT * FROM ValidationVirement WHERE utilisateur = ?1 ORDER BY date_validation DESC";
            return executerRequeteValidations(sql, utilisateur);
        } catch (Exception e) {
            throw new RuntimeException("Erreur récupération validations par utilisateur: " + e.getMessage());
        }
    }
    
    // Méthode privée pour exécuter les requêtes de validation
    private List<ValidationVirement> executerRequeteValidations(String sql, Object... params) {
        Query query = em.createNativeQuery(sql);
        for (int i = 0; i < params.length; i++) {
            query.setParameter(i + 1, params[i]);
        }
        
        List<Object[]> results = query.getResultList();
        List<ValidationVirement> validations = new ArrayList<>();
        
        for (Object[] result : results) {
            ValidationVirement validation = new ValidationVirement();
            validation.setId(((Number) result[0]).longValue());
            validation.setIdObjet(((Number) result[1]).longValue());
            validation.setUtilisateur((String) result[2]);
            if (result[3] != null) {
                validation.setDateValidation(((Timestamp) result[3]).toLocalDateTime());
            }
            validations.add(validation);
        }
        
        return validations;
    }

    @Override
    public Double getSommeVirementsParDate(String identifiant, LocalDateTime date) {
        try {
            java.sql.Date sqlDate = java.sql.Date.valueOf(date.toLocalDate());
            
            System.out.println("DEBUG - Recherche virements pour: " + identifiant + " à la date: " + sqlDate);
            
            String sql = "SELECT COALESCE(SUM(montant), 0) FROM Virements " +
                        "WHERE identifiant_source = ?1 " +
                        "AND DATE(date_creation) = ?2 " +
                        "AND statut IN (11, 21)";
            
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, identifiant);
            query.setParameter(2, sqlDate);
            
            Object result = query.getSingleResult();
            Double somme = result != null ? ((Number) result).doubleValue() : 0.0;
            
            System.out.println("DEBUG - Somme trouvée: " + somme);
            return somme;   
            
        } catch (Exception e) {
            System.out.println("Erreur getSommeVirementsParDate: " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }

    @Override
    public Map<String, Object> verifierLimiteJournaliereAvecDetails(Long idVirement) {
        Map<String, Object> resultat = new HashMap<>();
        
        try {
            Virement virement = getVirementById(idVirement)
                .orElseThrow(() -> new RuntimeException("Virement non trouvé"));
            
            String identifiantSource = virement.getIdentifiantSource();
            Double montantVirement = virement.getMontant();
            
            // Récupérer la somme des virements aujourd'hui
            Double sommeVirementsAujourdhui = getSommeVirementsParDate(identifiantSource, LocalDateTime.now());
            
            // Récupérer la limite journalière
            String sqlLimite = "SELECT limite_journaliere FROM Comptes WHERE identifiant = ?1 AND type_compte = 'courant'";
            Query queryLimite = em.createNativeQuery(sqlLimite);
            queryLimite.setParameter(1, identifiantSource);
            
            Object resultLimite = queryLimite.getSingleResult();
            Double limiteJournaliere = resultLimite != null ? ((Number) resultLimite).doubleValue() : 0.0;
            
            // Calculer le total après virement
            Double totalApresVirement = sommeVirementsAujourdhui + montantVirement;
            Boolean limiteRespectee = (limiteJournaliere == 0) || (totalApresVirement <= limiteJournaliere);
            
            // Remplir le résultat avec tous les détails
            resultat.put("limiteRespectee", limiteRespectee);
            resultat.put("limiteJournaliere", limiteJournaliere);
            resultat.put("sommeVirementsAujourdhui", sommeVirementsAujourdhui);
            resultat.put("montantVirement", montantVirement);
            resultat.put("totalApresVirement", totalApresVirement);
            resultat.put("identifiant", identifiantSource);
            resultat.put("virementId", idVirement);
            
            System.out.println("DEBUG Limite - " + identifiantSource + 
                            " | Déjà viré: " + sommeVirementsAujourdhui + 
                            " | Virement: " + montantVirement + 
                            " | Total: " + totalApresVirement +
                            " | Limite: " + limiteJournaliere +
                            " | Respecté: " + limiteRespectee);
            
        } catch (Exception e) {
            resultat.put("limiteRespectee", false);
            resultat.put("erreur", e.getMessage());
            System.out.println("DEBUG Limite - Erreur: " + e.getMessage());
        }
        
        return resultat;
    }

}