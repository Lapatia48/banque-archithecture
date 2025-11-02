package compteCourant;

import compteCourant.metier.CompteCourant;
import compteCourant.metier.Virement;
import compteCourant.metier.Operation;
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
public class CompteCourantBean implements CompteCourantRemote {
    
    @PersistenceContext(unitName = "banquePU")
    private EntityManager em;
    
    @Override
    public Double getSolde(String identifiant) {
        try {
            Optional<CompteCourant> compteOpt = getCompte(identifiant);
            return compteOpt.map(CompteCourant::getSolde).orElse(0.0);
        } catch (Exception e) {
            throw new RuntimeException("Erreur solde: " + e.getMessage());
        }
    }
    
    @Override
    public String faireDepot(String identifiant, Double montant, String details) {
        try {
            CompteCourant compte = getCompte(identifiant)
                .orElseThrow(() -> new RuntimeException("Compte courant non trouvé"));
            
            Operation operation = compte.deposer(montant, details, "depot");
            
            // Mettre à jour le solde en base
            mettreAJourSolde(compte);
            
            // Sauvegarder l'opération
            sauvegarderOperation(operation);
            
            return "Dépôt de " + montant + " réussi pour " + identifiant + ". Nouveau solde: " + compte.getSolde();
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur dépôt: " + e.getMessage());
        }
    }

    @Override
    public String faireDepotVirement(String identifiant, Double montant, String details) {
        try {
            CompteCourant compte = getCompte(identifiant)
                .orElseThrow(() -> new RuntimeException("Compte courant non trouvé"));
            
            Operation operation = compte.deposer(montant, details, "virement");
            
            // Mettre à jour le solde en base
            mettreAJourSolde(compte);
            
            // Sauvegarder l'opération
            sauvegarderOperation(operation);
            
            return "Dépôt de " + montant + " réussi pour " + identifiant;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur dépôt: " + e.getMessage());
        }
    }

    @Override
    public String faireRetrait(String identifiant, Double montant, String details) {
        try {
            CompteCourant compte = getCompte(identifiant)
                .orElseThrow(() -> new RuntimeException("Compte courant non trouvé"));
            
            Operation operation = compte.retirer(montant, details, "retrait");
            
            // Mettre à jour le solde en base
            mettreAJourSolde(compte);
            
            // Sauvegarder l'opération
            sauvegarderOperation(operation);
            
            return "Retrait de " + montant + " réussi pour " + identifiant + ". Nouveau solde: " + compte.getSolde();
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur retrait: " + e.getMessage());
        }
    }

    @Override
    public String faireRetraitVirement(String identifiant, Double montant, String details) {
        try {
            CompteCourant compte = getCompte(identifiant)
                .orElseThrow(() -> new RuntimeException("Compte courant non trouvé"));
            
            Operation operation = compte.retirer(montant, details, "virement");
            
            // Mettre à jour le solde en base
            mettreAJourSolde(compte);
            
            // Sauvegarder l'opération
            sauvegarderOperation(operation);
            
            return "Retrait de " + montant + " réussi pour " + identifiant;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur retrait: " + e.getMessage());
        }
    }

    @Override
    public List<Object[]> getOperations(String identifiant) {
        try {
            // Pour la compatibilité, on retourne encore List<Object[]>
            List<Operation> operations = getOperationsObjet(identifiant);
            return operations.stream()
                    .map(op -> new Object[]{
                        op.getDateOperation(),
                        op.getTypeCompte(),
                        op.getTypeOperation(),
                        op.getMontant(),
                        op.getDetails()
                    })
                    .toList();
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur opérations: " + e.getMessage());
        }
    }

    @Override
    public String faireVirement(String identifiantSource, String identifiantDest, Double montant, String details) {
        try {
            // Récupérer les comptes
            CompteCourant compteSource = getCompte(identifiantSource)
                .orElseThrow(() -> new RuntimeException("Compte source non trouvé"));
            
            CompteCourant compteDest = getCompte(identifiantDest)
                .orElseThrow(() -> new RuntimeException("Compte destination non trouvé"));
            
            // Créer et exécuter le virement
            Virement virement = new Virement(identifiantSource, identifiantDest, montant, "MGA", details);
            virement.valider(compteSource);
            
            Operation[] operations = virement.executer(compteSource, compteDest);
            
            // Mettre à jour les soldes
            mettreAJourSolde(compteSource);
            mettreAJourSolde(compteDest);
            
            // Sauvegarder les opérations
            sauvegarderOperation(operations[0]); // Retrait
            sauvegarderOperation(operations[1]); // Depot
            
            return "Virement de " + montant + " de " + identifiantSource + " à " + identifiantDest + " réussi.";
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur virement: " + e.getMessage());
        }
    }
    
    // Nouvelles méthodes orientées objet
    public Optional<CompteCourant> getCompte(String identifiant) {
        try {
            String sql = "SELECT id_compte, identifiant, type_compte, libelle, solde, niveau " +
                        "FROM Comptes WHERE identifiant = ?1 AND type_compte = 'courant'";
            
            Object[] result = (Object[]) em.createNativeQuery(sql)
                    .setParameter(1, identifiant)
                    .getSingleResult();
            
            if (result != null) {
                CompteCourant compte = new CompteCourant();
                compte.setIdCompte(((Number) result[0]).longValue());
                compte.setIdentifiantUtilisateur((String) result[1]);
                compte.setLibelle((String) result[3]);
                compte.setSolde(((Number) result[4]).doubleValue());
                compte.setNiveau(((Number) result[5]).intValue());
                return Optional.of(compte);
            }
            return Optional.empty();
            
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public Virement creerVirement(String identifiantSource, String identifiantDest, 
                                 Double montant, String devise, String details) {
        return new Virement(identifiantSource, identifiantDest, montant, devise, details);
    }
    
    public List<Operation> getOperationsObjet(String identifiant) {
        try {
            String sql = "SELECT id_operation, identifiant, compte, type_operation, montant, date_operation, details " +
                        "FROM Operations WHERE identifiant = ?1 AND compte = 'courant' ORDER BY date_operation DESC";
            
            List<Object[]> results = em.createNativeQuery(sql)
                    .setParameter(1, identifiant)
                    .getResultList();
            
            List<Operation> operations = new ArrayList<>();
            for (Object[] result : results) {
                Operation operation = new Operation();
                operation.setIdOperation(((Number) result[0]).longValue());
                operation.setIdentifiantUtilisateur((String) result[1]);
                operation.setTypeCompte((String) result[2]);
                operation.setTypeOperation((String) result[3]);
                operation.setMontant(((Number) result[4]).doubleValue());
                operation.setDateOperation(((Timestamp) result[5]).toLocalDateTime());
                operation.setDetails((String) result[6]);
                operations.add(operation);
            }
            
            return operations;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur récupération opérations: " + e.getMessage());
        }
    }
    
    // Méthodes privées pour la persistance
    private void mettreAJourSolde(CompteCourant compte) {
        String sql = "UPDATE Comptes SET solde = ?1 WHERE id_compte = ?2";
        em.createNativeQuery(sql)
                .setParameter(1, compte.getSolde())
                .setParameter(2, compte.getIdCompte())
                .executeUpdate();
    }
    
    private void sauvegarderOperation(Operation operation) {
        String sql = "INSERT INTO Operations (identifiant, compte, type_operation, montant, details, date_operation) " +
                    "VALUES (?1, ?2, ?3, ?4, ?5, ?6)";
        
        em.createNativeQuery(sql)
                .setParameter(1, operation.getIdentifiantUtilisateur())
                .setParameter(2, operation.getTypeCompte())
                .setParameter(3, operation.getTypeOperation())
                .setParameter(4, operation.getMontant())
                .setParameter(5, operation.getDetails())
                .setParameter(6, Timestamp.valueOf(operation.getDateOperation()))
                .executeUpdate();
    }
}