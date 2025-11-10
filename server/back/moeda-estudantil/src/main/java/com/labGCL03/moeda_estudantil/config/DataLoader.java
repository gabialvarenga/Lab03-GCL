package com.labGCL03.moeda_estudantil.config;

import com.labGCL03.moeda_estudantil.entities.Institution;
import com.labGCL03.moeda_estudantil.repositories.InstitutionRepository;
import com.labGCL03.moeda_estudantil.entities.Student;
import com.labGCL03.moeda_estudantil.repositories.StudentRepository;
import com.labGCL03.moeda_estudantil.enums.Role;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        loadInstitutions();
    }

    private void loadInstitutions() {
        // Verifica se já existem instituições cadastradas
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
            
            // Criar alguns alunos fake com moedas para testes de resgate
            List<Institution> savedInstitutions = institutionRepository.findAll();
            if (!savedInstitutions.isEmpty()) {
                Institution inst0 = savedInstitutions.get(0);

                Student s1 = new Student();
                s1.setName("Aluno Teste 1");
                s1.setEmail("aluno1@example.com");
                s1.setPassword(passwordEncoder.encode("password"));
                s1.setCpf("00000000191");
                s1.setRg("MG-12.345.678");
                s1.setAddress("Rua Exemplo, 123");
                s1.setCourse("Engenharia de Software");
                s1.setInstitution(inst0);
                s1.setRole(Role.STUDENT);
                s1.setCoinBalance(150); // moedas para testar resgate
                studentRepository.save(s1);

                Student s2 = new Student();
                s2.setName("Aluno Teste 2");
                s2.setEmail("aluno2@example.com");
                s2.setPassword(passwordEncoder.encode("password"));
                s2.setCpf("00000000291");
                s2.setRg("MG-98.765.432");
                s2.setAddress("Avenida Teste, 45");
                s2.setCourse("Direito");
                s2.setInstitution(inst0);
                s2.setRole(Role.STUDENT);
                s2.setCoinBalance(60);
                studentRepository.save(s2);

                Student s3 = new Student();
                s3.setName("Aluno Teste 3");
                s3.setEmail("aluno3@example.com");
                s3.setPassword(passwordEncoder.encode("password"));
                s3.setCpf("00000000391");
                s3.setRg("SP-11.222.333");
                s3.setAddress("Praça Demo, 9");
                s3.setCourse("Administração");
                s3.setInstitution(inst0);
                s3.setRole(Role.STUDENT);
                s3.setCoinBalance(0); // aluno sem moedas
                studentRepository.save(s3);
                log.info("Alunos de teste criados com saldos: 150, 60, 0");
            }

            log.info("Carga de dados concluída! {} instituições cadastradas.", institutionNames.size());
        } else {
            log.info("Instituições já cadastradas no sistema. Total: {}", count);
        }

        // Se não houver alunos cadastrados (por exemplo ao usar um banco com instituições já presentes),
        // criamos alguns alunos de teste com saldos para facilitar testes de resgate.
        if (studentRepository.count() == 0) {
            List<Institution> savedInstitutions = institutionRepository.findAll();
            if (!savedInstitutions.isEmpty()) {
                Institution inst0 = savedInstitutions.get(0);

                Student s1 = new Student();
                s1.setName("Aluno Demo A");
                s1.setEmail("demo.aluno1@example.com");
                s1.setPassword(passwordEncoder.encode("password"));
                s1.setCpf("10000000191");
                s1.setRg("RG-100.001");
                s1.setAddress("Teste 1");
                s1.setCourse("Ciência da Computação");
                s1.setInstitution(inst0);
                s1.setRole(Role.STUDENT);
                s1.setCoinBalance(120);
                studentRepository.save(s1);

                Student s2 = new Student();
                s2.setName("Aluno Demo B");
                s2.setEmail("demo.aluno2@example.com");
                s2.setPassword(passwordEncoder.encode("password"));
                s2.setCpf("10000000291");
                s2.setRg("RG-100.002");
                s2.setAddress("Teste 2");
                s2.setCourse("Engenharia");
                s2.setInstitution(inst0);
                s2.setRole(Role.STUDENT);
                s2.setCoinBalance(40);
                studentRepository.save(s2);

                log.info("Alunos de teste adicionais criados (demo.aluno1@example.com, demo.aluno2@example.com)");
            }
        }
    }
}
