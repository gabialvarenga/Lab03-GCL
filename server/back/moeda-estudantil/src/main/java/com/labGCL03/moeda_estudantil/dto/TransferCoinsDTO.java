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
@Schema(description = "Dados para transferência de moedas de professor para aluno")
public class TransferCoinsDTO {

    @Schema(description = "ID do aluno que receberá as moedas", example = "1", required = true)
    @NotNull(message = "ID do aluno é obrigatório")
    private Long studentId;

    @Schema(description = "Quantidade de moedas a transferir", example = "50", required = true)
    @NotNull(message = "Quantidade de moedas é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    private Integer amount;

    @Schema(description = "Motivo da transferência", example = "Participação ativa nas aulas", required = true)
    @NotBlank(message = "Motivo da transferência é obrigatório")
    private String reason;
}
