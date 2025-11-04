package com.labGCL03.moeda_estudantil.dto;

import com.labGCL03.moeda_estudantil.entities.Student;
import com.labGCL03.moeda_estudantil.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de resposta de um aluno")
public class StudentResponseDTO {

    @Schema(description = "ID único do aluno", example = "1")
    private Long id;
    
    @Schema(description = "Nome completo do aluno", example = "João da Silva")
    private String name;
    
    @Schema(description = "Email do aluno", example = "joao.silva@exemplo.com")
    private String email;
    
    @Schema(description = "CPF do aluno", example = "12345678900")
    private String cpf;
    
    @Schema(description = "RG do aluno", example = "MG1234567")
    private String rg;
    
    @Schema(description = "Endereço completo do aluno", example = "Rua das Flores, 123, Centro")
    private String address;
    
    @Schema(description = "Curso em que o aluno está matriculado", example = "Engenharia de Software")
    private String course;
    
    @Schema(description = "Saldo atual de moedas do aluno", example = "100")
    private Integer coinBalance;
    
    @Schema(description = "ID da instituição de ensino", example = "1")
    private Long institutionId;
    
    @Schema(description = "Nome da instituição de ensino", example = "UFMG")
    private String institutionName;
    
    @Schema(description = "Papel do usuário no sistema", example = "STUDENT")
    private Role role;

    public StudentResponseDTO(Student student) {
        this.id = student.getId();
        this.name = student.getName();
        this.email = student.getEmail();
        this.cpf = student.getCpf();
        this.rg = student.getRg();
        this.address = student.getAddress();
        this.course = student.getCourse();
        this.coinBalance = student.getCoinBalance();
        this.role = student.getRole();
        
        if (student.getInstitution() != null) {
            this.institutionId = student.getInstitution().getId();
            this.institutionName = student.getInstitution().getName();
        }
    }
}
