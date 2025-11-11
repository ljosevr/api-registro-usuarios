# Diagrama de Arquitectura Hexagonal

## Visión General

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                          CAPA DE APLICACIÓN                                  │
│  ┌────────────────────────────────────────────────────────────────────────┐  │
│  │                        UserController                                  │  │
│  │                  (REST API - Punto de Entrada)                         │  │
│  └────────────────────────────────────────────────────────────────────────┘  │
│                                   ↓                                          │
│  ┌────────────────────────────────────────────────────────────────────────┐  │
│  │                       UserDtoMapper                                    │  │
│  │              (Convierte Request/Response ↔ Domain)                     │  │
│  └────────────────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌──────────────────────────────────────────────────────────────────────────────┐
│                          CAPA DE DOMINIO (CORE)                              │
│  ┌────────────────────────────────────────────────────────────────────────┐  │
│  │                   RegisterUserUseCase (Puerto IN)                      │  │
│  └────────────────────────────────────────────────────────────────────────┘  │
│                                   ↓                                          │
│  ┌────────────────────────────────────────────────────────────────────────┐  │
│  │                  UserRegistrationService                               │  │
│  │                   (Lógica de Negocio)                                  │  │
│  │  • Validar formato email                                               │  │
│  │  • Validar formato password                                            │  │
│  │  • Verificar email único                                               │  │
│  │  • Generar token JWT                                                   │  │
│  │  • Crear usuario con timestamps                                        │  │
│  └────────────────────────────────────────────────────────────────────────┘  │
│                                   ↓                                          │
│  ┌─────────────────┬──────────────────────┬──────────────────────────────┐  │
│  │UserRepositoryPort│  ValidationPort      │  TokenGeneratorPort          │  │
│  │   (Puerto OUT)   │   (Puerto OUT)       │    (Puerto OUT)              │  │
│  └─────────────────┴──────────────────────┴──────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌──────────────────────────────────────────────────────────────────────────────┐
│                      CAPA DE INFRAESTRUCTURA                                 │
│  ┌────────────────┬─────────────────────┬────────────────────────────────┐  │
│  │ UserRepository │ RegexValidation     │  JwtTokenGenerator             │  │
│  │    Adapter     │    Adapter          │     Adapter                    │  │
│  │                │                     │                                │  │
│  │ • Convierte    │ • Lee regex de      │  • Genera JWT                  │  │
│  │   Domain ↔     │   properties        │  • Firma con secret            │  │
│  │   Entity       │ • Valida email      │  • Incluye expiración          │  │
│  │ • Persiste     │ • Valida password   │                                │  │
│  │   en H2        │                     │                                │  │
│  └────────────────┴─────────────────────┴────────────────────────────────┘  │
│                     ↓                                                        │
│  ┌────────────────────────────────────────────────────────────────────────┐  │
│  │                     JpaUserRepository                                  │  │
│  │                   (Spring Data JPA)                                    │  │
│  └────────────────────────────────────────────────────────────────────────┘  │
│                     ↓                                                        │
│  ┌────────────────────────────────────────────────────────────────────────┐  │
│  │                      H2 Database                                       │  │
│  │                    (En Memoria)                                        │  │
│  └────────────────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────────────┘
```

## Flujo de Datos

### Request (POST /api/users/register)
```
UserRegistrationRequest (JSON)
         ↓
UserController
         ↓
UserDtoMapper.toDomain()
         ↓
User (Domain Model)
         ↓
RegisterUserUseCase.registerUser()
         ↓
UserRegistrationService
    ├→ ValidationPort.isValidEmail()
    │       ↓
    │  RegexValidationAdapter
    │
    ├→ ValidationPort.isValidPassword()
    │       ↓
    │  RegexValidationAdapter
    │
    ├→ UserRepositoryPort.existsByEmail()
    │       ↓
    │  UserRepositoryAdapter
    │       ↓
    │  JpaUserRepository
    │       ↓
    │  H2 Database
    │
    ├→ TokenGeneratorPort.generateToken()
    │       ↓
    │  JwtTokenGeneratorAdapter
    │
    └→ UserRepositoryPort.save()
            ↓
       UserRepositoryAdapter
            ↓
       UserEntityMapper.toEntity()
            ↓
       JpaUserRepository.save()
            ↓
       H2 Database
            ↓
       UserEntity (JPA)
            ↓
       UserEntityMapper.toDomain()
            ↓
       User (Domain Model)
            ↓
UserDtoMapper.toResponse()
         ↓
UserRegistrationResponse (JSON)
```

## Modelos de Datos

### Domain Model
```
User
├── id: String (UUID)
├── name: String
├── email: String
├── password: String
├── phones: List<Phone>
├── created: LocalDateTime
├── modified: LocalDateTime
├── lastLogin: LocalDateTime
├── token: String (JWT)
└── isactive: Boolean

Phone
├── id: String (UUID)
├── number: String
├── citycode: String
└── contrycode: String
```

### JPA Entity
```
UserEntity
├── id: String (UUID) @Id
├── name: String
├── email: String @Column(unique=true)
├── password: String
├── phones: List<PhoneEntity> @OneToMany
├── created: LocalDateTime @CreationTimestamp
├── modified: LocalDateTime @UpdateTimestamp
├── lastLogin: LocalDateTime
├── token: String
└── isactive: Boolean

PhoneEntity
├── id: String (UUID) @Id
├── number: String
├── citycode: String
├── contrycode: String
└── user: UserEntity @ManyToOne
```

## Principios SOLID en Acción

### Single Responsibility Principle (SRP)
- **UserController**: Solo maneja HTTP requests/responses
- **UserRegistrationService**: Solo contiene lógica de registro
- **UserRepositoryAdapter**: Solo adapta entre dominio y persistencia
- **UserDtoMapper**: Solo convierte DTOs a dominio
- **UserEntityMapper**: Solo convierte entidades a dominio

### Open/Closed Principle (OCP)
- Nuevos validadores se pueden agregar implementando `ValidationPort`
- Nuevos generadores de token implementando `TokenGeneratorPort`
- Nuevas estrategias de persistencia implementando `UserRepositoryPort`

### Liskov Substitution Principle (LSP)
- Cualquier implementación de `ValidationPort` es intercambiable
- `RegexValidationAdapter` puede ser reemplazado por `DatabaseValidationAdapter`
- `JwtTokenGeneratorAdapter` puede ser reemplazado por `UuidTokenGeneratorAdapter`

### Interface Segregation Principle (ISP)
- `ValidationPort`: Solo métodos de validación
- `TokenGeneratorPort`: Solo generación de token
- `UserRepositoryPort`: Solo operaciones de repositorio
- Interfaces pequeñas y específicas

### Dependency Inversion Principle (DIP)
- `UserRegistrationService` depende de **abstracciones** (Ports)
- No depende de implementaciones concretas
- Los adaptadores se inyectan mediante Spring
- Fácil testing con mocks

## Ventajas de esta Arquitectura

✅ **Testabilidad**: Fácil crear mocks de los puertos  
✅ **Mantenibilidad**: Cambios en infraestructura no afectan al dominio  
✅ **Escalabilidad**: Agregar nuevas funcionalidades sin modificar existentes  
✅ **Independencia**: El dominio no conoce Spring, JPA, o HTTP  
✅ **Claridad**: Separación clara de responsabilidades  
✅ **Flexibilidad**: Cambiar H2 por PostgreSQL sin tocar el dominio  

