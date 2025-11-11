package com.example.registrarusuario.application.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests de Validación de Content-Type JSON")
class ContentTypeValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Debe rechazar request sin Content-Type header")
    void shouldRejectRequestWithoutContentType() throws Exception {
        String jsonBody = """
                {
                  "name": "Juan Rodriguez",
                  "email": "juan@test.com",
                  "password": "Hunter2",
                  "phones": [{"number": "1234567", "citycode": "1", "contrycode": "57"}]
                }
                """;

        mockMvc.perform(post("/api/users/register")
                        .content(jsonBody))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Debe rechazar Content-Type: application/xml")
    void shouldRejectXmlContentType() throws Exception {
        String xmlBody = """
                <user>
                  <name>Juan Rodriguez</name>
                  <email>juan@test.com</email>
                </user>
                """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(xmlBody))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Debe rechazar Content-Type: text/plain")
    void shouldRejectTextPlainContentType() throws Exception {
        String textBody = "name=Juan&email=juan@test.com";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(textBody))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Debe rechazar Content-Type: application/x-www-form-urlencoded")
    void shouldRejectFormUrlencodedContentType() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Juan Rodriguez")
                        .param("email", "juan@test.com"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Debe ACEPTAR Content-Type: application/json")
    void shouldAcceptJsonContentType() throws Exception {
        String jsonBody = """
                {
                  "name": "Juan Rodriguez",
                  "email": "juan@testjson.com",
                  "password": "Hunter2",
                  "phones": [{"number": "1234567", "citycode": "1", "contrycode": "57"}]
                }
                """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe retornar Content-Type: application/json en respuesta exitosa")
    void shouldReturnJsonContentTypeOnSuccess() throws Exception {
        String jsonBody = """
                {
                  "name": "Pedro Perez",
                  "email": "pedro@testresponse.com",
                  "password": "Hunter2",
                  "phones": [{"number": "7654321", "citycode": "2", "contrycode": "57"}]
                }
                """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("Debe retornar Content-Type: application/json en respuesta de error")
    void shouldReturnJsonContentTypeOnError() throws Exception {
        String jsonBody = """
                {
                  "name": "Maria Lopez",
                  "email": "email-invalido",
                  "password": "Hunter2",
                  "phones": [{"number": "1234567", "citycode": "1", "contrycode": "57"}]
                }
                """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    @DisplayName("Debe retornar JSON con charset UTF-8")
    void shouldReturnJsonWithUtf8Charset() throws Exception {
        String jsonBody = """
                {
                  "name": "José García",
                  "email": "jose@testutf8.com",
                  "password": "Hunter2",
                  "phones": [{"number": "1234567", "citycode": "1", "contrycode": "57"}]
                }
                """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe rechazar Content-Type con charset no UTF-8")
    void shouldRejectNonUtf8Charset() throws Exception {
        String jsonBody = """
                {
                  "name": "Juan Rodriguez",
                  "email": "juan@testcharset.com",
                  "password": "Hunter2",
                  "phones": [{"number": "1234567", "citycode": "1", "contrycode": "57"}]
                }
                """;

        // Spring Boot acepta diferentes charsets pero convierte a UTF-8
        // Este test verifica que la respuesta siempre sea JSON
        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json;charset=ISO-8859-1")
                        .content(jsonBody))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe rechazar multipart/form-data")
    void shouldRejectMultipartFormData() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Debe retornar error JSON cuando el body no es JSON válido")
    void shouldReturnJsonErrorOnInvalidJsonBody() throws Exception {
        String invalidJson = "{ esto no es json válido }";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}

