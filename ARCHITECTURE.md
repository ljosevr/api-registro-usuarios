# Arquitectura Hexagonal + Domain-Driven Design (DDD)

Este proyecto implementa **Arquitectura Hexagonal (Ports & Adapters)** combinada con **Domain-Driven Design (DDD)**.

## 📋 Tabla de Contenidos
- [Domain-Driven Design (DDD)](#domain-driven-design-ddd)
- [Estructura de Carpetas](#estructura-de-carpetas)
- [Diagrama de Arquitectura](#diagrama-de-arquitectura-hexagonal)
- [Principios SOLID + DDD](#principios-solid--ddd)
- [DDD + Arquitectura Hexagonal Trabajando Juntos](#-ddd--arquitectura-hexagonal-cómo-trabajan-juntos)

---

## Domain-Driven Design (DDD)

El proyecto sigue los principios de **DDD** para mantener el dominio como el núcleo independiente del negocio:

### 🎯 Principios DDD Aplicados

**1. Dominio como Núcleo Independiente**
- El **domain** no depende de ninguna capa externa
- Define sus propios modelos, reglas de negocio y contratos (ports)
- Puede ser reutilizado en diferentes contextos (REST, CLI, gRPC)

**2. Ubiquitous Language (Lenguaje Ubicuo)**
- Nombres que reflejan el negocio: `User`, `Phone`, `RegisterUserUseCase`
- Excepciones del dominio: `EmailAlreadyExistsException`, `InvalidFormatException`
- Los mismos términos en código y en conversaciones del equipo

**3. Separación de Responsabilidades**
- **Modelos de Dominio** (`User`, `Phone`): Entidades de negocio puras
- **Servicios de Dominio** (`UserRegistrationService`): Lógica de negocio
- **Puertos** (`RegisterUserUseCase`, `UserRepositoryPort`): Contratos definidos por el dominio
- **Entidades JPA** (`UserEntity`, `PhoneEntity`): Detalles de persistencia (fuera del dominio)

**4. Inversión de Dependencias**
```
Domain define → Ports (interfaces)
Infrastructure implementa → Ports (adaptadores)

✅ Domain NO conoce Infrastructure
✅ Infrastructure SÍ conoce Domain
```

**5. Boundaries (Límites)**
- **Domain** ← Lógica de negocio pura
- **Application** ← Orquestación (controllers, DTOs)
- **Infrastructure** ← Detalles técnicos (BD, JWT, validaciones)

### 📦 Estructura DDD

**Domain Layer (Capa de Dominio)**:
- `model/` → Entidades de dominio (agregados)
- `port/in/` → Casos de uso (lo que el dominio ofrece)
- `port/out/` → Contratos externos (lo que el dominio necesita)
- `service/` → Servicios de dominio (lógica de negocio)
- `exception/` → Excepciones del dominio

**Application Layer (Capa de Aplicación)**:
- Adaptadores de entrada (Controllers)
- DTOs (objetos de transferencia)
- Mappers (conversión DTO ↔ Domain)

**Infrastructure Layer (Capa de Infraestructura)**:
- Adaptadores de salida (Repositories, APIs externas)
- Implementaciones técnicas (JWT, Validaciones)
- Configuración de frameworks (Spring)

### ✅ Beneficios de DDD en este Proyecto

1. **Dominio Protegido**: La lógica de negocio está aislada de detalles técnicos
2. **Testeable**: El dominio se puede testear sin Spring, sin BD, sin HTTP
3. **Mantenible**: Cambios en infraestructura NO afectan el dominio
4. **Escalable**: Fácil agregar nuevos casos de uso o adaptadores
5. **Portable**: El dominio puede usarse en diferentes tipos de aplicaciones

---

## Estructura de Carpetas

### Arquitectura Hexagonal - Estructura Completa

```
src/main/java/com/example/registrarusuario/
├── domain/                          # Capa de Dominio (Core)
│   ├── model/                       # Modelos de dominio
│   │   ├── User.java
│   │   └── Phone.java
│   ├── port/
│   │   ├── in/                      # Puertos de entrada (Use Cases)
│   │   │   └── RegisterUserUseCase.java
│   │   └── out/                     # Puertos de salida (Interfaces)
│   │       ├── UserRepositoryPort.java
│   │       ├── TokenGeneratorPort.java
│   │       └── ValidationPort.java
│   ├── service/                     # Servicios de dominio
│   │   └── UserRegistrationService.java
│   └── exception/                   # Excepciones de dominio
│       ├── EmailAlreadyExistsException.java
│       └── InvalidFormatException.java
│
├── application/                     # Capa de Aplicación
│   ├── controller/                  # Controladores REST
│   │   └── UserController.java
│   ├── dto/                         # DTOs (Records Java)
│   │   ├── UserRegistrationRequest.java
│   │   ├── UserRegistrationResponse.java
│   │   ├── PhoneRequest.java
│   │   ├── PhoneResponse.java
│   │   └── ErrorResponse.java
│   ├── mapper/                      # Mappers DTO <-> Domain
│   │   └── UserDtoMapper.java
│   └── exception/                   # Manejo global de excepciones
│       └── GlobalExceptionHandler.java
│
└── infrastructure/                  # Capa de Infraestructura
    ├── config/                      # Configuración de Spring
    │   └── BeanConfiguration.java
    ├── adapter/                     # Adaptadores de servicios externos
    │   ├── JwtTokenGeneratorAdapter.java
    │   └── RegexValidationAdapter.java
    └── persistence/                 # Adaptador de persistencia
        ├── entity/                  # Entidades JPA
        │   ├── UserEntity.java
        │   └── PhoneEntity.java
        ├── repository/              # Repositorios JPA
        │   └── JpaUserRepository.java
        ├── adapter/                 # Implementación del puerto
        │   └── UserRepositoryAdapter.java
        └── mapper/                  # Mapper Entity <-> Domain
            └── UserEntityMapper.java
```

---

## Diagrama de Arquitectura Hexagonal

### Visión General

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

## Principios SOLID + DDD

El proyecto combina **SOLID** con **DDD** para lograr un código robusto y mantenible:

### S - Single Responsibility Principle
Cada clase tiene una única responsabilidad bien definida:
- `UserRegistrationService` → Solo lógica de registro de usuarios
- `UserController` → Solo maneja peticiones HTTP
- `UserRepositoryAdapter` → Solo persiste usuarios
- `UserDtoMapper` → Solo convierte DTOs ↔ Domain

### O - Open/Closed Principle
Abierto para extensión, cerrado para modificación:
- Nuevos casos de uso → Implementar `Port IN`
- Nuevos repositorios → Implementar `Port OUT`
- Nuevas validaciones → Implementar `ValidationPort`
- **Ejemplo**: Cambiar de H2 a PostgreSQL sin tocar el dominio

### L - Liskov Substitution Principle
Los adaptadores son intercambiables:
- `JwtTokenGeneratorAdapter` puede ser reemplazado por `UuidTokenGeneratorAdapter`
- `RegexValidationAdapter` puede ser reemplazado por `DatabaseValidationAdapter`
- Cualquier implementación de un puerto es válida

### I - Interface Segregation Principle
Interfaces pequeñas y específicas (ports):
- `RegisterUserUseCase` → Solo registrar usuario
- `UserRepositoryPort` → Solo operaciones de repositorio
- `TokenGeneratorPort` → Solo generar token
- `ValidationPort` → Solo validar

### D - Dependency Inversion Principle ⭐ (Clave en DDD)
**El dominio define interfaces, la infraestructura las implementa**:
```
✅ Domain (UserRegistrationService) → depende de → Port OUT (UserRepositoryPort)
✅ Infrastructure (UserRepositoryAdapter) → implementa → Port OUT
❌ NUNCA: Domain → depende de → Infrastructure
```

### Otros Principios Aplicados

#### DRY (Don't Repeat Yourself)
- Mappers reutilizables para conversiones
- Validaciones centralizadas
- Manejo de excepciones global

#### Clean Code
- Records de Java para DTOs inmutables
- Lombok solo donde es necesario (@Getter, @Setter, @Builder, @RequiredArgsConstructor)
- Nombres descriptivos y claros
- Lenguaje ubicuo (DDD): términos del negocio en el código

---

## 🎓 DDD + Arquitectura Hexagonal: Cómo Trabajan Juntos

Este proyecto demuestra cómo **DDD** y **Arquitectura Hexagonal** se complementan perfectamente:

### 🎯 DDD aporta:
- **Lenguaje Ubicuo**: Términos del negocio en el código
- **Modelo de Dominio Rico**: Entidades con comportamiento
- **Capas bien definidas**: Domain, Application, Infrastructure
- **Separación de conceptos**: Domain models vs Persistence models

### 🏗️ Arquitectura Hexagonal aporta:
- **Puertos e Interfaces**: Contratos definidos por el dominio
- **Adaptadores intercambiables**: Fácil cambiar implementaciones
- **Independencia de frameworks**: El dominio no conoce Spring
- **Testabilidad**: Fácil hacer mocks de adaptadores

### 💡 El Resultado:
```
DDD define → QUÉ hace el sistema (lógica de negocio)
Hexagonal define → CÓMO se conecta (puertos y adaptadores)

Juntos = Sistema robusto, mantenible y escalable ✨
```

### Ejemplo Práctico en este Proyecto:

**DDD dice**: "Necesito registrar un usuario con validaciones de negocio"
- ✅ Modelo: `User` (entidad de dominio)
- ✅ Caso de Uso: `RegisterUserUseCase` (port IN)
- ✅ Servicio: `UserRegistrationService` (lógica)

**Hexagonal dice**: "Te proporciono adaptadores para hacerlo"
- ✅ Adaptador REST: `UserController` (entrada)
- ✅ Adaptador BD: `UserRepositoryAdapter` (salida)
- ✅ Adaptador JWT: `JwtTokenGeneratorAdapter` (salida)

**Beneficio**: Puedo cambiar REST por GraphQL o H2 por PostgreSQL **sin tocar el dominio** 🎉

---

## Ventajas de esta Arquitectura

✅ **Testabilidad**: Fácil crear mocks de los puertos  
✅ **Mantenibilidad**: Cambios en infraestructura no afectan al dominio  
✅ **Escalabilidad**: Agregar nuevas funcionalidades sin modificar existentes  
✅ **Independencia**: El dominio no conoce Spring, JPA, o HTTP  
✅ **Claridad**: Separación clara de responsabilidades  
✅ **Flexibilidad**: Cambiar H2 por PostgreSQL sin tocar el dominio  

