package com.labGCL03.moeda_estudantil.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação de um novo aluno")
public class StudentRequestDTO {

    @Schema(description = "Nome completo do aluno", example = "João da Silva", required = true)
    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @Schema(description = "Email do aluno", example = "joao.silva@exemplo.com", required = true)
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    @Schema(description = "Senha de acesso", example = "senha123", required = true)
    @NotBlank(message = "Senha é obrigatória")
    private String password;

    @Schema(description = "CPF do aluno (deve ser único)", example = "12345678900", required = true)
    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @Schema(description = "RG do aluno", example = "MG1234567")
    private String rg;

    @Schema(description = "Endereço completo do aluno", example = "Rua das Flores, 123, Centro")
    private String address;

    @Schema(description = "Curso em que o aluno está matriculado", example = "Engenharia de Software", required = true)
    @NotBlank(message = "Curso é obrigatório")
    private String course;

    @Schema(description = "ID da instituição de ensino", example = "1", required = true)
    @NotNull(message = "ID da instituição é obrigatório")
    private Long institutionId;
}
