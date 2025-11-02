package repository;

import entity.Pourcentage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PourcentageRepository extends JpaRepository<Pourcentage, Long> {

    // Méthode pour update le pourcentage ET la période
    @Modifying
    @Transactional
    @Query("UPDATE Pourcentage p SET p.pourcentage = :nouveauPourcentage, p.periode = :nouvellePeriode WHERE p.typeCompte = :typeCompte AND p.periode = :anciennePeriode")
    int updatePourcentage(@Param("typeCompte") String typeCompte, 
                         @Param("anciennePeriode") int anciennePeriode,
                         @Param("nouvellePeriode") int nouvellePeriode,
                         @Param("nouveauPourcentage") double nouveauPourcentage);


    @Query("SELECT p FROM Pourcentage p ORDER BY p.periode")
    List<Pourcentage> findAllOrdered();
}