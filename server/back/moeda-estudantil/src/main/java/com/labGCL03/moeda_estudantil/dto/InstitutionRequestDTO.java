package com.labGCL03.moeda_estudantil.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação ou atualização de uma instituição de ensino")
public class InstitutionRequestDTO {

    @Schema(description = "Nome da instituição", example = "Universidade Federal de Minas Gerais", required = true)
    @NotBlank(message = "Nome da instituição é obrigatório")
    private String name;
}
