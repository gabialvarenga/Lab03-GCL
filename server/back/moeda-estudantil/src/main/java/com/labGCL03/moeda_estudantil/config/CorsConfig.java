package com.labGCL03.moeda_estudantil.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir origens específicas (frontend em desenvolvimento)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",
            "http://127.0.0.1:5173"
        ));
        
        // Permitir todos os métodos HTTP
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        // Permitir todos os headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Permitir credenciais (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Expor headers de resposta
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        
        // Tempo de cache da configuração CORS (em segundos)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
