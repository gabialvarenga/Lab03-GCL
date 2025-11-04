package com.labGCL03.moeda_estudantil.services;

import com.labGCL03.moeda_estudantil.dto.StudentRequestDTO;
import com.labGCL03.moeda_estudantil.dto.StudentUpdateDTO;
import com.labGCL03.moeda_estudantil.entities.Institution;
import com.labGCL03.moeda_estudantil.entities.Student;
import com.labGCL03.moeda_estudantil.entities.Transaction;
import com.labGCL03.moeda_estudantil.enums.Role;
import com.labGCL03.moeda_estudantil.exception.BusinessException;
import com.labGCL03.moeda_estudantil.exception.ResourceNotFoundException;
import com.labGCL03.moeda_estudantil.repositories.InstitutionRepository;
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
    private final InstitutionRepository institutionRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student findById(Long id) {
        return studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
    }

    public Student findByEmail(String email) {
        return studentRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno com email " + email + " não encontrado"));
    }

    public Student findByCpf(String cpf) {
        return studentRepository.findByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno com CPF " + cpf + " não encontrado"));
    }

    public List<Student> findByCourse(String course) {
        return studentRepository.findByCourse(course);
    }

    public List<Student> findByInstitution(Long institutionId) {
        return studentRepository.findByInstitutionId(institutionId);
    }

    public Student create(StudentRequestDTO dto) {
        // Validar CPF único
        if (studentRepository.existsByCpf(dto.getCpf())) {
            throw new BusinessException("CPF já cadastrado");
        }
        
        // Validar email único
        if (studentRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("Email já cadastrado");
        }
        
        // Buscar instituição
        Institution institution = institutionRepository.findById(dto.getInstitutionId())
            .orElseThrow(() -> new ResourceNotFoundException("Instituição", dto.getInstitutionId()));
        
        // Criar estudante
        Student student = new Student();
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setPassword(passwordEncoder.encode(dto.getPassword()));
        student.setCpf(dto.getCpf());
        student.setRg(dto.getRg());
        student.setAddress(dto.getAddress());
        student.setCourse(dto.getCourse());
        student.setInstitution(institution);
        student.setRole(Role.STUDENT);
        student.setCoinBalance(0);
        
        return studentRepository.save(student);
    }

    public Student update(Long id, StudentUpdateDTO dto) {
        Student student = findById(id);
        
        // Atualizar campos se fornecidos
        if (dto.getName() != null && !dto.getName().isBlank()) {
            student.setName(dto.getName());
        }
        
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            // Verificar se email já existe para outro estudante
            studentRepository.findByEmail(dto.getEmail()).ifPresent(existingStudent -> {
                if (!existingStudent.getId().equals(id)) {
                    throw new BusinessException("Email já cadastrado para outro estudante");
                }
            });
            student.setEmail(dto.getEmail());
        }
        
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            student.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        
        if (dto.getRg() != null) {
            student.setRg(dto.getRg());
        }
        
        if (dto.getAddress() != null) {
            student.setAddress(dto.getAddress());
        }
        
        if (dto.getCourse() != null && !dto.getCourse().isBlank()) {
            student.setCourse(dto.getCourse());
        }
        
        if (dto.getInstitutionId() != null) {
            Institution institution = institutionRepository.findById(dto.getInstitutionId())
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", dto.getInstitutionId()));
            student.setInstitution(institution);
        }
        
        return studentRepository.save(student);
    }

    public void delete(Long id) {
        Student student = findById(id);
        
        // Verificar se o aluno tem transações pendentes ou cupons ativos
        // (Implementar validações de negócio conforme necessário)
        
        studentRepository.delete(student);
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