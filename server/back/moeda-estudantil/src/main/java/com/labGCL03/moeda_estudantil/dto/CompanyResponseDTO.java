package com.labGCL03.moeda_estudantil.dto;

import com.labGCL03.moeda_estudantil.entities.Company;
import com.labGCL03.moeda_estudantil.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de resposta de uma empresa parceira")
public class CompanyResponseDTO {

    @Schema(description = "ID da empresa", example = "1")
    private Long id;

    @Schema(description = "Nome da empresa", example = "Tech Solutions LTDA")
    private String name;

    @Schema(description = "Email da empresa", example = "contato@techsolutions.com")
    private String email;

    @Schema(description = "CNPJ da empresa", example = "12345678000190")
    private String cnpj;

    @Schema(description = "Endereço da empresa", example = "Av. Afonso Pena, 1000, Centro, Belo Horizonte - MG")
    private String address;

    @Schema(description = "Tipo de usuário", example = "COMPANY")
    private Role role;

    @Schema(description = "Quantidade de vantagens oferecidas pela empresa", example = "5")
    private Integer advantagesCount;

    public CompanyResponseDTO(Company company) {
        this.id = company.getId();
        this.name = company.getName();
        this.email = company.getEmail();
        this.cnpj = company.getCnpj();
        this.address = company.getAddress();
        this.role = company.getRole();
        this.advantagesCount = company.getAdvantages() != null ? company.getAdvantages().size() : 0;
    }

    public CompanyResponseDTO(Company company, Integer advantagesCount) {
        this.id = company.getId();
        this.name = company.getName();
        this.email = company.getEmail();
        this.cnpj = company.getCnpj();
        this.address = company.getAddress();
        this.role = company.getRole();
        this.advantagesCount = advantagesCount;
    }
}
