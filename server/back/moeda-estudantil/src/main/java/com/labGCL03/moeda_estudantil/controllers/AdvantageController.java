package com.labGCL03.moeda_estudantil.controllers;

import com.labGCL03.moeda_estudantil.dto.AdvantageRequestDTO;
import com.labGCL03.moeda_estudantil.dto.AdvantageResponseDTO;
import com.labGCL03.moeda_estudantil.dto.AdvantageUpdateDTO;
import com.labGCL03.moeda_estudantil.entities.Advantage;
import com.labGCL03.moeda_estudantil.exception.ErrorResponse;
import com.labGCL03.moeda_estudantil.services.AdvantageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/advantages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Vantagens", description = "API para gerenciamento de vantagens oferecidas pelas empresas parceiras")
public class AdvantageController {

    private final AdvantageService advantageService;

    @Operation(
            summary = "Listar todas as vantagens",
            description = "Retorna uma lista com todas as vantagens cadastradas no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de vantagens retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<AdvantageResponseDTO>> getAllAdvantages() {
        List<Advantage> advantages = advantageService.findAll();
        List<AdvantageResponseDTO> response = advantages.stream()
            .map(AdvantageResponseDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Buscar vantagem por ID",
            description = "Retorna os dados de uma vantagem específica através do seu ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vantagem encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Vantagem não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<AdvantageResponseDTO> getAdvantageById(
            @Parameter(description = "ID da vantagem") @PathVariable Long id) {
        Advantage advantage = advantageService.findById(id);
        AdvantageResponseDTO response = new AdvantageResponseDTO(advantage);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar vantagens de uma empresa",
            description = "Retorna todas as vantagens oferecidas por uma empresa específica"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de vantagens retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<AdvantageResponseDTO>> getAdvantagesByCompany(
            @Parameter(description = "ID da empresa") @PathVariable Long companyId) {
        List<Advantage> advantages = advantageService.findByCompanyId(companyId);
        List<AdvantageResponseDTO> response = advantages.stream()
            .map(AdvantageResponseDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Buscar vantagens dentro do orçamento",
            description = "Retorna vantagens que custam até o valor especificado em moedas"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de vantagens retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Valor inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/budget/{maxCost}")
    public ResponseEntity<List<AdvantageResponseDTO>> getAdvantagesWithinBudget(
            @Parameter(description = "Custo máximo em moedas", example = "100") 
            @PathVariable Integer maxCost) {
        List<Advantage> advantages = advantageService.findAdvantagesWithinBudget(maxCost);
        List<AdvantageResponseDTO> response = advantages.stream()
            .map(AdvantageResponseDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Buscar vantagens por nome",
            description = "Retorna vantagens que contém o texto especificado no nome"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de vantagens retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Nome inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<List<AdvantageResponseDTO>> searchAdvantagesByName(
            @Parameter(description = "Texto para busca no nome", example = "desconto") 
            @RequestParam String name) {
        List<Advantage> advantages = advantageService.searchByName(name);
        List<AdvantageResponseDTO> response = advantages.stream()
            .map(AdvantageResponseDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Criar nova vantagem",
            description = "Cadastra uma nova vantagem no sistema. Deve ser associada a uma empresa."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vantagem criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<AdvantageResponseDTO> createAdvantage(
            @Parameter(description = "Dados da vantagem a ser criada")
            @Valid @RequestBody AdvantageRequestDTO dto) {
        Advantage advantage = advantageService.create(dto);
        AdvantageResponseDTO response = new AdvantageResponseDTO(advantage);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Atualizar vantagem completamente",
            description = "Atualiza todos os dados de uma vantagem existente (PUT)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vantagem atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Vantagem não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<AdvantageResponseDTO> updateAdvantage(
            @Parameter(description = "ID da vantagem") @PathVariable Long id,
            @Parameter(description = "Dados atualizados da vantagem")
            @Valid @RequestBody AdvantageUpdateDTO dto) {
        
        Advantage advantage = advantageService.update(id, dto);
        AdvantageResponseDTO response = new AdvantageResponseDTO(advantage);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Atualizar vantagem parcialmente",
            description = "Atualiza apenas os campos fornecidos de uma vantagem existente (PATCH)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vantagem atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Vantagem não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AdvantageResponseDTO> partialUpdateAdvantage(
            @Parameter(description = "ID da vantagem") @PathVariable Long id,
            @Parameter(description = "Campos a serem atualizados")
            @RequestBody AdvantageUpdateDTO dto) {
        
        Advantage advantage = advantageService.update(id, dto);
        AdvantageResponseDTO response = new AdvantageResponseDTO(advantage);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Deletar vantagem",
            description = "Remove uma vantagem do sistema. Não é possível deletar vantagens que já foram resgatadas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vantagem deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Não é possível deletar vantagem já resgatada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Vantagem não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdvantage(
            @Parameter(description = "ID da vantagem") 
            @PathVariable Long id) {
        advantageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
