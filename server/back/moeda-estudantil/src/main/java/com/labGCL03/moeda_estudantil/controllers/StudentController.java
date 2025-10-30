package com.labGCL03.moeda_estudantil.controllers;

import com.labGCL03.moeda_estudantil.dto.StudentRequestDTO;
import com.labGCL03.moeda_estudantil.dto.StudentResponseDTO;
import com.labGCL03.moeda_estudantil.dto.StudentUpdateDTO;
import com.labGCL03.moeda_estudantil.entities.Student;
import com.labGCL03.moeda_estudantil.exception.ErrorResponse;
import com.labGCL03.moeda_estudantil.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Alunos", description = "API para gerenciamento de alunos no sistema de moeda estudantil")
public class StudentController {

    private final StudentService studentService;

    @Operation(
            summary = "Listar todos os alunos",
            description = "Retorna uma lista com todos os alunos cadastrados no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de alunos retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        List<Student> students = studentService.findAll();
        List<StudentResponseDTO> response = students.stream()
            .map(StudentResponseDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Buscar aluno por ID",
            description = "Retorna os dados de um aluno específico através do seu ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudentById(
            @Parameter(description = "ID do aluno", required = true) @PathVariable Long id) {
        Student student = studentService.findById(id);
        StudentResponseDTO response = new StudentResponseDTO(student);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Buscar aluno por email",
            description = "Retorna os dados de um aluno através do seu email"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<StudentResponseDTO> getStudentByEmail(
            @Parameter(description = "Email do aluno", required = true, example = "joao@exemplo.com") 
            @PathVariable String email) {
        Student student = studentService.findByEmail(email);
        StudentResponseDTO response = new StudentResponseDTO(student);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Buscar aluno por CPF",
            description = "Retorna os dados de um aluno através do seu CPF"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<StudentResponseDTO> getStudentByCpf(
            @Parameter(description = "CPF do aluno", required = true, example = "12345678900") 
            @PathVariable String cpf) {
        Student student = studentService.findByCpf(cpf);
        StudentResponseDTO response = new StudentResponseDTO(student);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Buscar alunos por curso",
            description = "Retorna uma lista de alunos matriculados em um curso específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de alunos retornada com sucesso")
    })
    @GetMapping("/course/{course}")
    public ResponseEntity<List<StudentResponseDTO>> getStudentsByCourse(
            @Parameter(description = "Nome do curso", required = true, example = "Engenharia de Software") 
            @PathVariable String course) {
        List<Student> students = studentService.findByCourse(course);
        List<StudentResponseDTO> response = students.stream()
            .map(StudentResponseDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Buscar alunos por instituição",
            description = "Retorna uma lista de alunos matriculados em uma instituição específica"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de alunos retornada com sucesso")
    })
    @GetMapping("/institution/{institutionId}")
    public ResponseEntity<List<StudentResponseDTO>> getStudentsByInstitution(
            @Parameter(description = "ID da instituição", required = true) 
            @PathVariable Long institutionId) {
        List<Student> students = studentService.findByInstitution(institutionId);
        List<StudentResponseDTO> response = students.stream()
            .map(StudentResponseDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Buscar saldo de um aluno",
            description = "Retorna o saldo atual de moedas de um aluno"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/balance")
    public ResponseEntity<Integer> getStudentBalance(
            @Parameter(description = "ID do aluno", required = true) 
            @PathVariable Long id) {
        Integer balance = studentService.getStudentBalance(id);
        return ResponseEntity.ok(balance);
    }

    @Operation(
            summary = "Criar novo aluno",
            description = "Cria um novo aluno no sistema. O CPF e email devem ser únicos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Aluno criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF/email já cadastrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Instituição não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<StudentResponseDTO> createStudent(
            @Parameter(description = "Dados do aluno a ser criado", required = true)
            @Valid @RequestBody StudentRequestDTO dto) {
        Student student = studentService.create(dto);
        StudentResponseDTO response = new StudentResponseDTO(student);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Atualizar aluno completamente",
            description = "Atualiza todos os dados de um aluno existente (PUT)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @Parameter(description = "ID do aluno", required = true) @PathVariable Long id,
            @Parameter(description = "Dados atualizados do aluno", required = true)
            @Valid @RequestBody StudentUpdateDTO dto) {
        
        Student student = studentService.update(id, dto);
        StudentResponseDTO response = new StudentResponseDTO(student);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Atualizar aluno parcialmente",
            description = "Atualiza apenas os campos fornecidos de um aluno existente (PATCH)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> partialUpdateStudent(
            @Parameter(description = "ID do aluno", required = true) @PathVariable Long id,
            @Parameter(description = "Campos a serem atualizados", required = true)
            @RequestBody StudentUpdateDTO dto) {
        
        Student student = studentService.update(id, dto);
        StudentResponseDTO response = new StudentResponseDTO(student);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Deletar aluno",
            description = "Remove um aluno do sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Aluno deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @Parameter(description = "ID do aluno", required = true) 
            @PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Buscar alunos com saldo mínimo",
            description = "Retorna uma lista de alunos com saldo igual ou superior ao valor especificado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de alunos retornada com sucesso")
    })
    @GetMapping("/balance/minimum/{minBalance}")
    public ResponseEntity<List<StudentResponseDTO>> getStudentsWithMinimumBalance(
            @Parameter(description = "Saldo mínimo de moedas", required = true, example = "50") 
            @PathVariable Integer minBalance) {
        List<Student> students = studentService.getStudentsWithMinimumBalance(minBalance);
        List<StudentResponseDTO> response = students.stream()
            .map(StudentResponseDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obter total de moedas em uma instituição",
            description = "Retorna o total de moedas acumuladas por todos os alunos de uma instituição"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total de moedas retornado com sucesso")
    })
    @GetMapping("/institution/{institutionId}/total-coins")
    public ResponseEntity<Long> getTotalCoinsInInstitution(
            @Parameter(description = "ID da instituição", required = true) 
            @PathVariable Long institutionId) {
        Long totalCoins = studentService.getTotalCoinsInInstitution(institutionId);
        return ResponseEntity.ok(totalCoins != null ? totalCoins : 0L);
    }

    @Operation(
            summary = "Obter média de moedas em uma instituição",
            description = "Retorna a média de moedas por aluno em uma instituição"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Média de moedas retornada com sucesso")
    })
    @GetMapping("/institution/{institutionId}/average-coins")
    public ResponseEntity<Double> getAverageCoinsInInstitution(
            @Parameter(description = "ID da instituição", required = true) 
            @PathVariable Long institutionId) {
        Double averageCoins = studentService.getAverageCoinsInInstitution(institutionId);
        return ResponseEntity.ok(averageCoins != null ? averageCoins : 0.0);
    }
}
