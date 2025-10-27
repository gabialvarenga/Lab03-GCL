package com.labGCL03.moeda_estudantil.repositories;

import com.labGCL03.moeda_estudantil.entities.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    
    Optional<Institution> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.institution.id = :institutionId")
    Long countStudentsByInstitutionId(Long institutionId);
    
    @Query("SELECT COUNT(t) FROM Teacher t WHERE t.institution.id = :institutionId")
    Long countTeachersByInstitutionId(Long institutionId);
}