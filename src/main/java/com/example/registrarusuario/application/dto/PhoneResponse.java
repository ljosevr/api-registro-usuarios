package com.example.registrarusuario.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PhoneResponse(
        @JsonProperty("number")
        String number,

        @JsonProperty("citycode")
        String citycode,

        @JsonProperty("contrycode")
        String contrycode
) {}

