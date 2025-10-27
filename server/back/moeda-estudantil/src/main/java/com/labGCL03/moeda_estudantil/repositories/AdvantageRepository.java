package com.labGCL03.moeda_estudantil.repositories;

import com.labGCL03.moeda_estudantil.entities.Advantage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvantageRepository extends JpaRepository<Advantage, Long> {
    
    List<Advantage> findByCompanyId(Long companyId);
    
    @Query("SELECT a FROM Advantage a WHERE a.costInCoins <= :maxCost")
    List<Advantage> findAdvantagesWithinBudget(@Param("maxCost") Integer maxCost);
    
    @Query("SELECT a FROM Advantage a WHERE a.name LIKE %:name%")
    List<Advantage> searchAdvantagesByName(@Param("name") String name);
    
    @Query("SELECT a FROM Advantage a WHERE a.company.id = :companyId " +
           "AND a.costInCoins BETWEEN :minCost AND :maxCost")
    List<Advantage> findAdvantagesByCompanyAndCostRange(@Param("companyId") Long companyId,
                                                        @Param("minCost") Integer minCost,
                                                        @Param("maxCost") Integer maxCost);
    
    @Query("SELECT COUNT(c) FROM Coupon c WHERE c.advantage.id = :advantageId")
    Long countCouponsGenerated(@Param("advantageId") Long advantageId);
    
    @Query("SELECT AVG(a.costInCoins) FROM Advantage a")
    Double getAverageCostOfAdvantages();
}