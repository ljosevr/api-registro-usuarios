package com.example.registrarusuario.domain.service;

import com.example.registrarusuario.domain.exception.EmailAlreadyExistsException;
import com.example.registrarusuario.domain.exception.InvalidFormatException;
import com.example.registrarusuario.domain.model.User;
import com.example.registrarusuario.domain.port.in.RegisterUserUseCase;
import com.example.registrarusuario.domain.port.out.TokenGeneratorPort;
import com.example.registrarusuario.domain.port.out.UserRepositoryPort;
import com.example.registrarusuario.domain.port.out.ValidationPort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class UserRegistrationService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final ValidationPort validationPort;
    private final TokenGeneratorPort tokenGeneratorPort;

    @Override
    public User registerUser(User user) {
        // Validar formato de email
        if (!validationPort.isValidEmail(user.getEmail())) {
            throw new InvalidFormatException("El formato del correo es inválido");
        }

        // Validar formato de contraseña
        if (!validationPort.isValidPassword(user.getPassword())) {
            throw new InvalidFormatException("El formato de la contraseña es inválido");
        }

        // Verificar si el email ya existe
        if (userRepositoryPort.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("El correo ya registrado");
        }

        // Generar token
        String token = tokenGeneratorPort.generateToken(user.getEmail());

        // Crear usuario con valores iniciales
        LocalDateTime now = LocalDateTime.now();
        User newUser = User.builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .phones(user.getPhones())
                .created(now)
                .modified(now)
                .lastLogin(now)
                .token(token)
                .isactive(true)
                .build();

        return userRepositoryPort.save(newUser);
    }
}

