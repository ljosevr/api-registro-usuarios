package com.example.registrarusuario.domain.service;

import com.example.registrarusuario.domain.exception.EmailAlreadyExistsException;
import com.example.registrarusuario.domain.exception.InvalidFormatException;
import com.example.registrarusuario.domain.model.Phone;
import com.example.registrarusuario.domain.model.User;
import com.example.registrarusuario.domain.port.out.TokenGeneratorPort;
import com.example.registrarusuario.domain.port.out.UserRepositoryPort;
import com.example.registrarusuario.domain.port.out.ValidationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRegistrationService Tests")
class UserRegistrationServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private ValidationPort validationPort;

    @Mock
    private TokenGeneratorPort tokenGeneratorPort;

    @InjectMocks
    private UserRegistrationService userRegistrationService;

    private User testUser;
    private Phone testPhone;

    @BeforeEach
    void setUp() {
        testPhone = Phone.builder()
                .number("1234567")
                .citycode("1")
                .contrycode("57")
                .build();

        testUser = User.builder()
                .name("Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password("Hunter2")
                .phones(List.of(testPhone))
                .build();
    }

    @Test
    @DisplayName("Debe registrar usuario exitosamente cuando todos los datos son válidos")
    void shouldRegisterUserSuccessfully() {
        // Given
        when(validationPort.isValidEmail(anyString())).thenReturn(true);
        when(validationPort.isValidPassword(anyString())).thenReturn(true);
        when(userRepositoryPort.existsByEmail(anyString())).thenReturn(false);
        when(tokenGeneratorPort.generateToken(anyString())).thenReturn("test-jwt-token");

        User savedUser = User.builder()
                .id("uuid-123")
                .name(testUser.getName())
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .phones(testUser.getPhones())
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .token("test-jwt-token")
                .isactive(true)
                .build();

        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userRegistrationService.registerUser(testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("uuid-123");
        assertThat(result.getEmail()).isEqualTo("juan@rodriguez.org");
        assertThat(result.getToken()).isEqualTo("test-jwt-token");
        assertThat(result.getIsactive()).isTrue();

        verify(validationPort).isValidEmail("juan@rodriguez.org");
        verify(validationPort).isValidPassword("Hunter2");
        verify(userRepositoryPort).existsByEmail("juan@rodriguez.org");
        verify(tokenGeneratorPort).generateToken("juan@rodriguez.org");
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar InvalidFormatException cuando el email es inválido")
    void shouldThrowInvalidFormatExceptionWhenEmailIsInvalid() {
        // Given
        when(validationPort.isValidEmail(anyString())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userRegistrationService.registerUser(testUser))
                .isInstanceOf(InvalidFormatException.class)
                .hasMessage("El formato del correo es inválido");

        verify(validationPort).isValidEmail("juan@rodriguez.org");
        verify(validationPort, never()).isValidPassword(anyString());
        verify(userRepositoryPort, never()).existsByEmail(anyString());
        verify(tokenGeneratorPort, never()).generateToken(anyString());
        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar InvalidFormatException cuando la contraseña es inválida")
    void shouldThrowInvalidFormatExceptionWhenPasswordIsInvalid() {
        // Given
        when(validationPort.isValidEmail(anyString())).thenReturn(true);
        when(validationPort.isValidPassword(anyString())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userRegistrationService.registerUser(testUser))
                .isInstanceOf(InvalidFormatException.class)
                .hasMessage("El formato de la contraseña es inválido");

        verify(validationPort).isValidEmail("juan@rodriguez.org");
        verify(validationPort).isValidPassword("Hunter2");
        verify(userRepositoryPort, never()).existsByEmail(anyString());
        verify(tokenGeneratorPort, never()).generateToken(anyString());
        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar EmailAlreadyExistsException cuando el email ya existe")
    void shouldThrowEmailAlreadyExistsExceptionWhenEmailExists() {
        // Given
        when(validationPort.isValidEmail(anyString())).thenReturn(true);
        when(validationPort.isValidPassword(anyString())).thenReturn(true);
        when(userRepositoryPort.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userRegistrationService.registerUser(testUser))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("El correo ya registrado");

        verify(validationPort).isValidEmail("juan@rodriguez.org");
        verify(validationPort).isValidPassword("Hunter2");
        verify(userRepositoryPort).existsByEmail("juan@rodriguez.org");
        verify(tokenGeneratorPort, never()).generateToken(anyString());
        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe generar token JWT al registrar usuario")
    void shouldGenerateJwtTokenWhenRegisteringUser() {
        // Given
        String expectedToken = "eyJhbGciOiJIUzI1NiJ9.test.token";
        when(validationPort.isValidEmail(anyString())).thenReturn(true);
        when(validationPort.isValidPassword(anyString())).thenReturn(true);
        when(userRepositoryPort.existsByEmail(anyString())).thenReturn(false);
        when(tokenGeneratorPort.generateToken(anyString())).thenReturn(expectedToken);

        User savedUser = User.builder()
                .id("uuid-123")
                .name(testUser.getName())
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .phones(testUser.getPhones())
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .token(expectedToken)
                .isactive(true)
                .build();

        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userRegistrationService.registerUser(testUser);

        // Then
        assertThat(result.getToken()).isEqualTo(expectedToken);
        verify(tokenGeneratorPort).generateToken("juan@rodriguez.org");
    }

    @Test
    @DisplayName("Debe establecer isactive en true al crear usuario")
    void shouldSetIsActiveToTrueWhenCreatingUser() {
        // Given
        when(validationPort.isValidEmail(anyString())).thenReturn(true);
        when(validationPort.isValidPassword(anyString())).thenReturn(true);
        when(userRepositoryPort.existsByEmail(anyString())).thenReturn(false);
        when(tokenGeneratorPort.generateToken(anyString())).thenReturn("token");

        User savedUser = User.builder()
                .id("uuid-123")
                .name(testUser.getName())
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .phones(testUser.getPhones())
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .token("token")
                .isactive(true)
                .build();

        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userRegistrationService.registerUser(testUser);

        // Then
        assertThat(result.getIsactive()).isTrue();
    }
}

