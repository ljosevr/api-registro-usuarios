package com.example.registrarusuario.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ErrorResponse(
        @JsonProperty("mensaje")
        String mensaje
) {}

