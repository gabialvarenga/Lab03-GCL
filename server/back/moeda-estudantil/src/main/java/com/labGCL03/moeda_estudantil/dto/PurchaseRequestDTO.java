package com.labGCL03.moeda_estudantil.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestDTO {
    
    @NotNull(message = "ID do aluno é obrigatório")
    private Long studentId;
    
    @NotNull(message = "ID da vantagem é obrigatório")
    private Long advantageId;
}
