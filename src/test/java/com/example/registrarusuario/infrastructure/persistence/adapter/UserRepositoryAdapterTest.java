package com.example.registrarusuario.infrastructure.persistence.adapter;

import com.example.registrarusuario.domain.model.Phone;
import com.example.registrarusuario.domain.model.User;
import com.example.registrarusuario.infrastructure.persistence.entity.UserEntity;
import com.example.registrarusuario.infrastructure.persistence.mapper.UserEntityMapper;
import com.example.registrarusuario.infrastructure.persistence.repository.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepositoryAdapter Tests")
class UserRepositoryAdapterTest {

    @Mock
    private JpaUserRepository jpaUserRepository;

    @Mock
    private UserEntityMapper userEntityMapper;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    private User domainUser;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        Phone phone = Phone.builder()
                .id("phone-1")
                .number("1234567")
                .citycode("1")
                .contrycode("57")
                .build();

        domainUser = User.builder()
                .id("user-123")
                .name("Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password("Hunter2")
                .phones(List.of(phone))
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .token("jwt-token")
                .isactive(true)
                .build();

        userEntity = new UserEntity();
        userEntity.setId("user-123");
        userEntity.setEmail("juan@rodriguez.org");
    }

    @Test
    @DisplayName("Debe guardar un usuario correctamente")
    void shouldSaveUserSuccessfully() {
        // Given
        when(userEntityMapper.toEntity(any(User.class))).thenReturn(userEntity);
        when(jpaUserRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userEntityMapper.toDomain(any(UserEntity.class))).thenReturn(domainUser);

        // When
        User savedUser = userRepositoryAdapter.save(domainUser);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isEqualTo("user-123");
        assertThat(savedUser.getEmail()).isEqualTo("juan@rodriguez.org");

        verify(userEntityMapper).toEntity(domainUser);
        verify(jpaUserRepository).save(userEntity);
        verify(userEntityMapper).toDomain(userEntity);
    }


    @Test
    @DisplayName("Debe retornar true cuando el email existe")
    void shouldReturnTrueWhenEmailExists() {
        // Given
        when(jpaUserRepository.existsByEmail(anyString())).thenReturn(true);

        // When
        boolean exists = userRepositoryAdapter.existsByEmail("juan@rodriguez.org");

        // Then
        assertThat(exists).isTrue();
        verify(jpaUserRepository).existsByEmail("juan@rodriguez.org");
    }

    @Test
    @DisplayName("Debe retornar false cuando el email no existe")
    void shouldReturnFalseWhenEmailDoesNotExist() {
        // Given
        when(jpaUserRepository.existsByEmail(anyString())).thenReturn(false);

        // When
        boolean exists = userRepositoryAdapter.existsByEmail("noexiste@test.com");

        // Then
        assertThat(exists).isFalse();
        verify(jpaUserRepository).existsByEmail("noexiste@test.com");
    }
}

