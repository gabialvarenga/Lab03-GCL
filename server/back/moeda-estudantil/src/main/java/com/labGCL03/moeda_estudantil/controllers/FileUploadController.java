package com.labGCL03.moeda_estudantil.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
@Tag(name = "Upload", description = "Endpoints para upload de arquivos")
@Slf4j
public class FileUploadController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Operation(
        summary = "Upload de imagem",
        description = "Faz upload de uma imagem e retorna a URL pública do arquivo. Formatos aceitos: JPG, PNG, GIF. Tamanho máximo: 5MB."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Imagem enviada com sucesso",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Arquivo inválido ou formato não suportado"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erro ao salvar arquivo"
        )
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Validar se o arquivo está vazio
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Arquivo vazio"));
            }

            // Validar tipo de arquivo
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("message", "Apenas arquivos de imagem são aceitos"));
            }

            // Validar tamanho (5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of("message", "O arquivo deve ter no máximo 5MB"));
            }

            // Criar diretório se não existir
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Gerar nome único para o arquivo
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;

            // Salvar arquivo
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Retornar URL completa do arquivo (incluindo o host do backend)
            String fileUrl = "http://localhost:8080/uploads/" + filename;
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            response.put("filename", filename);

            log.info("Arquivo salvo com sucesso: {} - URL: {}", filename, fileUrl);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            log.error("Erro ao salvar arquivo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erro ao salvar arquivo: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Deletar imagem",
        description = "Remove uma imagem do servidor"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Imagem deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Imagem não encontrada"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/image/{filename}")
    public ResponseEntity<?> deleteImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Arquivo deletado: {}", filename);
                return ResponseEntity.ok(Map.of("message", "Imagem deletada com sucesso"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Imagem não encontrada"));
            }
        } catch (IOException e) {
            log.error("Erro ao deletar arquivo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erro ao deletar arquivo"));
        }
    }
}
