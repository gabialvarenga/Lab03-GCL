package com.labGCL03.moeda_estudantil.controllers;

import com.labGCL03.moeda_estudantil.dto.CompanyRequestDTO;
import com.labGCL03.moeda_estudantil.dto.CompanyResponseDTO;
import com.labGCL03.moeda_estudantil.dto.CompanyUpdateDTO;
import com.labGCL03.moeda_estudantil.entities.Company;
import com.labGCL03.moeda_estudantil.exception.ErrorResponse;
import com.labGCL03.moeda_estudantil.services.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Empresas Parceiras", description = "API para gerenciamento de empresas parceiras no sistema de moeda estudantil")
public class CompanyController {

    private final CompanyService companyService;

    @Operation(
            summary = "Listar todas as empresas",
            description = "Retorna uma lista com todas as empresas parceiras cadastradas no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de empresas retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> getAllCompanies() {
        List<Company> companies = companyService.findAll();
        List<CompanyResponseDTO> response = companies.stream()
            .map(CompanyResponseDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Buscar empresa por ID",
            description = "Retorna os dados de uma empresa específica através do seu ID. Requer autenticação."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa encontrada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> getCompanyById(
            @Parameter(description = "ID da empresa") @PathVariable Long id) {
        Company company = companyService.findById(id);
        CompanyResponseDTO response = new CompanyResponseDTO(company);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Buscar empresa por email",
            description = "Retorna os dados de uma empresa através do seu email"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<CompanyResponseDTO> getCompanyByEmail(
            @Parameter(description = "Email da empresa", example = "contato@empresa.com") 
            @PathVariable String email) {
        Company company = companyService.findByEmail(email);
        CompanyResponseDTO response = new CompanyResponseDTO(company);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Buscar empresa por CNPJ",
            description = "Retorna os dados de uma empresa através do seu CNPJ"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<CompanyResponseDTO> getCompanyByCnpj(
            @Parameter(description = "CNPJ da empresa", example = "12345678000190") 
            @PathVariable String cnpj) {
        Company company = companyService.findByCnpj(cnpj);
        CompanyResponseDTO response = new CompanyResponseDTO(company);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Criar nova empresa",
            description = "Cadastra uma nova empresa parceira no sistema. O CNPJ e email devem ser únicos. Endpoint público (não requer autenticação)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empresa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou CNPJ/email já cadastrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirements // Endpoint público
    @PostMapping
    public ResponseEntity<CompanyResponseDTO> createCompany(
            @Parameter(description = "Dados da empresa a ser criada")
            @Valid @RequestBody CompanyRequestDTO dto) {
        Company company = companyService.create(dto);
        CompanyResponseDTO response = new CompanyResponseDTO(company);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Atualizar empresa completamente",
            description = "Atualiza todos os dados de uma empresa existente (PUT)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(
            @Parameter(description = "ID da empresa") @PathVariable Long id,
            @Parameter(description = "Dados atualizados da empresa")
            @Valid @RequestBody CompanyUpdateDTO dto) {
        
        Company company = companyService.update(id, dto);
        CompanyResponseDTO response = new CompanyResponseDTO(company);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Atualizar empresa parcialmente",
            description = "Atualiza apenas os campos fornecidos de uma empresa existente (PATCH)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> partialUpdateCompany(
            @Parameter(description = "ID da empresa") @PathVariable Long id,
            @Parameter(description = "Campos a serem atualizados")
            @RequestBody CompanyUpdateDTO dto) {
        
        Company company = companyService.update(id, dto);
        CompanyResponseDTO response = new CompanyResponseDTO(company);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Deletar empresa",
            description = "Remove uma empresa do sistema. Não é possível deletar empresas que possuem vantagens cadastradas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Empresa deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Não é possível deletar empresa com vantagens cadastradas",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(
            @Parameter(description = "ID da empresa") 
            @PathVariable Long id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
