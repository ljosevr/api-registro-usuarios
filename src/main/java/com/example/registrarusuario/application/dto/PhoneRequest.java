package com.example.registrarusuario.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record PhoneRequest(
        @NotBlank(message = "El número de teléfono es obligatorio")
        @JsonProperty("number")
        String number,

        @NotBlank(message = "El código de ciudad es obligatorio")
        @JsonProperty("citycode")
        String citycode,

        @NotBlank(message = "El código de país es obligatorio")
        @JsonProperty("contrycode")
        String contrycode
) {}

