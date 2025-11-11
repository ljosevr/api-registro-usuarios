package com.example.registrarusuario.infrastructure.config;

import com.example.registrarusuario.domain.port.in.RegisterUserUseCase;
import com.example.registrarusuario.domain.port.out.TokenGeneratorPort;
import com.example.registrarusuario.domain.port.out.UserRepositoryPort;
import com.example.registrarusuario.domain.port.out.ValidationPort;
import com.example.registrarusuario.domain.service.UserRegistrationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepositoryPort userRepositoryPort,
            ValidationPort validationPort,
            TokenGeneratorPort tokenGeneratorPort) {
        return new UserRegistrationService(userRepositoryPort, validationPort, tokenGeneratorPort);
    }
}

