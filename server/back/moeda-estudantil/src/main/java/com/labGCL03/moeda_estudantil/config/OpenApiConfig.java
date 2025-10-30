package com.labGCL03.moeda_estudantil.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Sistema de Moeda Estudantil")
                        .version("1.0.0")
                        .description("API REST para gerenciamento do sistema de moeda estudantil. " +
                                "Permite o gerenciamento completo de alunos, professores, transações, " +
                                "vantagens e cupons no sistema de mérito acadêmico.")
                        .contact(new Contact()
                                .name("Equipe Lab03-GCL")
                                .email("contato@moedaestudantil.com")
                                .url("https://github.com/gabialvarenga/Lab03-GCL"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor Local")
                ));
    }
}
