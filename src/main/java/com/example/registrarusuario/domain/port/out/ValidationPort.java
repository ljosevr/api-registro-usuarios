package com.example.registrarusuario.domain.port.out;

public interface ValidationPort {
    boolean isValidEmail(String email);
    boolean isValidPassword(String password);
}

