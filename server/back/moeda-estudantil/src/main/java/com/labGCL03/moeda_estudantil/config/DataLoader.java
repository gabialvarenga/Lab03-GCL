package com.labGCL03.moeda_estudantil.config;

import com.labGCL03.moeda_estudantil.entities.Institution;
import com.labGCL03.moeda_estudantil.repositories.InstitutionRepository;
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
            
            log.info("Carga de dados concluída! {} instituições cadastradas.", institutionNames.size());
        } else {
            log.info("Instituições já cadastradas no sistema. Total: {}", count);
        }
    }
}
