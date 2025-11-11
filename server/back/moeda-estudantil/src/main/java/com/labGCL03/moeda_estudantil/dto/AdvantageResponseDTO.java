package com.labGCL03.moeda_estudantil.dto;

import com.labGCL03.moeda_estudantil.entities.Advantage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de resposta de uma vantagem")
public class AdvantageResponseDTO {

    @Schema(description = "ID da vantagem", example = "1")
    private Long id;

    @Schema(description = "Nome da vantagem", example = "Desconto de 20% em cursos online")
    private String name;

    @Schema(description = "Descrição da vantagem", example = "Desconto válido para todos os cursos da plataforma X")
    private String description;

    @Schema(description = "Custo em moedas estudantis", example = "100")
    private Integer costInCoins;

    @Schema(description = "Quantidade disponível de cupons (visível apenas para empresas)", example = "50")
    private Integer availableQuantity;

    @Schema(description = "Imagem da vantagem em Base64", example = "data:image/jpeg;base64,/9j/4AAQSkZJRg...")
    private String photo;

    @Schema(description = "Nome do arquivo da foto", example = "desconto.jpg")
    private String photoName;

    @Schema(description = "Tipo MIME da foto", example = "image/jpeg")
    private String photoType;

    @Schema(description = "ID da empresa que oferece a vantagem", example = "1")
    private Long companyId;

    @Schema(description = "Nome da empresa", example = "Tech Solutions LTDA")
    private String companyName;

    @Schema(description = "Quantidade de vezes que foi resgatada", example = "15")
    private Integer timesRedeemed;

    @Schema(description = "Data de criação", example = "2025-11-03T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização", example = "2025-11-03T15:30:00")
    private LocalDateTime updatedAt;

    public AdvantageResponseDTO(Advantage advantage) {
        this(advantage, false, false);
    }
    
    public AdvantageResponseDTO(Advantage advantage, boolean includeTimesRedeemed) {
        this(advantage, includeTimesRedeemed, false);
    }

    /**
     * @param advantage A vantagem
     * @param includeTimesRedeemed Se deve incluir o número de vezes resgatada
     * @param showAvailableQuantity Se deve mostrar a quantidade disponível (apenas para empresas)
     */
    public AdvantageResponseDTO(Advantage advantage, boolean includeTimesRedeemed, boolean showAvailableQuantity) {
        this.id = advantage.getId();
        this.name = advantage.getName();
        this.description = advantage.getDescription();
        this.costInCoins = advantage.getCostInCoins();
        this.availableQuantity = showAvailableQuantity ? advantage.getAvailableQuantity() : null;
        this.photo = advantage.getPhoto();
        this.photoName = advantage.getPhotoName();
        this.photoType = advantage.getPhotoType();
        this.companyId = advantage.getCompany() != null ? advantage.getCompany().getId() : null;
        this.companyName = advantage.getCompany() != null ? advantage.getCompany().getName() : null;
        // Evitar acesso a coleções lazy durante compra - definir como null ou 0 por padrão
        this.timesRedeemed = includeTimesRedeemed ? advantage.getTimesRedeemed() : 0;
        this.createdAt = advantage.getCreatedAt();
        this.updatedAt = advantage.getUpdatedAt();
    }
}
