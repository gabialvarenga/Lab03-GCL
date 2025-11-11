package com.labGCL03.moeda_estudantil.dto;

import com.labGCL03.moeda_estudantil.entities.Institution;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de uma instituição de ensino")
public class InstitutionDTO {

    @Schema(description = "ID da instituição", example = "1")
    private Long id;

    @Schema(description = "Nome da instituição", example = "Universidade Federal de Minas Gerais")
    private String name;

    @Schema(description = "Lista de cursos disponíveis na instituição")
    private List<String> availableCourses;

    @Schema(description = "Data de cadastro da instituição")
    private LocalDateTime createdAt;

    public InstitutionDTO(Institution institution) {
        this.id = institution.getId();
        this.name = institution.getName();
        this.availableCourses = institution.getAvailableCourses();
        this.createdAt = institution.getCreatedAt();
    }
}
