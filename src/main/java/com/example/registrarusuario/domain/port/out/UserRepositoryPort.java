package com.example.registrarusuario.domain.port.out;

import com.example.registrarusuario.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    boolean existsByEmail(String email);
}

