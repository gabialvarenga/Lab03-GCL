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

}
