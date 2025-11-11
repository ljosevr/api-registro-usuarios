package com.example.registrarusuario.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Servidor Local");

        Contact contact = new Contact();
        contact.setName("Equipo de Desarrollo");
        contact.setEmail("contacto@example.com");

        License license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0");

        Info info = new Info()
                .title("API de Registro de Usuarios")
                .version("1.0.0")
                .description("API REST para registro de usuarios implementada con Arquitectura Hexagonal. " +
                        "Esta API permite registrar usuarios con validaciones de email y contraseña, " +
                        "generación de tokens JWT y persistencia en base de datos H2.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}

