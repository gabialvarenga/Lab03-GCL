package com.labGCL03.moeda_estudantil.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estrutura padrão de resposta de erro")
public class ErrorResponse {
    
    @Schema(description = "Data e hora do erro", example = "2025-10-30T10:30:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "Código de status HTTP", example = "404")
    private int status;
    
    @Schema(description = "Tipo de erro", example = "Not Found")
    private String error;
    
    @Schema(description = "Mensagem descritiva do erro", example = "Aluno com ID 1 não encontrado")
    private String message;
    
    @Schema(description = "Caminho da requisição que gerou o erro", example = "/api/students/1")
    private String path;
    
    @Schema(description = "Erros de validação por campo (quando aplicável)")
    private Map<String, String> validationErrors;

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
