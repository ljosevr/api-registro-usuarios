package com.example.registrarusuario.infrastructure.persistence.mapper;

import com.example.registrarusuario.domain.model.Phone;
import com.example.registrarusuario.domain.model.User;
import com.example.registrarusuario.infrastructure.persistence.entity.PhoneEntity;
import com.example.registrarusuario.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserEntityMapper {

    public UserEntity toEntity(User user) {
        UserEntity userEntity = UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .created(user.getCreated())
                .modified(user.getModified())
                .lastLogin(user.getLastLogin())
                .token(user.getToken())
                .isactive(user.getIsactive())
                .build();

        if (user.getPhones() != null) {
            List<PhoneEntity> phoneEntities = user.getPhones().stream()
                    .map(phone -> toPhoneEntity(phone, userEntity))
                    .collect(Collectors.toList());
            phoneEntities.forEach(userEntity::addPhone);
        }

        return userEntity;
    }

    public User toDomain(UserEntity entity) {
        List<Phone> phones = entity.getPhones() != null
                ? entity.getPhones().stream()
                    .map(this::toPhoneDomain)
                    .collect(Collectors.toList())
                : List.of();

        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .phones(phones)
                .created(entity.getCreated())
                .modified(entity.getModified())
                .lastLogin(entity.getLastLogin())
                .token(entity.getToken())
                .isactive(entity.getIsactive())
                .build();
    }

    private PhoneEntity toPhoneEntity(Phone phone, UserEntity userEntity) {
        return PhoneEntity.builder()
                .id(phone.getId())
                .number(phone.getNumber())
                .citycode(phone.getCitycode())
                .contrycode(phone.getContrycode())
                .user(userEntity)
                .build();
    }

    private Phone toPhoneDomain(PhoneEntity entity) {
        return Phone.builder()
                .id(entity.getId())
                .number(entity.getNumber())
                .citycode(entity.getCitycode())
                .contrycode(entity.getContrycode())
                .build();
    }
}

