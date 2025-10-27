package com.labGCL03.moeda_estudantil.repositories;

import com.labGCL03.moeda_estudantil.entities.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    Optional<Coupon> findByCode(String code);
    
    List<Coupon> findByStudentIdOrderByGeneratedDateDesc(Long studentId);
    
    List<Coupon> findByAdvantageIdOrderByGeneratedDateDesc(Long advantageId);
    
    List<Coupon> findByUsedFalse();
    
    List<Coupon> findByUsedTrue();
    
    @Query("SELECT c FROM Coupon c WHERE c.student.id = :studentId")
    List<Coupon> findByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT c FROM Coupon c WHERE c.advantage.company.id = :companyId ORDER BY c.generatedDate DESC")
    List<Coupon> findCouponsByCompanyId(@Param("companyId") Long companyId);
    
    @Query("SELECT COUNT(c) FROM Coupon c WHERE c.student.id = :studentId AND c.used = true")
    Long countUsedCouponsByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(c) FROM Coupon c WHERE c.advantage.company.id = :companyId AND c.used = true")
    Long countUsedCouponsByCompanyId(@Param("companyId") Long companyId);
}