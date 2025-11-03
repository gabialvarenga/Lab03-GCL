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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos - Login e cadastro
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/students").permitAll() // Alunos podem se cadastrar
                        .requestMatchers(HttpMethod.POST, "/api/companies").permitAll() // Empresas podem se cadastrar
                        
                        // Swagger/OpenAPI - Permitir acesso completo à documentação
                        .requestMatchers(
                            "/swagger-ui/**", 
                            "/v3/api-docs/**", 
                            "/swagger-ui.html",
                            "/api-docs/**",
                            "/swagger-resources/**",
                            "/webjars/**"
                        ).permitAll()
                        
                        // Instituições - Apenas leitura para todos autenticados
                        .requestMatchers(HttpMethod.GET, "/api/institutions/**").authenticated()
                        
                        // Alunos - Apenas o próprio aluno ou admin
                        .requestMatchers(HttpMethod.GET, "/api/students/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/students/**").hasAnyRole("STUDENT", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/students/**").hasAnyRole("STUDENT", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/students/**").hasRole("ADMIN")
                        
                        // Professores
                        .requestMatchers(HttpMethod.GET, "/api/teachers/**").hasAnyRole("TEACHER", "STUDENT", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/teachers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/teachers/**").hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/teachers/**").hasRole("ADMIN")
                        
                        // Empresas - Própria empresa ou admin
                        .requestMatchers(HttpMethod.GET, "/api/companies/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/companies/**").hasAnyRole("COMPANY", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/companies/**").hasAnyRole("COMPANY", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/companies/**").hasRole("ADMIN")
                        
                        // Vantagens - Visualização para todos autenticados, gestão para empresas
                        .requestMatchers(HttpMethod.GET, "/api/advantages/**").authenticated()
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
