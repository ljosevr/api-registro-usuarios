package com.example.registrarusuario.infrastructure.persistence.adapter;

import com.example.registrarusuario.domain.model.User;
import com.example.registrarusuario.domain.port.out.UserRepositoryPort;
import com.example.registrarusuario.infrastructure.persistence.entity.UserEntity;
import com.example.registrarusuario.infrastructure.persistence.mapper.UserEntityMapper;
import com.example.registrarusuario.infrastructure.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpaUserRepository;
    private final UserEntityMapper userEntityMapper;

    @Override
    public User save(User user) {
        UserEntity userEntity = userEntityMapper.toEntity(user);
        UserEntity savedEntity = jpaUserRepository.save(userEntity);
        return userEntityMapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }
}

