package com.example.registrarusuario.infrastructure.persistence.repository;

import com.example.registrarusuario.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, String> {
    boolean existsByEmail(String email);
}

