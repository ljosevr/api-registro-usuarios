package com.example.registrarusuario.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Phone {
    private String id;
    private String number;
    private String citycode;
    private String contrycode;
}

