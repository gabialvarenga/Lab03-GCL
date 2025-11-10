package com.labGCL03.moeda_estudantil.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
@Tag(name = "Upload", description = "Endpoints para upload de arquivos")
@Slf4j
public class FileUploadController {

    @Operation(
        summary = "Upload de imagem em Base64",
        description = "Converte uma imagem para Base64 e retorna a string codificada. Formatos aceitos: JPG, PNG, GIF. Tamanho máximo: 5MB."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Imagem convertida com sucesso",
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
            description = "Erro ao processar arquivo"
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

            // Converter para Base64
            byte[] imageBytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            // Criar data URL completo
            String dataUrl = "data:" + contentType + ";base64," + base64Image;

            // Retornar resposta
            Map<String, String> response = new HashMap<>();
            response.put("photo", dataUrl);
            response.put("photoName", file.getOriginalFilename());
            response.put("photoType", contentType);
            response.put("size", String.valueOf(file.getSize()));

            log.info("Imagem convertida para Base64 com sucesso: {} - Tamanho: {} bytes", 
                     file.getOriginalFilename(), file.getSize());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erro ao processar arquivo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erro ao processar arquivo: " + e.getMessage()));
        }
    }
}
