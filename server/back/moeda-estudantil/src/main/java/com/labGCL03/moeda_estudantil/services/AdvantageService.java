package com.labGCL03.moeda_estudantil.services;

import com.labGCL03.moeda_estudantil.dto.AdvantageRequestDTO;
import com.labGCL03.moeda_estudantil.dto.AdvantageUpdateDTO;
import com.labGCL03.moeda_estudantil.entities.Advantage;
import com.labGCL03.moeda_estudantil.entities.Company;
import com.labGCL03.moeda_estudantil.exception.BusinessException;
import com.labGCL03.moeda_estudantil.exception.ResourceNotFoundException;
import com.labGCL03.moeda_estudantil.repositories.AdvantageRepository;
import com.labGCL03.moeda_estudantil.repositories.CompanyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdvantageService {

    private final AdvantageRepository advantageRepository;
    private final CompanyRepository companyRepository;

    public List<Advantage> findAll() {
        return advantageRepository.findAll();
    }

    public Advantage findById(Long id) {
        return advantageRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vantagem", id));
    }

    public List<Advantage> findByCompanyId(Long companyId) {
        // Verifica se a empresa existe
        companyRepository.findById(companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Empresa", companyId));
        
        return advantageRepository.findByCompanyId(companyId);
    }

    public List<Advantage> findAdvantagesWithinBudget(Integer maxCost) {
        if (maxCost <= 0) {
            throw new BusinessException("Valor máximo deve ser positivo");
        }
        return advantageRepository.findAdvantagesWithinBudget(maxCost);
    }

    public List<Advantage> searchByName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("Nome para busca é obrigatório");
        }
        return advantageRepository.searchAdvantagesByName(name);
    }

    public Advantage create(AdvantageRequestDTO dto) {
        log.info("Criando nova vantagem: {}", dto.getName());
        
        // Buscar empresa
        Company company = companyRepository.findById(dto.getCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Empresa", dto.getCompanyId()));
        
        // Criar vantagem
        Advantage advantage = new Advantage();
        advantage.setName(dto.getName());
        advantage.setDescription(dto.getDescription());
        advantage.setCostInCoins(dto.getCostInCoins());
        advantage.setPhoto(dto.getPhoto());
        advantage.setCompany(company);
        
        Advantage savedAdvantage = advantageRepository.save(advantage);
        log.info("Vantagem criada com sucesso. ID: {}", savedAdvantage.getId());
        
        return savedAdvantage;
    }

    public Advantage update(Long id, AdvantageUpdateDTO dto) {
        log.info("Atualizando vantagem ID: {}", id);
        
        Advantage advantage = findById(id);
        
        // Atualizar campos se fornecidos
        if (dto.getName() != null && !dto.getName().isBlank()) {
            advantage.setName(dto.getName());
        }
        
        if (dto.getDescription() != null) {
            advantage.setDescription(dto.getDescription());
        }
        
        if (dto.getCostInCoins() != null) {
            if (dto.getCostInCoins() <= 0) {
                throw new BusinessException("Custo deve ser positivo");
            }
            advantage.setCostInCoins(dto.getCostInCoins());
        }
        
        if (dto.getPhoto() != null) {
            advantage.setPhoto(dto.getPhoto());
        }
        
        Advantage updatedAdvantage = advantageRepository.save(advantage);
        log.info("Vantagem atualizada com sucesso. ID: {}", updatedAdvantage.getId());
        
        return updatedAdvantage;
    }

    public void delete(Long id) {
        log.info("Deletando vantagem ID: {}", id);
        
        Advantage advantage = findById(id);
        
        // Verificar se a vantagem possui cupons gerados
        Long couponsCount = advantageRepository.countCouponsGenerated(id);
        if (couponsCount != null && couponsCount > 0) {
            throw new BusinessException(
                "Não é possível excluir vantagem que já foi resgatada. " +
                "Total de cupons gerados: " + couponsCount
            );
        }
        
        advantageRepository.delete(advantage);
        log.info("Vantagem deletada com sucesso. ID: {}", id);
    }

    public List<Advantage> findByCompanyAndCostRange(Long companyId, Integer minCost, Integer maxCost) {
        if (minCost < 0 || maxCost < 0) {
            throw new BusinessException("Valores de custo devem ser não-negativos");
        }
        if (minCost > maxCost) {
            throw new BusinessException("Custo mínimo não pode ser maior que o custo máximo");
        }
        
        // Verifica se a empresa existe
        companyRepository.findById(companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Empresa", companyId));
        
        return advantageRepository.findAdvantagesByCompanyAndCostRange(companyId, minCost, maxCost);
    }
}
