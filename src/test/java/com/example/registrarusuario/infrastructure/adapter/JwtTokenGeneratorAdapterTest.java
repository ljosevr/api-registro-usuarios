package com.example.registrarusuario.infrastructure.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtTokenGeneratorAdapter Tests")
class JwtTokenGeneratorAdapterTest {

    private JwtTokenGeneratorAdapter tokenGenerator;

    @BeforeEach
    void setUp() {
        tokenGenerator = new JwtTokenGeneratorAdapter();
        ReflectionTestUtils.setField(tokenGenerator, "secret",
                "mySecretKeyForJWTTokenGenerationThatShouldBeVeryLongAndSecure123456789");
        ReflectionTestUtils.setField(tokenGenerator, "expiration", 86400000L);
    }

    @Test
    @DisplayName("Debe generar token JWT no nulo")
    void shouldGenerateNonNullToken() {
        String token = tokenGenerator.generateToken("juan@rodriguez.org");
        assertThat(token).isNotNull();
    }

    @Test
    @DisplayName("Debe generar token JWT no vacío")
    void shouldGenerateNonEmptyToken() {
        String token = tokenGenerator.generateToken("juan@rodriguez.org");
        assertThat(token).isNotEmpty();
    }

    @Test
    @DisplayName("Debe generar token JWT con formato correcto (3 partes separadas por puntos)")
    void shouldGenerateTokenWithCorrectFormat() {
        String token = tokenGenerator.generateToken("juan@rodriguez.org");
        String[] parts = token.split("\\.");
        assertThat(parts).hasSize(3);
    }

    @Test
    @DisplayName("Debe generar tokens diferentes para emails diferentes")
    void shouldGenerateDifferentTokensForDifferentEmails() {
        String token1 = tokenGenerator.generateToken("juan@rodriguez.org");
        String token2 = tokenGenerator.generateToken("pedro@perez.com");

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Debe generar token que comienza con header JWT típico")
    void shouldGenerateTokenStartingWithJwtHeader() {
        String token = tokenGenerator.generateToken("test@example.com");
        assertThat(token).startsWith("eyJ");
    }
}


