package com.labGCL03.moeda_estudantil.services;

import com.labGCL03.moeda_estudantil.entities.Student;
import com.labGCL03.moeda_estudantil.entities.Teacher;
import com.labGCL03.moeda_estudantil.entities.Transaction;
import com.labGCL03.moeda_estudantil.enums.TransactionType;
import com.labGCL03.moeda_estudantil.repositories.StudentRepository;
import com.labGCL03.moeda_estudantil.repositories.TeacherRepository;
import com.labGCL03.moeda_estudantil.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final EmailService emailService;

    public Transaction sendCoins(Long teacherId, Long studentId, Integer amount, String reason) {
        // Validar parâmetros
        if (amount <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Motivo é obrigatório");
        }

        // Buscar professor e aluno
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
        
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        // Verificar saldo do professor
        if (teacher.getCurrentBalance() < amount) {
            throw new IllegalArgumentException("Saldo insuficiente do professor");
        }

        // Criar transação
        Transaction transaction = new Transaction();
        transaction.setSender(teacher);
        transaction.setReceiver(student);
        transaction.setAmount(amount);
        transaction.setReason(reason);
        transaction.setType(TransactionType.SENT);
        transaction.setDate(LocalDateTime.now());

        // Atualizar saldos
        teacher.setCurrentBalance(teacher.getCurrentBalance() - amount);
        student.setCoinBalance(student.getCoinBalance() + amount);

        // Salvar no banco
        teacherRepository.save(teacher);
        studentRepository.save(student);
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Enviar notificação por email (assíncrono)
        emailService.notifyStudentCoinReceived(student, amount, reason, teacher);

        return savedTransaction;
    }

    public List<Transaction> getStudentTransactionHistory(Long studentId) {
        return transactionRepository.findByReceiverIdOrderByDateDesc(studentId);
    }

    public List<Transaction> getStudentTransactionHistory(Long studentId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findTransactionsByUserIdAndDateRange(studentId, startDate, endDate);
    }

    public List<Transaction> getTeacherTransactionHistory(Long teacherId) {
        return transactionRepository.findBySenderIdOrderByDateDesc(teacherId);
    }

    public List<Transaction> getTeacherTransactionHistory(Long teacherId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findTransactionsByUserIdAndDateRange(teacherId, startDate, endDate);
    }

    public List<Transaction> getUserTransactionHistory(Long userId) {
        return transactionRepository.findAllTransactionsByUserId(userId);
    }

    public List<Transaction> getUserTransactionHistory(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findTransactionsByUserIdAndDateRange(userId, startDate, endDate);
    }

    public Transaction createSemesterCreditTransaction(Teacher teacher, Integer amount) {
        Transaction transaction = new Transaction();
        transaction.setSender(null); // Sistema
        transaction.setReceiver(teacher);
        transaction.setAmount(amount);
        transaction.setReason("Crédito semestral de moedas");
        transaction.setType(TransactionType.RECEIVED);
        transaction.setDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    public Transaction createRedemptionTransaction(Student student, Integer amount, String advantageName) {
        Transaction transaction = new Transaction();
        transaction.setSender(student);
        transaction.setReceiver(null); // Sistema/Empresa
        transaction.setAmount(amount);
        transaction.setReason("Resgate de vantagem: " + advantageName);
        transaction.setType(TransactionType.REDEEMED);
        transaction.setDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }
}