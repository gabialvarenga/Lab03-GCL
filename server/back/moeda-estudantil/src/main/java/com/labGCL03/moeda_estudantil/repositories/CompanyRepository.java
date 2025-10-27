package com.labGCL03.moeda_estudantil.repositories;

import com.labGCL03.moeda_estudantil.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    
    Optional<Company> findByEmail(String email);
    
    Optional<Company> findByCnpj(String cnpj);
    
    boolean existsByCnpj(String cnpj);
    
    @Query("SELECT COUNT(a) FROM Advantage a WHERE a.company.id = :companyId")
    Long countAdvantagesByCompanyId(@Param("companyId") Long companyId);
}