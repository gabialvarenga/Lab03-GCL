package com.labGCL03.moeda_estudantil.services;

import com.labGCL03.moeda_estudantil.dto.LoginRequestDTO;
import com.labGCL03.moeda_estudantil.dto.LoginResponseDTO;
import com.labGCL03.moeda_estudantil.entities.User;
import com.labGCL03.moeda_estudantil.exception.BusinessException;
import com.labGCL03.moeda_estudantil.repositories.UserRepository;
import com.labGCL03.moeda_estudantil.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public LoginResponseDTO login(LoginRequestDTO request) {
        log.info("Tentativa de login para o email: {}", request.getEmail());

        try {
            // Autentica o usuário
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Busca o usuário no banco
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

            // Gera o token
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            String token = jwtService.generateToken(userDetails);

            log.info("Login realizado com sucesso para: {} ({})", user.getName(), user.getRole());

            return new LoginResponseDTO(
                    token,
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole()
            );

        } catch (Exception e) {
            log.error("Erro ao fazer login: {}", e.getMessage());
            throw new BusinessException("Email ou senha inválidos");
        }
    }

    /**
     * Renova o token JWT do usuário autenticado
     */
    public String refreshToken(String authHeader) {
        log.info("Tentativa de renovação de token");

        try {
            // Extrai o token do header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new BusinessException("Token inválido");
            }

            String token = authHeader.substring(7);
            String userEmail = jwtService.extractUsername(token);

            // Valida se o usuário existe
            userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

            // Gera novo token
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            String newToken = jwtService.generateToken(userDetails);

            log.info("Token renovado com sucesso para: {}", userEmail);
            return newToken;

        } catch (Exception e) {
            log.error("Erro ao renovar token: {}", e.getMessage());
            throw new BusinessException("Não foi possível renovar o token");
        }
    }
}
