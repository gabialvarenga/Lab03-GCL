package com.labGCL03.moeda_estudantil.services;

import com.labGCL03.moeda_estudantil.dto.CompanyRequestDTO;
import com.labGCL03.moeda_estudantil.dto.CompanyUpdateDTO;
import com.labGCL03.moeda_estudantil.entities.Company;
import com.labGCL03.moeda_estudantil.enums.Role;
import com.labGCL03.moeda_estudantil.exception.BusinessException;
import com.labGCL03.moeda_estudantil.exception.ResourceNotFoundException;
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
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public Company findById(Long id) {
        return companyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Empresa", id));
    }

    public Company findByEmail(String email) {
        return companyRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Empresa com email " + email + " não encontrada"));
    }

    public Company findByCnpj(String cnpj) {
        return companyRepository.findByCnpj(cnpj)
            .orElseThrow(() -> new ResourceNotFoundException("Empresa com CNPJ " + cnpj + " não encontrada"));
    }

    public Company create(CompanyRequestDTO dto) {
        log.info("Criando nova empresa: {}", dto.getName());
        
        // Validar CNPJ único
        if (companyRepository.existsByCnpj(dto.getCnpj())) {
            throw new BusinessException("CNPJ já cadastrado");
        }
        
        // Validar email único
        if (companyRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("Email já cadastrado");
        }
        
        // Criar empresa
        Company company = new Company();
        company.setName(dto.getName());
        company.setEmail(dto.getEmail());
        company.setPassword(dto.getPassword()); // TODO: Implementar criptografia de senha
        company.setCnpj(dto.getCnpj());
        company.setAddress(dto.getAddress());
        company.setRole(Role.COMPANY);
        
        Company savedCompany = companyRepository.save(company);
        log.info("Empresa criada com sucesso. ID: {}", savedCompany.getId());
        
        return savedCompany;
    }

    public Company update(Long id, CompanyUpdateDTO dto) {
        log.info("Atualizando empresa ID: {}", id);
        
        Company company = findById(id);
        
        // Atualizar campos se fornecidos
        if (dto.getName() != null && !dto.getName().isBlank()) {
            company.setName(dto.getName());
        }
        
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            // Verificar se email já existe para outra empresa
            companyRepository.findByEmail(dto.getEmail()).ifPresent(existingCompany -> {
                if (!existingCompany.getId().equals(id)) {
                    throw new BusinessException("Email já cadastrado para outra empresa");
                }
            });
            company.setEmail(dto.getEmail());
        }
        
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            company.setPassword(dto.getPassword()); // TODO: Implementar criptografia de senha
        }
        
        if (dto.getCnpj() != null && !dto.getCnpj().isBlank()) {
            // Verificar se CNPJ já existe para outra empresa
            companyRepository.findByCnpj(dto.getCnpj()).ifPresent(existingCompany -> {
                if (!existingCompany.getId().equals(id)) {
                    throw new BusinessException("CNPJ já cadastrado para outra empresa");
                }
            });
            company.setCnpj(dto.getCnpj());
        }
        
        if (dto.getAddress() != null) {
            company.setAddress(dto.getAddress());
        }
        
        Company updatedCompany = companyRepository.save(company);
        log.info("Empresa atualizada com sucesso. ID: {}", updatedCompany.getId());
        
        return updatedCompany;
    }

    public void delete(Long id) {
        log.info("Deletando empresa ID: {}", id);
        
        Company company = findById(id);
        
        // Verificar se a empresa possui vantagens cadastradas
        Long advantagesCount = companyRepository.countAdvantagesByCompanyId(id);
        if (advantagesCount != null && advantagesCount > 0) {
            throw new BusinessException(
                "Não é possível excluir empresa com vantagens cadastradas. " +
                "Total de vantagens: " + advantagesCount
            );
        }
        
        companyRepository.delete(company);
        log.info("Empresa deletada com sucesso. ID: {}", id);
    }

    public boolean existsByCnpj(String cnpj) {
        return companyRepository.existsByCnpj(cnpj);
    }

    public boolean existsByEmail(String email) {
        return companyRepository.findByEmail(email).isPresent();
    }
}
