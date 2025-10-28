package com.labGCL03.moeda_estudantil.services;

import com.labGCL03.moeda_estudantil.entities.Advantage;
import com.labGCL03.moeda_estudantil.entities.Coupon;
import com.labGCL03.moeda_estudantil.entities.Student;
import com.labGCL03.moeda_estudantil.repositories.AdvantageRepository;
import com.labGCL03.moeda_estudantil.repositories.CouponRepository;
import com.labGCL03.moeda_estudantil.repositories.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;
    private final StudentRepository studentRepository;
    private final AdvantageRepository advantageRepository;
    private final TransactionService transactionService;
    private final EmailService emailService;

    public Coupon redeemAdvantage(Long studentId, Long advantageId) {
        // Buscar aluno e vantagem
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        
        Advantage advantage = advantageRepository.findById(advantageId)
            .orElseThrow(() -> new RuntimeException("Vantagem não encontrada"));

        // Validações
        if (student.getCoinBalance() < advantage.getCostInCoins()) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }

        // Usar o método da entidade Student para resgatar vantagem
        Coupon coupon = student.redeemAdvantage(advantage);
        
        // Persistir as alterações
        studentRepository.save(student);
        Coupon savedCoupon = couponRepository.save(coupon);

        // Criar transação de resgate
        transactionService.createRedemptionTransaction(student, advantage.getCostInCoins(), advantage.getName());

        // Enviar emails
        emailService.sendCouponToStudent(student, savedCoupon);
        emailService.notifyCompanyRedemption(advantage.getCompany(), savedCoupon);

        return savedCoupon;
    }

    public void markCouponAsUsed(String code) {
        Coupon coupon = couponRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Cupom não encontrado"));

        if (coupon.isUsed()) {
            throw new IllegalArgumentException("Cupom já foi utilizado");
        }

        coupon.markAsUsed();
        couponRepository.save(coupon);
    }

    public List<Coupon> getStudentCoupons(Long studentId) {
        return couponRepository.findByStudentIdOrderByGeneratedDateDesc(studentId);
    }

    public List<Coupon> getCompanyCoupons(Long companyId) {
        return couponRepository.findCouponsByCompanyId(companyId);
    }

    public Coupon validateCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Cupom não encontrado"));

        if (coupon.isUsed()) {
            throw new IllegalArgumentException("Cupom já foi utilizado");
        }

        return coupon;
    }
}