package com.example.registrarusuario.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record UserRegistrationResponse(
        @JsonProperty("id")
        String id,

        @JsonProperty("name")
        String name,

        @JsonProperty("email")
        String email,

        @JsonProperty("phones")
        List<PhoneResponse> phones,

        @JsonProperty("created")
        LocalDateTime created,

        @JsonProperty("modified")
        LocalDateTime modified,

        @JsonProperty("last_login")
        LocalDateTime lastLogin,

        @JsonProperty("token")
        String token,

        @JsonProperty("isactive")
        Boolean isactive
) {}

