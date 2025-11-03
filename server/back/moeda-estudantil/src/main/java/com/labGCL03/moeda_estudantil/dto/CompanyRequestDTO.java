package com.labGCL03.moeda_estudantil.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação de uma nova empresa parceira")
public class CompanyRequestDTO {

    @Schema(description = "Nome da empresa", example = "Tech Solutions LTDA", required = true)
    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @Schema(description = "Email da empresa", example = "contato@techsolutions.com", required = true)
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    @Schema(description = "Senha de acesso", example = "senha123", required = true)
    @NotBlank(message = "Senha é obrigatória")
    private String password;

    @Schema(description = "CNPJ da empresa (deve ser único)", example = "12345678000190", required = true)
    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos")
    private String cnpj;

    @Schema(description = "Endereço completo da empresa", example = "Av. Afonso Pena, 1000, Centro, Belo Horizonte - MG")
    private String address;
}
