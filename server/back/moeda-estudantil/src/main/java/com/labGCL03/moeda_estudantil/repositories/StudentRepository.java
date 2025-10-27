package com.labGCL03.moeda_estudantil.repositories;

import com.labGCL03.moeda_estudantil.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    Optional<Student> findByEmail(String email);
    
    Optional<Student> findByCpf(String cpf);
    
    boolean existsByCpf(String cpf);
    
    List<Student> findByCourse(String course);
    
    List<Student> findByInstitutionId(Long institutionId);
    
    @Query("SELECT s FROM Student s WHERE s.institution.id = :institutionId AND s.course = :course")
    List<Student> findByInstitutionIdAndCourse(@Param("institutionId") Long institutionId, 
                                               @Param("course") String course);
    
    @Query("SELECT s FROM Student s WHERE s.coinBalance >= :minBalance")
    List<Student> findStudentsWithMinimumBalance(@Param("minBalance") Integer minBalance);
    
    @Query("SELECT SUM(s.coinBalance) FROM Student s WHERE s.institution.id = :institutionId")
    Long getTotalCoinsInInstitution(@Param("institutionId") Long institutionId);
    
    @Query("SELECT AVG(s.coinBalance) FROM Student s WHERE s.institution.id = :institutionId")
    Double getAverageCoinsInInstitution(@Param("institutionId") Long institutionId);
}