package com.labGCL03.moeda_estudantil.services;

import com.labGCL03.moeda_estudantil.entities.Student;
import com.labGCL03.moeda_estudantil.entities.Teacher;
import com.labGCL03.moeda_estudantil.entities.Coupon;
import com.labGCL03.moeda_estudantil.entities.Company;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Async
    public void notifyStudentCoinReceived(Student student, Integer amount, String reason, Teacher teacher) {
        // Implementação real do envio de email
        log.info("Enviando email para {}: Você recebeu {} moedas do professor {}. Motivo: {}",
                student.getEmail(), amount, teacher.getName(), reason);
        
        // Aqui você integraria com um serviço de email real como:
        // - Spring Mail
        // - AWS SES
        // - SendGrid
        // etc.
    }

    @Async
    public void sendCouponToStudent(Student student, Coupon coupon) {
        log.info("Enviando cupom {} para o aluno {}: {}",
                coupon.getCode(), student.getName(), student.getEmail());
        
        // Implementação real do envio do cupom por email
    }

    @Async
    public void notifyCompanyRedemption(Company company, Coupon coupon) {
        log.info("Notificando empresa {} sobre resgate do cupom {}",
                company.getName(), coupon.getCode());
        
        // Implementação real da notificação para a empresa
    }

    @Async
    public void sendEmailVerification(String email, String verificationToken) {
        log.info("Enviando email de verificação para: {}", email);
        
        // Implementação real do envio de email de verificação
    }
}