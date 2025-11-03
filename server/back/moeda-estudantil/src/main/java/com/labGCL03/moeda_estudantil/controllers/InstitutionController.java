package com.labGCL03.moeda_estudantil.controllers;

import com.labGCL03.moeda_estudantil.dto.InstitutionDTO;
import com.labGCL03.moeda_estudantil.services.InstitutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/institutions")
@RequiredArgsConstructor
@Tag(name = "Instituições", description = "Endpoints para consultar instituições de ensino pré-cadastradas")
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

}
