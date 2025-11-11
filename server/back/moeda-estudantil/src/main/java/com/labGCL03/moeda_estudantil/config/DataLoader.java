package com.labGCL03.moeda_estudantil.config;

import com.labGCL03.moeda_estudantil.entities.Company;
import com.labGCL03.moeda_estudantil.entities.Institution;
import com.labGCL03.moeda_estudantil.entities.Teacher;
import com.labGCL03.moeda_estudantil.repositories.InstitutionRepository;
import com.labGCL03.moeda_estudantil.entities.Student;
import com.labGCL03.moeda_estudantil.repositories.StudentRepository;
import com.labGCL03.moeda_estudantil.repositories.TeacherRepository;
import com.labGCL03.moeda_estudantil.enums.Role;
import com.labGCL03.moeda_estudantil.repositories.CompanyRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * DataLoader para pré-carregar instituições de ensino no sistema.
 * As instituições são cadastradas automaticamente na inicialização da aplicação,
 * permitindo que os alunos as selecionem durante o cadastro.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final InstitutionRepository institutionRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        loadInstitutions();
        loadCompanies();
        loadTeachers();
        loadStudents();
    }

    private void loadCompanies() {
        if (companyRepository.count() == 0) {
            log.info("Nenhuma empresa encontrada. Criando empresa de teste...");

            Company c = new Company();
            c.setName("Empresa Demo");
            c.setEmail("empresa.demo@test.com");
            c.setPassword(passwordEncoder.encode("123456"));
            c.setCnpj("12.345.678/0001-90");
            c.setAddress("Av. Demo, 100");
            c.setRole(Role.COMPANY);

            companyRepository.save(c);
            log.info("✓ Empresa criada: {} - Login: {} / Senha: 123456", c.getName(), c.getEmail());
        } else {
            log.info("Empresas já cadastradas no sistema. Total: {}", companyRepository.count());
        }
    }

    private void loadInstitutions() {
        long count = institutionRepository.count();
        
        if (count == 0) {
            log.info("Nenhuma instituição encontrada. Iniciando carga de dados...");
            
            List<String> institutionNames = Arrays.asList(
                "Pontifícia Universidade Católica de Minas Gerais (PUC Minas)",
                "Universidade Federal de Minas Gerais (UFMG)",
                "Universidade Federal de São Paulo (UNIFESP)",
                "Universidade de São Paulo (USP)",
                "Universidade Estadual de Campinas (UNICAMP)",
                "Universidade Federal do Rio de Janeiro (UFRJ)",
                "Universidade de Brasília (UnB)",
                "Universidade Federal do Rio Grande do Sul (UFRGS)",
                "Universidade Federal de Santa Catarina (UFSC)",
                "Universidade Federal do Paraná (UFPR)"
            );
            
            for (String name : institutionNames) {
                Institution institution = new Institution();
                institution.setName(name);
                institution.setCreatedAt(LocalDateTime.now());
                institutionRepository.save(institution);
                log.info("Instituição cadastrada: {}", name);
            }
            
            log.info("Carga de instituições concluída! {} instituições cadastradas.", institutionNames.size());
        } else {
            log.info("Instituições já cadastradas no sistema. Total: {}", count);
        }
    }

    private void loadStudents() {
        if (studentRepository.count() == 0) {
            log.info("Nenhum aluno encontrado. Criando alunos de teste...");
            
            List<Institution> institutions = institutionRepository.findAll();
            if (institutions.isEmpty()) {
                log.error("ERRO: Não há instituições cadastradas. Não é possível criar alunos.");
                return;
            }
            
            Institution defaultInstitution = institutions.get(0);

            // Aluno 1: Com muitas moedas
            Student s1 = new Student();
            s1.setName("Aluno Teste Rico");
            s1.setEmail("aluno.rico@test.com");
            s1.setPassword(passwordEncoder.encode("123456"));
            s1.setCpf("11111111191");
            s1.setRg("MG-11.111.111");
            s1.setAddress("Rua dos Testes, 100");
            s1.setCourse("Engenharia de Software");
            s1.setInstitution(defaultInstitution);
            s1.setRole(Role.STUDENT);
            s1.setCoinBalance(500);
            studentRepository.save(s1);
            log.info("✓ Aluno criado: {} (500 moedas) - Login: aluno.rico@test.com / Senha: 123456", s1.getName());
        } else {
            log.info("Alunos já cadastrados no sistema. Total: {}", studentRepository.count());
        }
    }

    private void loadTeachers() {
        if (teacherRepository.count() == 0) {
            log.info("Nenhum professor encontrado. Criando professores de teste...");
            
            List<Institution> institutions = institutionRepository.findAll();
            if (institutions.isEmpty()) {
                log.error("ERRO: Não há instituições cadastradas. Não é possível criar professores.");
                return;
            }
            
            Institution defaultInstitution = institutions.get(0);

            // Professor 1: Com bastante saldo
            Teacher t1 = new Teacher();
            t1.setName("João Silva");
            t1.setEmail("professor.joao@test.com");
            t1.setPassword(passwordEncoder.encode("123456"));
            t1.setCpf("44444444491");
            t1.setDepartment("Departamento de Engenharia");
            t1.setInstitution(defaultInstitution);
            t1.setRole(Role.TEACHER);
            t1.setCurrentBalance(1000); 
            teacherRepository.save(t1);
            log.info("✓ Professor criado: {} (1000 moedas) - Login: professor.joao@test.com / Senha: 123456", t1.getName());

            // Professor 2: Com saldo médio
            Teacher t2 = new Teacher();
            t2.setName("Maria Santos");
            t2.setEmail("professora.maria@test.com");
            t2.setPassword(passwordEncoder.encode("123456"));
            t2.setCpf("55555555591");
            t2.setDepartment("Departamento de Ciências Humanas");
            t2.setInstitution(defaultInstitution);
            t2.setRole(Role.TEACHER);
            t2.setCurrentBalance(500); // Saldo médio
            teacherRepository.save(t2);
            log.info("✓ Professor criado: {} (500 moedas) - Login: professora.maria@test.com / Senha: 123456", t2.getName());

            // Professor 3: Com pouco saldo
            Teacher t3 = new Teacher();
            t3.setName("Pedro Costa");
            t3.setEmail("professor.pedro@test.com");
            t3.setPassword(passwordEncoder.encode("123456"));
            t3.setCpf("66666666691");
            t3.setDepartment("Departamento de Tecnologia");
            t3.setInstitution(defaultInstitution);
            t3.setRole(Role.TEACHER);
            t3.setCurrentBalance(100);
            teacherRepository.save(t3);
            log.info("✓ Professor criado: {} (100 moedas) - Login: professor.pedro@test.com / Senha: 123456", t3.getName());
        } else {
            log.info("Professores já cadastrados no sistema. Total: {}", teacherRepository.count());
        }
    }
}
