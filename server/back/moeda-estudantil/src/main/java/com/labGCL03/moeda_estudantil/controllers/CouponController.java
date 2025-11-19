package com.labGCL03.moeda_estudantil.controllers;

import com.labGCL03.moeda_estudantil.dto.CouponResponseDTO;
import com.labGCL03.moeda_estudantil.dto.CouponValidationResponseDTO;
import com.labGCL03.moeda_estudantil.entities.Coupon;
import com.labGCL03.moeda_estudantil.exception.ErrorResponse;
import com.labGCL03.moeda_estudantil.services.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Cupons", description = "API para gerenciamento e validação de cupons de resgate")
public class CouponController {

    private final CouponService couponService;

    @Operation(
            summary = "Validar cupom",
            description = "Valida um cupom através do código. Retorna as informações do cupom se for válido e ainda não foi utilizado. Usado pela empresa para conferir antes de entregar a vantagem."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupom válido"),
            @ApiResponse(responseCode = "400", description = "Cupom já foi utilizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Cupom não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/validate/{code}")
    public ResponseEntity<CouponValidationResponseDTO> validateCoupon(
            @Parameter(description = "Código do cupom", example = "A3B7-9C2F", required = true)
            @PathVariable String code) {
        
        Coupon coupon = couponService.validateCoupon(code);
        CouponValidationResponseDTO response = new CouponValidationResponseDTO(coupon);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Marcar cupom como utilizado",
            description = "Marca um cupom como utilizado após a empresa confirmar a entrega da vantagem. Esta ação é irreversível."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupom marcado como utilizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Cupom já foi utilizado anteriormente",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (apenas COMPANY)"),
            @ApiResponse(responseCode = "404", description = "Cupom não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/use/{code}")
    public ResponseEntity<Void> markCouponAsUsed(
            @Parameter(description = "Código do cupom", example = "A3B7-9C2F", required = true)
            @PathVariable String code) {
        
        couponService.markCouponAsUsed(code);
        
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Buscar cupons de um aluno",
            description = "Retorna todos os cupons gerados por um aluno específico, ordenados por data de geração (mais recentes primeiro)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cupons retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CouponResponseDTO>> getStudentCoupons(
            @Parameter(description = "ID do aluno", required = true)
            @PathVariable Long studentId) {
        
        List<Coupon> coupons = couponService.getStudentCoupons(studentId);
        List<CouponResponseDTO> response = coupons.stream()
            .map(CouponResponseDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Buscar cupons de uma empresa",
            description = "Retorna todos os cupons gerados para as vantagens de uma empresa específica. Usado pela empresa para ver todos os resgates."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cupons retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (apenas COMPANY ou ADMIN)"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<CouponResponseDTO>> getCompanyCoupons(
            @Parameter(description = "ID da empresa", required = true)
            @PathVariable Long companyId) {
        
        List<Coupon> coupons = couponService.getCompanyCoupons(companyId);
        List<CouponResponseDTO> response = coupons.stream()
            .map(CouponResponseDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
}
