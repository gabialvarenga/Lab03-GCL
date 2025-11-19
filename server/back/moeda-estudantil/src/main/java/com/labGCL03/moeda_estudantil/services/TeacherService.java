package com.labGCL03.moeda_estudantil.services;

import com.labGCL03.moeda_estudantil.dto.TeacherUpdateDTO;
import com.labGCL03.moeda_estudantil.entities.Teacher;
import com.labGCL03.moeda_estudantil.repositories.TeacherRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TransactionService transactionService;

    public Teacher findById(Long id) {
        return teacherRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
    }

    public Teacher findByEmail(String email) {
        return teacherRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
    }

    public List<Teacher> findByInstitution(Long institutionId) {
        return teacherRepository.findByInstitutionId(institutionId);
    }

    @Scheduled(cron = "0 0 2 1 2,8 *") // Executa no dia 1 de fevereiro e agosto às 2h
    public void creditSemesterCoins() {
        String currentPeriod = getCurrentSemesterPeriod();
        log.info("Iniciando crédito semestral para o período: {}", currentPeriod);

        List<Teacher> teachersNeedingCredit = teacherRepository
            .findTeachersNeedingSemesterCredit(currentPeriod);

        for (Teacher teacher : teachersNeedingCredit) {
            creditCoinsToTeacher(teacher, currentPeriod);
        }

        log.info("Crédito semestral concluído. {} professores creditados.", teachersNeedingCredit.size());
    }

    public void creditCoinsToTeacher(Teacher teacher, String period) {
        // Adicionar 1000 moedas ao saldo atual
        teacher.setCurrentBalance(teacher.getCurrentBalance() + 1000);
        teacher.setLastCreditPeriod(period);
        
        teacherRepository.save(teacher);

        // Criar transação de crédito semestral
        transactionService.createSemesterCreditTransaction(teacher, 1000);

        log.info("Professor {} creditado com 1000 moedas para o período {}", 
                teacher.getName(), period);
    }

    public void creditCoinsManually(Long teacherId) {
        Teacher teacher = findById(teacherId);
        String currentPeriod = getCurrentSemesterPeriod();
        
        if (currentPeriod.equals(teacher.getLastCreditPeriod())) {
            throw new IllegalArgumentException("Professor já recebeu crédito neste semestre");
        }

        creditCoinsToTeacher(teacher, currentPeriod);
    }

    private String getCurrentSemesterPeriod() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        
        if (month >= 2 && month <= 7) {
            return year + "-1"; // Primeiro semestre
        } else {
            return year + "-2"; // Segundo semestre
        }
    }

    public List<Teacher> getTeachersNeedingCredit() {
        String currentPeriod = getCurrentSemesterPeriod();
        return teacherRepository.findTeachersNeedingSemesterCredit(currentPeriod);
    }

    public Teacher save(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public Teacher update(Long id, TeacherUpdateDTO dto) {
        Teacher teacher = findById(id);
        
        if (!teacher.getEmail().equals(dto.getEmail())) {
            teacherRepository.findByEmail(dto.getEmail()).ifPresent(t -> {
                if (!t.getId().equals(id)) {
                    throw new IllegalArgumentException("Email já está em uso");
                }
            });
        }
        
        teacher.setName(dto.getName());
        teacher.setEmail(dto.getEmail());
        teacher.setDepartment(dto.getDepartment());
        
        return teacherRepository.save(teacher);
    }
}