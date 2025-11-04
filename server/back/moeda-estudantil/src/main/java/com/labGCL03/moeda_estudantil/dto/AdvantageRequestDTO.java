package com.labGCL03.moeda_estudantil.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação de uma nova vantagem")
public class AdvantageRequestDTO {

    @Schema(description = "Nome da vantagem", example = "Desconto de 20% em cursos online")
    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @Schema(description = "Descrição detalhada da vantagem", example = "Desconto válido para todos os cursos da plataforma X")
    private String description;

    @Schema(description = "Custo em moedas estudantis", example = "100")
    @NotNull(message = "Custo é obrigatório")
    @Min(value = 1, message = "Custo deve ser no mínimo 1 moeda")
    private Integer costInCoins;

    @Schema(description = "URL da foto da vantagem", example = "https://example.com/photo.jpg")
    private String photo;

    @Schema(description = "ID da empresa que oferece a vantagem", example = "1")
    @NotNull(message = "ID da empresa é obrigatório")
    private Long companyId;
}
