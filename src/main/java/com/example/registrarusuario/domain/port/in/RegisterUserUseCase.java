package com.example.registrarusuario.domain.port.in;

import com.example.registrarusuario.domain.model.User;

public interface RegisterUserUseCase {
    User registerUser(User user);
}

