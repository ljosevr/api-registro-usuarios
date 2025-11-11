package com.example.registrarusuario.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UserRegistrationRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @JsonProperty("name")
        String name,

        @NotBlank(message = "El correo es obligatorio")
        @JsonProperty("email")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @JsonProperty("password")
        String password,

        @NotEmpty(message = "Debe incluir al menos un teléfono")
        @Valid
        @JsonProperty("phones")
        List<PhoneRequest> phones
) {}

