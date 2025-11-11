package com.example.registrarusuario.application.controller;

import com.example.registrarusuario.application.dto.ErrorResponse;
import com.example.registrarusuario.application.dto.UserRegistrationRequest;
import com.example.registrarusuario.application.dto.UserRegistrationResponse;
import com.example.registrarusuario.application.mapper.UserDtoMapper;
import com.example.registrarusuario.domain.model.User;
import com.example.registrarusuario.domain.port.in.RegisterUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "API para gestión de usuarios")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final UserDtoMapper userDtoMapper;

    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea un nuevo usuario en el sistema con validación de email único, formato de email y contraseña. Genera un token JWT que se persiste junto con el usuario."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario registrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRegistrationResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id": "550e8400-e29b-41d4-a716-446655440000",
                                              "name": "Juan Rodriguez",
                                              "email": "juan@rodriguez.org",
                                              "phones": [
                                                {
                                                  "number": "1234567",
                                                  "citycode": "1",
                                                  "contrycode": "57"
                                                }
                                              ],
                                              "created": "2025-11-10T10:30:00",
                                              "modified": "2025-11-10T10:30:00",
                                              "last_login": "2025-11-10T10:30:00",
                                              "token": "eyJhbGciOiJIUzI1NiJ9...",
                                              "isactive": true
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos (email o contraseña con formato incorrecto)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "mensaje": "El formato del correo es inválido"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El email ya está registrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "mensaje": "El correo ya registrado"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping(
            value = "/register",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<UserRegistrationResponse> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {

        User user = userDtoMapper.toDomain(request);
        User registeredUser = registerUserUseCase.registerUser(user);
        UserRegistrationResponse response = userDtoMapper.toResponse(registeredUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

