package com.labGCL03.moeda_estudantil.controllers;

import com.labGCL03.moeda_estudantil.dto.InstitutionDTO;
import com.labGCL03.moeda_estudantil.dto.InstitutionRequestDTO;
import com.labGCL03.moeda_estudantil.services.InstitutionService;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/api/institutions")
@RequiredArgsConstructor
@Tag(name = "Instituições", description = "Endpoints para gerenciar instituições de ensino")
public class InstitutionController {

    private final InstitutionService institutionService;

    @GetMapping
    @Operation(
        summary = "Listar todas as instituições",
        description = "Retorna a lista de todas as instituições de ensino cadastradas no sistema. " +
                "Os alunos devem selecionar uma destas instituições ao realizar o cadastro."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de instituições retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = InstitutionDTO.class)
            )
        )
    })
    public ResponseEntity<List<InstitutionDTO>> getAllInstitutions() {
        List<InstitutionDTO> institutions = institutionService.findAll();
        return ResponseEntity.ok(institutions);
    }

    @PostMapping
    @Operation(
        summary = "Cadastrar nova instituição",
        description = "Cria uma nova instituição de ensino no sistema. O nome deve ser único."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Instituição cadastrada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = InstitutionDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou nome já cadastrado"
        )
    })
    public ResponseEntity<InstitutionDTO> createInstitution(@Valid @RequestBody InstitutionRequestDTO dto) {
        InstitutionDTO created = institutionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar instituição por ID",
        description = "Retorna os detalhes de uma instituição específica"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Instituição encontrada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = InstitutionDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Instituição não encontrada"
        )
    })
    public ResponseEntity<InstitutionDTO> getInstitutionById(@PathVariable Long id) {
        InstitutionDTO institution = institutionService.findById(id);
        return ResponseEntity.ok(institution);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar instituição",
        description = "Atualiza os dados de uma instituição existente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Instituição atualizada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = InstitutionDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Instituição não encontrada"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou nome já cadastrado"
        )
    })
    public ResponseEntity<InstitutionDTO> updateInstitution(
            @PathVariable Long id,
            @Valid @RequestBody InstitutionRequestDTO dto) {
        InstitutionDTO updated = institutionService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir instituição",
        description = "Remove uma instituição do sistema. Não é possível excluir instituições " +
                "que possuem alunos ou professores vinculados."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Instituição excluída com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Instituição não encontrada"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Não é possível excluir instituição com alunos ou professores vinculados"
        )
    })
    public ResponseEntity<Void> deleteInstitution(@PathVariable Long id) {
        institutionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/name/{name}")
    @Operation(
        summary = "Buscar instituição por nome",
        description = "Retorna uma instituição pelo nome exato"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Instituição encontrada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = InstitutionDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Instituição não encontrada"
        )
    })
    public ResponseEntity<InstitutionDTO> getInstitutionByName(@PathVariable String name) {
        InstitutionDTO institution = institutionService.findByName(name);
        return ResponseEntity.ok(institution);
    }

    @GetMapping("/{id}/students/count")
    @Operation(
        summary = "Contar alunos de uma instituição",
        description = "Retorna o número de alunos cadastrados em uma instituição"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Contagem retornada com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Instituição não encontrada"
        )
    })
    public ResponseEntity<Long> countStudents(@PathVariable Long id) {
        Long count = institutionService.countStudents(id);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{id}/teachers/count")
    @Operation(
        summary = "Contar professores de uma instituição",
        description = "Retorna o número de professores cadastrados em uma instituição"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Contagem retornada com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Instituição não encontrada"
        )
    })
    public ResponseEntity<Long> countTeachers(@PathVariable Long id) {
        Long count = institutionService.countTeachers(id);
        return ResponseEntity.ok(count);
    }
}
