package com.labGCL03.moeda_estudantil.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;
    private final org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos - Login e cadastro
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/students").permitAll() // Alunos podem se cadastrar
                        .requestMatchers(HttpMethod.POST, "/api/companies").permitAll() // Empresas podem se cadastrar
                        .requestMatchers(HttpMethod.GET, "/api/institutions/**").permitAll() // Instituições públicas para tela de registro
                        
                        // Upload de arquivos e servir imagens
                        .requestMatchers("/uploads/**").permitAll() // Servir imagens públicas
                        .requestMatchers(HttpMethod.POST, "/api/upload/**").hasAnyRole("COMPANY", "ADMIN") // Upload apenas para empresas
                        .requestMatchers(HttpMethod.DELETE, "/api/upload/**").hasAnyRole("COMPANY", "ADMIN")
                        
                        // Swagger/OpenAPI - Permitir acesso completo à documentação
                        .requestMatchers(
                            "/swagger-ui/**", 
                            "/v3/api-docs/**", 
                            "/swagger-ui.html",
                            "/api-docs/**",
                            "/swagger-resources/**",
                            "/webjars/**"
                        ).permitAll()
                        
                        // Alunos - STUDENT tem acesso completo aos endpoints de aluno
                        .requestMatchers(HttpMethod.GET, "/api/students/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/students/**").hasAnyRole("STUDENT", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/students/**").hasAnyRole("STUDENT", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/students/**").hasAnyRole("STUDENT", "ADMIN") // STUDENT pode deletar próprio perfil
                        
                        // Professores - Regras específicas ANTES das genéricas
                        .requestMatchers(HttpMethod.POST, "/api/teachers/*/transfer").hasAnyRole("TEACHER", "ADMIN") // Professor pode transferir
                        .requestMatchers(HttpMethod.GET, "/api/teachers/**").hasAnyRole("TEACHER", "STUDENT", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/teachers/**").hasRole("ADMIN") // Outras operações POST apenas ADMIN
                        .requestMatchers(HttpMethod.PUT, "/api/teachers/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/teachers/**").hasRole("ADMIN")
                        
                        // Empresas - STUDENT pode apenas consultar (GET), COMPANY e ADMIN podem gerenciar
                        .requestMatchers(HttpMethod.GET, "/api/companies/**").hasAnyRole("STUDENT", "COMPANY", "TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/companies/**").hasAnyRole("COMPANY", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/companies/**").hasAnyRole("COMPANY", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/companies/**").hasAnyRole("COMPANY", "ADMIN") // COMPANY pode deletar próprio perfil
                        
                        // Vantagens - STUDENT pode apenas consultar (GET), COMPANY e ADMIN podem gerenciar
                        .requestMatchers(HttpMethod.GET, "/api/advantages/**").hasAnyRole("STUDENT", "COMPANY", "TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/advantages").hasAnyRole("COMPANY", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/advantages/**").hasAnyRole("COMPANY", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/advantages/**").hasAnyRole("COMPANY", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/advantages/**").hasAnyRole("COMPANY", "ADMIN")
                        
                        // Transações - Professores enviam, alunos resgatam
                        .requestMatchers(HttpMethod.POST, "/api/transactions/send").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.POST, "/api/transactions/redeem").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/transactions/**").authenticated()
                        
                        // Qualquer outra requisição precisa estar autenticada
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
