package com.labGCL03.moeda_estudantil.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para atualização de uma empresa parceira")
public class CompanyUpdateDTO {

    @Schema(description = "Nome da empresa", example = "Tech Solutions LTDA")
    private String name;

    @Schema(description = "Email da empresa", example = "contato@techsolutions.com")
    @Email(message = "Email deve ser válido")
    private String email;

    @Schema(description = "Senha de acesso (caso deseje alterar)", example = "novaSenha123")
    private String password;

    @Schema(description = "CNPJ da empresa", example = "12345678000190")
    @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos")
    private String cnpj;

    @Schema(description = "Endereço completo da empresa", example = "Av. Afonso Pena, 1000, Centro, Belo Horizonte - MG")
    private String address;
}
