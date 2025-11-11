package com.labGCL03.moeda_estudantil.controllers;

import com.labGCL03.moeda_estudantil.dto.TeacherResponseDTO;
import com.labGCL03.moeda_estudantil.dto.TeacherUpdateDTO;
import com.labGCL03.moeda_estudantil.dto.TransactionResponseDTO;
import com.labGCL03.moeda_estudantil.dto.TransferCoinsDTO;
import com.labGCL03.moeda_estudantil.entities.Teacher;
import com.labGCL03.moeda_estudantil.entities.Transaction;
import com.labGCL03.moeda_estudantil.exception.ErrorResponse;
import com.labGCL03.moeda_estudantil.services.TeacherService;
import com.labGCL03.moeda_estudantil.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Professores", description = "API para gerenciamento de professores no sistema de moeda estudantil")
public class TeacherController {

    private final TeacherService teacherService;
    private final TransactionService transactionService;

    @Operation(
            summary = "Buscar professor por ID",
            description = "Retorna os dados de um professor específico através do seu ID. Requer autenticação."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Professor encontrado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Professor não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponseDTO> getTeacherById(
            @Parameter(description = "ID do professor", required = true) @PathVariable Long id) {
        Teacher teacher = teacherService.findById(id);
        TeacherResponseDTO response = new TeacherResponseDTO(teacher);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Transferir moedas para um aluno",
            description = "Permite que um professor transfira moedas para um aluno. Requer role TEACHER."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Saldo insuficiente ou dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer TEACHER)"),
            @ApiResponse(responseCode = "404", description = "Professor ou aluno não encontrados",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/{id}/transfer")
    public ResponseEntity<Void> transferCoins(
            @Parameter(description = "ID do professor", required = true) @PathVariable Long id,
            @Parameter(description = "Dados da transferência", required = true)
            @Valid @RequestBody TransferCoinsDTO dto) {
        
        transactionService.sendCoins(id, dto.getStudentId(), dto.getAmount(), dto.getReason());
        
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Obter saldo do professor",
            description = "Retorna o saldo atual de moedas do professor. Requer autenticação."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Professor não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}/balance")
    public ResponseEntity<Integer> getBalance(
            @Parameter(description = "ID do professor", required = true) @PathVariable Long id) {
        Teacher teacher = teacherService.findById(id);
        return ResponseEntity.ok(teacher.getCurrentBalance());
    }

    @Operation(
            summary = "Obter transações do professor",
            description = "Retorna o histórico de transações do professor. Requer autenticação."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transações retornadas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Professor não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactions(
            @Parameter(description = "ID do professor", required = true) @PathVariable Long id) {
        List<Transaction> transactions = transactionService.getTeacherTransactionHistory(id);
        List<TransactionResponseDTO> response = transactions.stream()
                .map(TransactionResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Atualizar perfil do professor",
            description = "Atualiza os dados do professor. Requer role TEACHER (próprio perfil) ou ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Professor atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para atualizar este professor"),
            @ApiResponse(responseCode = "404", description = "Professor não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponseDTO> updateTeacher(
            @Parameter(description = "ID do professor", required = true) @PathVariable Long id,
            @Parameter(description = "Dados atualizados do professor", required = true)
            @Valid @RequestBody TeacherUpdateDTO dto) {
        
        Teacher teacher = teacherService.update(id, dto);
        TeacherResponseDTO response = new TeacherResponseDTO(teacher);
        
        return ResponseEntity.ok(response);
    }
}
