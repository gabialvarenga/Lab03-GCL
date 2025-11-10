package com.labGCL03.moeda_estudantil.config;

import com.labGCL03.moeda_estudantil.entities.Company;
import com.labGCL03.moeda_estudantil.entities.Institution;
import com.labGCL03.moeda_estudantil.repositories.InstitutionRepository;
import com.labGCL03.moeda_estudantil.entities.Student;
import com.labGCL03.moeda_estudantil.repositories.StudentRepository;
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
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        loadInstitutions();
        loadCompanies();
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
            s1.setCoinBalance(500); // Bastante moedas para testar
            studentRepository.save(s1);
            log.info("✓ Aluno criado: {} (500 moedas) - Login: aluno.rico@test.com / Senha: 123456", s1.getName());

            // Aluno 2: Com poucas moedas
            Student s2 = new Student();
            s2.setName("Aluno Teste Médio");
            s2.setEmail("aluno.medio@test.com");
            s2.setPassword(passwordEncoder.encode("123456"));
            s2.setCpf("22222222291");
            s2.setRg("MG-22.222.222");
            s2.setAddress("Avenida dos Testes, 200");
            s2.setCourse("Administração");
            s2.setInstitution(defaultInstitution);
            s2.setRole(Role.STUDENT);
            s2.setCoinBalance(50); // Poucas moedas
            studentRepository.save(s2);
            log.info("✓ Aluno criado: {} (50 moedas) - Login: aluno.medio@test.com / Senha: 123456", s2.getName());

            // Aluno 3: Sem moedas
            Student s3 = new Student();
            s3.setName("Aluno Teste Pobre");
            s3.setEmail("aluno.pobre@test.com");
            s3.setPassword(passwordEncoder.encode("123456"));
            s3.setCpf("33333333391");
            s3.setRg("MG-33.333.333");
            s3.setAddress("Praça dos Testes, 300");
            s3.setCourse("Direito");
            s3.setInstitution(defaultInstitution);
            s3.setRole(Role.STUDENT);
            s3.setCoinBalance(0); // Sem moedas
            studentRepository.save(s3);
            log.info("✓ Aluno criado: {} (0 moedas) - Login: aluno.pobre@test.com / Senha: 123456", s3.getName());

            log.info("═══════════════════════════════════════════════════════════");
            log.info("ALUNOS DE TESTE CRIADOS COM SUCESSO!");
            log.info("═══════════════════════════════════════════════════════════");
            log.info("Login 1: aluno.rico@test.com  | Senha: 123456 | Saldo: 500 moedas");
            log.info("Login 2: aluno.medio@test.com | Senha: 123456 | Saldo: 50 moedas");
            log.info("Login 3: aluno.pobre@test.com | Senha: 123456 | Saldo: 0 moedas");
            log.info("═══════════════════════════════════════════════════════════");
        } else {
            log.info("Alunos já cadastrados no sistema. Total: {}", studentRepository.count());
        }
    }
}
