package com.labGCL03.moeda_estudantil.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para atualização de um aluno (todos os campos são opcionais)")
public class StudentUpdateDTO {

    @Schema(description = "Nome completo do aluno", example = "João da Silva Santos")
    private String name;

    @Schema(description = "Email do aluno", example = "joao.santos@exemplo.com")
    @Email(message = "Email deve ser válido")
    private String email;

    @Schema(description = "Nova senha de acesso", example = "novaSenha123")
    private String password;

    @Schema(description = "RG do aluno", example = "MG7654321")
    private String rg;

    @Schema(description = "Endereço completo do aluno", example = "Rua Nova, 456, Bairro Novo")
    private String address;

    @Schema(description = "Curso em que o aluno está matriculado", example = "Ciência da Computação")
    private String course;

    @Schema(description = "ID da instituição de ensino", example = "1")
    private Long institutionId;
}
