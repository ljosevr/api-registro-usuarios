package com.example.registrarusuario.domain.port.out;

public interface TokenGeneratorPort {
    String generateToken(String email);
}

