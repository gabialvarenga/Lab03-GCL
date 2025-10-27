package com.labGCL03.moeda_estudantil.repositories;

import com.labGCL03.moeda_estudantil.entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    
    Optional<Teacher> findByEmail(String email);
    
    Optional<Teacher> findByCpf(String cpf);
    
    boolean existsByCpf(String cpf);
    
    @Query("SELECT t FROM Teacher t WHERE t.institution.id = :institutionId")
    List<Teacher> findByInstitutionId(@Param("institutionId") Long institutionId);
                                                   
    
    @Query("SELECT t FROM Teacher t WHERE t.currentBalance >= :minBalance")
    List<Teacher> findTeachersWithMinimumBalance(@Param("minBalance") Integer minBalance);
    
    @Query("SELECT t FROM Teacher t WHERE t.lastCreditPeriod != :currentPeriod OR t.lastCreditPeriod IS NULL")
    List<Teacher> findTeachersNeedingSemesterCredit(@Param("currentPeriod") String currentPeriod);
    
    @Query("SELECT SUM(t.currentBalance) FROM Teacher t WHERE t.institution.id = :institutionId")
    Long getTotalCoinsInInstitution(@Param("institutionId") Long institutionId);
    
    @Query("SELECT AVG(t.currentBalance) FROM Teacher t WHERE t.institution.id = :institutionId")
    Double getAverageCoinsInInstitution(@Param("institutionId") Long institutionId);
}