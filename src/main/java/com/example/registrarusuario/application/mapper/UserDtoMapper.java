package com.example.registrarusuario.application.mapper;

import com.example.registrarusuario.application.dto.PhoneRequest;
import com.example.registrarusuario.application.dto.PhoneResponse;
import com.example.registrarusuario.application.dto.UserRegistrationRequest;
import com.example.registrarusuario.application.dto.UserRegistrationResponse;
import com.example.registrarusuario.domain.model.Phone;
import com.example.registrarusuario.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDtoMapper {

    public User toDomain(UserRegistrationRequest request) {
        List<Phone> phones = request.phones().stream()
                .map(this::toPhoneDomain)
                .collect(Collectors.toList());

        return User.builder()
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .phones(phones)
                .build();
    }

    public UserRegistrationResponse toResponse(User user) {
        List<PhoneResponse> phoneResponses = user.getPhones().stream()
                .map(this::toPhoneResponse)
                .collect(Collectors.toList());

        return new UserRegistrationResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                phoneResponses,
                user.getCreated(),
                user.getModified(),
                user.getLastLogin(),
                user.getToken(),
                user.getIsactive()
        );
    }

    private Phone toPhoneDomain(PhoneRequest phoneRequest) {
        return Phone.builder()
                .number(phoneRequest.number())
                .citycode(phoneRequest.citycode())
                .contrycode(phoneRequest.contrycode())
                .build();
    }

    private PhoneResponse toPhoneResponse(Phone phone) {
        return new PhoneResponse(
                phone.getNumber(),
                phone.getCitycode(),
                phone.getContrycode()
        );
    }
}

