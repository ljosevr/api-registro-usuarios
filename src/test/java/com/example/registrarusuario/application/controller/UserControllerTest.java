package com.example.registrarusuario.application.controller;

import com.example.registrarusuario.application.dto.PhoneRequest;
import com.example.registrarusuario.application.dto.UserRegistrationRequest;
import com.example.registrarusuario.application.dto.UserRegistrationResponse;
import com.example.registrarusuario.application.mapper.UserDtoMapper;
import com.example.registrarusuario.domain.exception.EmailAlreadyExistsException;
import com.example.registrarusuario.domain.exception.InvalidFormatException;
import com.example.registrarusuario.domain.model.Phone;
import com.example.registrarusuario.domain.model.User;
import com.example.registrarusuario.domain.port.in.RegisterUserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("UserController Integration Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegisterUserUseCase registerUserUseCase;

    @MockBean
    private UserDtoMapper userDtoMapper;

    private UserRegistrationRequest validRequest;
    private User domainUser;
    private User registeredUser;
    private UserRegistrationResponse response;

    @BeforeEach
    void setUp() {
        PhoneRequest phoneRequest = new PhoneRequest("1234567", "1", "57");

        validRequest = new UserRegistrationRequest(
                "Juan Rodriguez",
                "juan@rodriguez.org",
                "Hunter2",
                List.of(phoneRequest)
        );

        Phone phone = Phone.builder()
                .number("1234567")
                .citycode("1")
                .contrycode("57")
                .build();

        domainUser = User.builder()
                .name("Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password("Hunter2")
                .phones(List.of(phone))
                .build();

        registeredUser = User.builder()
                .id("uuid-123")
                .name("Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password("Hunter2")
                .phones(List.of(phone))
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .token("eyJhbGciOiJIUzI1NiJ9.test.token")
                .isactive(true)
                .build();

        response = new UserRegistrationResponse(
                "uuid-123",
                "Juan Rodriguez",
                "juan@rodriguez.org",
                List.of(new com.example.registrarusuario.application.dto.PhoneResponse("1234567", "1", "57")),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                "eyJhbGciOiJIUzI1NiJ9.test.token",
                true
        );
    }

    @Test
    @DisplayName("POST /api/users/register - Debe registrar usuario exitosamente y retornar 201")
    void shouldRegisterUserSuccessfully() throws Exception {
        // Given
        when(userDtoMapper.toDomain(any(UserRegistrationRequest.class))).thenReturn(domainUser);
        when(registerUserUseCase.registerUser(any(User.class))).thenReturn(registeredUser);
        when(userDtoMapper.toResponse(any(User.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("uuid-123"))
                .andExpect(jsonPath("$.name").value("Juan Rodriguez"))
                .andExpect(jsonPath("$.email").value("juan@rodriguez.org"))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.isactive").value(true))
                .andExpect(jsonPath("$.phones").isArray())
                .andExpect(jsonPath("$.phones[0].number").value("1234567"));
    }

    @Test
    @DisplayName("POST /api/users/register - Debe retornar 409 cuando email ya existe")
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        // Given
        when(userDtoMapper.toDomain(any(UserRegistrationRequest.class))).thenReturn(domainUser);
        when(registerUserUseCase.registerUser(any(User.class)))
                .thenThrow(new EmailAlreadyExistsException("El correo ya registrado"));

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensaje").value("El correo ya registrado"));
    }

    @Test
    @DisplayName("POST /api/users/register - Debe retornar 400 cuando email es inválido")
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        // Given
        when(userDtoMapper.toDomain(any(UserRegistrationRequest.class))).thenReturn(domainUser);
        when(registerUserUseCase.registerUser(any(User.class)))
                .thenThrow(new InvalidFormatException("El formato del correo es inválido"));

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensaje").value("El formato del correo es inválido"));
    }

    @Test
    @DisplayName("POST /api/users/register - Debe retornar 400 cuando contraseña es inválida")
    void shouldReturn400WhenPasswordIsInvalid() throws Exception {
        // Given
        when(userDtoMapper.toDomain(any(UserRegistrationRequest.class))).thenReturn(domainUser);
        when(registerUserUseCase.registerUser(any(User.class)))
                .thenThrow(new InvalidFormatException("El formato de la contraseña es inválido"));

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensaje").value("El formato de la contraseña es inválido"));
    }

    @Test
    @DisplayName("POST /api/users/register - Debe retornar 400 cuando nombre está vacío")
    void shouldReturn400WhenNameIsEmpty() throws Exception {
        // Given
        UserRegistrationRequest invalidRequest = new UserRegistrationRequest(
                "",
                "juan@rodriguez.org",
                "Hunter2",
                List.of(new PhoneRequest("1234567", "1", "57"))
        );

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    @DisplayName("POST /api/users/register - Debe retornar 400 cuando email está vacío")
    void shouldReturn400WhenEmailIsEmpty() throws Exception {
        // Given
        UserRegistrationRequest invalidRequest = new UserRegistrationRequest(
                "Juan Rodriguez",
                "",
                "Hunter2",
                List.of(new PhoneRequest("1234567", "1", "57"))
        );

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    @DisplayName("POST /api/users/register - Debe retornar 400 cuando contraseña está vacía")
    void shouldReturn400WhenPasswordIsEmpty() throws Exception {
        // Given
        UserRegistrationRequest invalidRequest = new UserRegistrationRequest(
                "Juan Rodriguez",
                "juan@rodriguez.org",
                "",
                List.of(new PhoneRequest("1234567", "1", "57"))
        );

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    @DisplayName("POST /api/users/register - Debe retornar 400 cuando lista de teléfonos está vacía")
    void shouldReturn400WhenPhonesListIsEmpty() throws Exception {
        // Given
        UserRegistrationRequest invalidRequest = new UserRegistrationRequest(
                "Juan Rodriguez",
                "juan@rodriguez.org",
                "Hunter2",
                List.of()
        );

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    @DisplayName("POST /api/users/register - Debe aceptar múltiples teléfonos")
    void shouldAcceptMultiplePhones() throws Exception {
        // Given
        UserRegistrationRequest multiPhoneRequest = new UserRegistrationRequest(
                "Ana Martinez",
                "ana@example.cl",
                "Secure123",
                List.of(
                        new PhoneRequest("1111111", "1", "57"),
                        new PhoneRequest("2222222", "1", "57"),
                        new PhoneRequest("3333333", "2", "57")
                )
        );

        when(userDtoMapper.toDomain(any(UserRegistrationRequest.class))).thenReturn(domainUser);
        when(registerUserUseCase.registerUser(any(User.class))).thenReturn(registeredUser);
        when(userDtoMapper.toResponse(any(User.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(multiPhoneRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}

