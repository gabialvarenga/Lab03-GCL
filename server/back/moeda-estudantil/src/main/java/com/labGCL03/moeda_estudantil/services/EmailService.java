package com.labGCL03.moeda_estudantil.services;

import com.labGCL03.moeda_estudantil.entities.Student;
import com.labGCL03.moeda_estudantil.entities.Teacher;
import com.labGCL03.moeda_estudantil.entities.Coupon;
import com.labGCL03.moeda_estudantil.entities.Company;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    @Async
    public void notifyStudentCoinReceived(Student student, Integer amount, String reason, Teacher teacher) {
        log.info("=== TENTANDO ENVIAR EMAIL ===");
        log.info("Email habilitado: {}", emailEnabled);
        log.info("Aluno: {}", student.getName());
        log.info("Email do aluno: {}", student.getEmail());
        log.info("Remetente configurado: {}", fromEmail);
        
        if (!emailEnabled) {
            log.info("[SIMULAÇÃO] Email para {}: Você recebeu {} moedas do professor {}. Motivo: {}",
                    student.getEmail(), amount, teacher.getName(), reason);
            return;
        }

        try {
            log.info("Criando mensagem de email...");
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(student.getEmail());
            helper.setSubject("🎉 Você recebeu " + amount + " moedas!");

            String htmlContent = buildCoinReceivedEmail(student, amount, reason, teacher);
            helper.setText(htmlContent, true);

            log.info("Enviando email de {} para {}...", fromEmail, student.getEmail());
            mailSender.send(message);
            log.info("✅ Email enviado com sucesso para {}", student.getEmail());

        } catch (MessagingException e) {
            log.error("❌ ERRO ao enviar email para {}: {}", student.getEmail(), e.getMessage());
            log.error("Detalhes do erro:", e);
        } catch (Exception e) {
            log.error("❌ ERRO INESPERADO ao enviar email: {}", e.getMessage());
            log.error("Stack trace:", e);
        }
    }

    @Async
    public void sendCouponToStudent(Student student, Coupon coupon) {
        if (!emailEnabled) {
            log.info("[SIMULAÇÃO] Enviando cupom {} para o aluno {}: {}",
                    coupon.getCode(), student.getName(), student.getEmail());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(student.getEmail());
            helper.setSubject("🎁 Seu cupom de resgate - " + coupon.getAdvantage().getName());

            String htmlContent = buildCouponEmail(student, coupon);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Cupom enviado com sucesso para {}", student.getEmail());

        } catch (MessagingException e) {
            log.error("Erro ao enviar cupom para {}: {}", student.getEmail(), e.getMessage());
        }
    }

    @Async
    public void notifyCompanyRedemption(Company company, Coupon coupon) {
        if (!emailEnabled) {
            log.info("[SIMULAÇÃO] Notificando empresa {} sobre resgate do cupom {}",
                    company.getName(), coupon.getCode());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(company.getEmail());
            helper.setSubject("📢 Nova vantagem resgatada - " + coupon.getAdvantage().getName());

            String htmlContent = buildCompanyNotificationEmail(company, coupon);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Notificação enviada para empresa {}", company.getEmail());

        } catch (MessagingException e) {
            log.error("Erro ao notificar empresa {}: {}", company.getEmail(), e.getMessage());
        }
    }

    @Async
    public void sendEmailVerification(String email, String verificationToken) {
        if (!emailEnabled) {
            log.info("[SIMULAÇÃO] Enviando email de verificação para: {}", email);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("✅ Verifique seu email - Moeda Estudantil");

            String htmlContent = buildVerificationEmail(email, verificationToken);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de verificação enviado para {}", email);

        } catch (MessagingException e) {
            log.error("Erro ao enviar email de verificação para {}: {}", email, e.getMessage());
        }
    }

    private String buildCoinReceivedEmail(Student student, Integer amount, String reason, Teacher teacher) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .highlight { background: #fff; padding: 20px; border-left: 4px solid #667eea; margin: 20px 0; border-radius: 5px; }
                        .amount { font-size: 36px; font-weight: bold; color: #667eea; }
                        .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🎉 Parabéns, %s!</h1>
                        </div>
                        <div class="content">
                            <p>Você acaba de receber moedas no Sistema de Moeda Estudantil!</p>
                            
                            <div class="highlight">
                                <p><strong>Valor recebido:</strong></p>
                                <p class="amount">%d moedas</p>
                                <p><strong>De:</strong> Professor %s</p>
                                <p><strong>Motivo:</strong> %s</p>
                            </div>
                            
                            <p>Continue se dedicando aos seus estudos para acumular mais moedas e trocar por vantagens incríveis!</p>
                            
                            <p>Acesse a plataforma para conferir seu saldo atualizado e ver as vantagens disponíveis.</p>
                        </div>
                        <div class="footer">
                            <p>Sistema de Moeda Estudantil - Este é um email automático, não responda.</p>
                        </div>
                    </div>
                </body>
                </html>
                """, student.getName(), amount, teacher.getName(), reason);
    }

    private String buildCouponEmail(Student student, Coupon coupon) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .coupon { background: #fff; padding: 30px; text-align: center; border: 2px dashed #f5576c; margin: 20px 0; border-radius: 10px; }
                        .code { font-size: 32px; font-weight: bold; color: #f5576c; letter-spacing: 3px; margin: 20px 0; }
                        .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🎁 Seu Cupom Chegou!</h1>
                        </div>
                        <div class="content">
                            <p>Olá <strong>%s</strong>,</p>
                            <p>Aqui está o seu cupom de resgate da vantagem <strong>%s</strong>!</p>
                            
                            <div class="coupon">
                                <p><strong>Código do Cupom:</strong></p>
                                <div class="code">%s</div>
                                <p><strong>Empresa:</strong> %s</p>
                                <p style="color: #666; font-size: 14px; margin-top: 15px;">Apresente este código na empresa para resgatar sua vantagem.</p>
                            </div>
                            
                            <p>Aproveite sua recompensa! 🎉</p>
                        </div>
                        <div class="footer">
                            <p>Sistema de Moeda Estudantil - Este é um email automático, não responda.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(student.getName(), coupon.getAdvantage().getName(), 
                              coupon.getCode(), coupon.getAdvantage().getCompany().getName());
    }

    private String buildCompanyNotificationEmail(Company company, Coupon coupon) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .info { background: #fff; padding: 20px; margin: 20px 0; border-radius: 5px; }
                        .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>📢 Nova Vantagem Resgatada</h1>
                        </div>
                        <div class="content">
                            <p>Olá <strong>%s</strong>,</p>
                            <p>Uma vantagem da sua empresa foi resgatada!</p>
                            
                            <div class="info">
                                <p><strong>Vantagem:</strong> %s</p>
                                <p><strong>Código do Cupom:</strong> %s</p>
                                <p><strong>Aluno:</strong> %s</p>
                                <p><strong>Email:</strong> %s</p>
                            </div>
                            
                            <p>O aluno entrará em contato para resgatar a vantagem.</p>
                        </div>
                        <div class="footer">
                            <p>Sistema de Moeda Estudantil - Este é um email automático, não responda.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(company.getName(), coupon.getAdvantage().getName(), 
                              coupon.getCode(), coupon.getStudent().getName(), 
                              coupon.getStudent().getEmail());
    }

    private String buildVerificationEmail(String email, String verificationToken) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .button { display: inline-block; padding: 15px 30px; background: #667eea; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                        .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>✅ Verificação de Email</h1>
                        </div>
                        <div class="content">
                            <p>Obrigado por se cadastrar no Sistema de Moeda Estudantil!</p>
                            <p>Seu token de verificação é: <strong>%s</strong></p>
                        </div>
                        <div class="footer">
                            <p>Sistema de Moeda Estudantil - Este é um email automático, não responda.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(verificationToken);
    }
}
