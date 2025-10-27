package com.labGCL03.moeda_estudantil.services;

import com.labGCL03.moeda_estudantil.entities.Student;
import com.labGCL03.moeda_estudantil.entities.Transaction;
import com.labGCL03.moeda_estudantil.repositories.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final TransactionService transactionService;

    public Student findById(Long id) {
        return studentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
    }

    public Student findByEmail(String email) {
        return studentRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
    }

    public Student findByCpf(String cpf) {
        return studentRepository.findByCpf(cpf)
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
    }

    public List<Student> findByCourse(String course) {
        return studentRepository.findByCourse(course);
    }

    public List<Student> findByInstitution(Long institutionId) {
        return studentRepository.findByInstitutionId(institutionId);
    }

    public Student save(Student student) {
        // Validar CPF único
        if (student.getCpf() != null && studentRepository.existsByCpf(student.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        
        return studentRepository.save(student);
    }

    public Integer getStudentBalance(Long studentId) {
        Student student = findById(studentId);
        return student.getCoinBalance();
    }

    public List<Transaction> getStudentTransactionHistory(Long studentId) {
        return transactionService.getStudentTransactionHistory(studentId);
    }

    public List<Student> getStudentsWithMinimumBalance(Integer minBalance) {
        return studentRepository.findStudentsWithMinimumBalance(minBalance);
    }

    public Long getTotalCoinsInInstitution(Long institutionId) {
        return studentRepository.getTotalCoinsInInstitution(institutionId);
    }

    public Double getAverageCoinsInInstitution(Long institutionId) {
        return studentRepository.getAverageCoinsInInstitution(institutionId);
    }

    public void updateStudentBalance(Long studentId, Integer newBalance) {
        if (newBalance < 0) {
            throw new IllegalArgumentException("Saldo não pode ser negativo");
        }
        
        Student student = findById(studentId);
        student.setCoinBalance(newBalance);
        studentRepository.save(student);
    }

    public boolean canAffordAdvantage(Long studentId, Integer cost) {
        Student student = findById(studentId);
        return student.getCoinBalance() >= cost;
    }
}