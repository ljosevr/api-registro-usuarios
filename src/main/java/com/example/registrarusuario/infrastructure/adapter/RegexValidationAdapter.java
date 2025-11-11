package com.example.registrarusuario.infrastructure.adapter;

import com.example.registrarusuario.domain.port.out.ValidationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class RegexValidationAdapter implements ValidationPort {

    @Value("${app.validation.email.regex}")
    private String emailRegex;

    @Value("${app.validation.password.regex}")
    private String passwordRegex;

    @Override
    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    @Override
    public boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile(passwordRegex);
        return pattern.matcher(password).matches();
    }
}

