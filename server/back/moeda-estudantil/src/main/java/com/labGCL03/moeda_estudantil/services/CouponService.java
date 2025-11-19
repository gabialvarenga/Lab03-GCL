package com.labGCL03.moeda_estudantil.services;

import com.labGCL03.moeda_estudantil.entities.Advantage;
import com.labGCL03.moeda_estudantil.entities.Company;
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
        // Buscar aluno e vantagem com relações eager-loaded
        Student student = studentRepository.findByIdWithInstitution(studentId)
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        
        Advantage advantage = advantageRepository.findByIdWithCompany(advantageId)
            .orElseThrow(() -> new RuntimeException("Vantagem não encontrada"));

        // Validações
        if (student.getCoinBalance() < advantage.getCostInCoins()) {
            throw new IllegalArgumentException("Saldo insuficiente. Você possui " + 
                student.getCoinBalance() + " moedas, mas esta vantagem custa " + 
                advantage.getCostInCoins() + " moedas.");
        }

        // Verifica se há cupons disponíveis
        if (!advantage.isAvailable()) {
            throw new IllegalArgumentException("Não há mais cupons disponíveis para esta vantagem");
        }

        // Decrementa a quantidade disponível da vantagem
        advantage.decrementQuantity();
        advantageRepository.save(advantage);

        // Usar o método da entidade Student para resgatar vantagem (desconta saldo)
        Coupon coupon = student.redeemAdvantage(advantage);
        
        // Garantir que as relações do coupon estejam definidas antes de salvar
        coupon.setStudent(student);
        coupon.setAdvantage(advantage);
        
        // Persistir as alterações (student tem saldo atualizado)
        studentRepository.save(student);
        Coupon savedCoupon = couponRepository.save(coupon);
        
        // Importante: manter as referências carregadas no coupon retornado
        savedCoupon.setStudent(student);
        savedCoupon.setAdvantage(advantage);

        // Criar transação de resgate para histórico
        transactionService.createRedemptionTransaction(student, advantage.getCostInCoins(), advantage.getName());

        // Enviar email com cupom para o aluno
        emailService.sendCouponToStudent(student, savedCoupon);
        
        // Enviar email de notificação para a empresa parceira
        Company company = advantage.getCompany();
        if (company != null) {
            emailService.notifyCompanyRedemption(company, savedCoupon);
        }

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