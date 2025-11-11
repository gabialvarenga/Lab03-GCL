package com.labGCL03.moeda_estudantil.dto;

import com.labGCL03.moeda_estudantil.entities.Teacher;
import com.labGCL03.moeda_estudantil.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de resposta de um professor")
public class TeacherResponseDTO {

    @Schema(description = "ID único do professor", example = "1")
    private Long id;
    
    @Schema(description = "Nome completo do professor", example = "João Silva")
    private String name;
    
    @Schema(description = "Email do professor", example = "joao.silva@exemplo.com")
    private String email;
    
    @Schema(description = "CPF do professor", example = "12345678900")
    private String cpf;
    
    @Schema(description = "Departamento do professor", example = "Departamento de Engenharia")
    private String department;
    
    @Schema(description = "Saldo atual de moedas do professor", example = "1000")
    private Integer balance;
    
    @Schema(description = "ID da instituição de ensino", example = "1")
    private Long institutionId;
    
    @Schema(description = "Nome da instituição de ensino", example = "UFMG")
    private String institutionName;
    
    @Schema(description = "Papel do usuário no sistema", example = "TEACHER")
    private Role role;

    public TeacherResponseDTO(Teacher teacher) {
        this.id = teacher.getId();
        this.name = teacher.getName();
        this.email = teacher.getEmail();
        this.cpf = teacher.getCpf();
        this.department = teacher.getDepartment();
        this.balance = teacher.getCurrentBalance();
        this.role = teacher.getRole();
        
        if (teacher.getInstitution() != null) {
            this.institutionId = teacher.getInstitution().getId();
            this.institutionName = teacher.getInstitution().getName();
        }
    }
}
