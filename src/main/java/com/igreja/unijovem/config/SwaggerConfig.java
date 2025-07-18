package com.igreja.unijovem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Igreja UniJovem API")
                        .version("1.0.0")
                        .description("API para gerenciamento do sistema Igreja UniJovem - Autenticação e UsuarioLogin")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("dev@unijovem.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("Servidor de Desenvolvimento")
                ));
    }
}