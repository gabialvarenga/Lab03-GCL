package com.labGCL03.moeda_estudantil.dto;

import com.labGCL03.moeda_estudantil.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de resposta após login bem-sucedido")
public class LoginResponseDTO {

    @Schema(description = "Token JWT para autenticação", example = "eyJhbGciOiJIUzI1NiIs...")
    private String token;

    @Schema(description = "Tipo do token", example = "Bearer")
    private String type = "Bearer";

    @Schema(description = "ID do usuário", example = "1")
    private Long userId;

    @Schema(description = "Nome do usuário", example = "João Silva")
    private String name;

    @Schema(description = "Email do usuário", example = "joao@exemplo.com")
    private String email;

    @Schema(description = "Tipo de usuário", example = "STUDENT")
    private Role role;

    public LoginResponseDTO(String token, Long userId, String name, String email, Role role) {
        this.token = token;
        this.type = "Bearer";
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
