package com.labGCL03.moeda_estudantil.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para atualização de uma vantagem")
public class AdvantageUpdateDTO {

    @Schema(description = "Nome da vantagem", example = "Desconto de 30% em cursos online")
    private String name;

    @Schema(description = "Descrição detalhada da vantagem", example = "Desconto válido para todos os cursos da plataforma X")
    private String description;

    @Schema(description = "Custo em moedas estudantis", example = "150")
    @Min(value = 1, message = "Custo deve ser no mínimo 1 moeda")
    private Integer costInCoins;

    @Schema(description = "URL da foto da vantagem", example = "https://example.com/photo.jpg")
    private String photo;
}
