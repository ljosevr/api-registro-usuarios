# API de Registro de Usuarios

## Descripción
API REST para registro de usuarios implementada con Spring Boot, siguiendo arquitectura hexagonal y principios SOLID.

---

## 🚀 Inicio Rápido

### Prerrequisitos
- Java 17 o superior
- Git

### Paso 1: Clonar el Repositorio
```bash
git clone https://github.com/ljosevr/api-registro-usuarios.git
cd api-registro-usuarios
```

### Paso 2: Configurar Variables de Entorno (JWT Secret)

#### Opción A: Con archivo .env (Recomendado)

1. **Crear archivo .env** desde la plantilla:
   ```bash
   cp .env.example .env
   ```

2. **Generar un JWT secret seguro**:
   ```bash
   openssl rand -base64 64
   ```
   
   Esto generará un string aleatorio seguro. Cópialo.

3. **Editar el archivo .env** y agregar tu secret:
   ```bash
   # Abrir con tu editor favorito
   nano .env
   # o
   vim .env
   # o
   code .env
   ```

   Contenido del archivo .env:
   ```properties
   JWT_SECRET=pega-aqui-el-secret-que-generaste-con-openssl
   JWT_EXPIRATION=86400000
   ```

4. **Guardar y cerrar** el archivo

#### Opción B: Variable de entorno en terminal (Temporal)

```bash
export JWT_SECRET="tu-secret-generado-con-openssl-aqui"
```

#### Opción C: Para pruebas rápidas (Solo desarrollo local)

Puedes omitir este paso. La aplicación usará un valor por defecto:
```
changeme-only-for-local-development
```
⚠️ **NUNCA uses este valor en producción**

### Paso 3: Compilar el Proyecto
```bash
./gradlew clean build
```

**Resultado esperado**:
```
BUILD SUCCESSFUL
43 tests passed ✅
```

### Paso 4: Ejecutar la Aplicación
```bash
./gradlew bootRun
```

**La aplicación estará disponible en**: http://localhost:8080

### Paso 5: Verificar que Funciona

#### Opción A: Con curl
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Rodriguez",
    "email": "juan@test.com",
    "password": "Hunter2",
    "phones": [{"number": "1234567", "citycode": "1", "contrycode": "57"}]
  }'
```

#### Opción B: Con Swagger UI
Abre en tu navegador: http://localhost:8080/swagger-ui/index.html

#### Opción C: Con script de pruebas
```bash
chmod +x test-api.sh
./test-api.sh
```

### Paso 6: Acceder a H2 Console (Opcional)
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Usuario**: `sa`
- **Password**: (dejar vacío)

---

## 🎯 Resumen de Comandos

```bash
# 1. Clonar
git clone https://github.com/ljosevr/api-registro-usuarios.git
cd api-registro-usuarios

# 2. Configurar JWT (opcional para desarrollo)
cp .env.example .env
# Editar .env con tu secret generado

# 3. Compilar
./gradlew clean build

# 4. Ejecutar
./gradlew bootRun

# 5. Probar
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@example.com","password":"Test123","phones":[{"number":"123","citycode":"1","contrycode":"57"}]}'
```

---

## Tecnologías Utilizadas
- Java 17
- Spring Boot 3.5.7
- Spring Data JPA
- H2 Database (en memoria)
- JWT (JSON Web Tokens)
- Lombok
- Gradle

## Arquitectura

Este proyecto implementa **Arquitectura Hexagonal (Ports & Adapters)** combinada con **Domain-Driven Design (DDD)**.

### Domain-Driven Design (DDD)

El proyecto sigue los principios de **DDD** para mantener el dominio como el núcleo independiente del negocio:

#### 🎯 Principios DDD Aplicados

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

#### 📦 Estructura DDD

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

#### ✅ Beneficios de DDD en este Proyecto

1. **Dominio Protegido**: La lógica de negocio está aislada de detalles técnicos
2. **Testeable**: El dominio se puede testear sin Spring, sin BD, sin HTTP
3. **Mantenible**: Cambios en infraestructura NO afectan el dominio
4. **Escalable**: Fácil agregar nuevos casos de uso o adaptadores
5. **Portable**: El dominio puede usarse en diferentes tipos de aplicaciones

---

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

## Principios Aplicados

### SOLID + DDD

El proyecto combina **SOLID** con **DDD** para lograr un código robusto y mantenible:

#### S - Single Responsibility Principle
Cada clase tiene una única responsabilidad bien definida:
- `UserRegistrationService` → Solo lógica de registro de usuarios
- `UserController` → Solo maneja peticiones HTTP
- `UserRepositoryAdapter` → Solo persiste usuarios
- `UserDtoMapper` → Solo convierte DTOs ↔ Domain

#### O - Open/Closed Principle
Abierto para extensión, cerrado para modificación:
- Nuevos casos de uso → Implementar `Port IN`
- Nuevos repositorios → Implementar `Port OUT`
- Nuevas validaciones → Implementar `ValidationPort`
- **Ejemplo**: Cambiar de H2 a PostgreSQL sin tocar el dominio

#### L - Liskov Substitution Principle
Los adaptadores son intercambiables:
- `JwtTokenGeneratorAdapter` puede ser reemplazado por `UuidTokenGeneratorAdapter`
- `RegexValidationAdapter` puede ser reemplazado por `DatabaseValidationAdapter`
- Cualquier implementación de un puerto es válida

#### I - Interface Segregation Principle
Interfaces pequeñas y específicas (ports):
- `RegisterUserUseCase` → Solo registrar usuario
- `UserRepositoryPort` → Solo operaciones de repositorio
- `TokenGeneratorPort` → Solo generar token
- `ValidationPort` → Solo validar

#### D - Dependency Inversion Principle ⭐ (Clave en DDD)
**El dominio define interfaces, la infraestructura las implementa**:
```
✅ Domain (UserRegistrationService) → depende de → Port OUT (UserRepositoryPort)
✅ Infrastructure (UserRepositoryAdapter) → implementa → Port OUT
❌ NUNCA: Domain → depende de → Infrastructure
```

### DRY (Don't Repeat Yourself)
- Mappers reutilizables para conversiones
- Validaciones centralizadas
- Manejo de excepciones global

### Clean Code
- Records de Java para DTOs inmutables
- Lombok solo donde es necesario (@Getter, @Setter, @Builder, @RequiredArgsConstructor)
- Nombres descriptivos y claros
- Lenguaje ubicuo (DDD): términos del negocio en el código

## Configuración

### application.properties
```properties
# Validación (expresiones regulares configurables)
app.validation.email.regex=^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$
app.validation.password.regex=^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$

# JWT
app.jwt.secret=mySecretKeyForJWTTokenGenerationThatShouldBeVeryLongAndSecure123456789
app.jwt.expiration=86400000
```

### Validaciones
- **Email**: Formato estándar de correo electrónico (configurable)
- **Password**: Al menos una mayúscula, una minúscula y un dígito (configurable)

## API Endpoints

### Registrar Usuario
**POST** `/api/users/register`

#### Request Body
```json
{
  "name": "Juan Rodriguez",
  "email": "juan@rodriguez.org",
  "password": "Hunter2",
  "phones": [
    {
      "number": "1234567",
      "citycode": "1",
      "contrycode": "57"
    }
  ]
}
```

#### Response Exitosa (201 Created)
```json
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
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "isactive": true
}
```

#### Errores Posibles

**409 Conflict** - Email ya registrado
```json
{
  "mensaje": "El correo ya registrado"
}
```

**400 Bad Request** - Formato de email inválido
```json
{
  "mensaje": "El formato del correo es inválido"
}
```

**400 Bad Request** - Formato de contraseña inválido
```json
{
  "mensaje": "El formato de la contraseña es inválido"
}
```

**400 Bad Request** - Validación de campos
```json
{
  "mensaje": "El nombre es obligatorio, El correo es obligatorio"
}
```

## Ejecutar la Aplicación

### Con Gradle
```bash
./gradlew bootRun
```

### Compilar
```bash
./gradlew clean build
```

### Ejecutar JAR
```bash
java -jar build/libs/registrarusuario-0.0.1-SNAPSHOT.jar
```

La aplicación se ejecutará en `http://localhost:8080`

## Consola H2
La consola de H2 está habilitada en: `http://localhost:8080/h2-console`

- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Usuario**: `sa`
- **Contraseña**: (vacío)

## Ejemplos de Uso

### Registro Exitoso
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Rodriguez",
    "email": "juan@rodriguez.org",
    "password": "Hunter2",
    "phones": [
      {
        "number": "1234567",
        "citycode": "1",
        "contrycode": "57"
      }
    ]
  }'
```

### Email Duplicado
```bash
# Ejecutar el comando anterior dos veces para obtener error 409
```

### Email Inválido
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Rodriguez",
    "email": "email-invalido",
    "password": "Hunter2",
    "phones": [
      {
        "number": "1234567",
        "citycode": "1",
        "contrycode": "57"
      }
    ]
  }'
```

### Contraseña Inválida
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Rodriguez",
    "email": "juan@test.cl",
    "password": "abc",
    "phones": [
      {
        "number": "1234567",
        "citycode": "1",
        "contrycode": "57"
      }
    ]
  }'
```

## Testing

### Ejecutar Tests

```bash
# Todos los tests
./gradlew test

# Con reporte de cobertura
./gradlew test jacocoTestReport

# Ver reporte HTML
open build/reports/jacoco/test/html/index.html
```

### Tests Implementados

#### Tests Unitarios
1. **UserRegistrationServiceTest** - Tests de lógica de negocio
   - Registro exitoso de usuario
   - Validación de email inválido
   - Validación de contraseña inválida
   - Detección de email duplicado
   - Generación de token JWT
   - Configuración de isactive

2. **RegexValidationAdapterTest** - Tests de validación
   - Validación de emails correctos e incorrectos
   - Validación de contraseñas con diferentes formatos
   - Manejo de valores nulos y vacíos

3. **JwtTokenGeneratorAdapterTest** - Tests de generación de tokens
   - Generación de tokens JWT válidos
   - Formato correcto de tokens
   - Unicidad de tokens

#### Tests de Integración
4. **UserControllerTest** - Tests del endpoint REST
   - Registro exitoso (201)
   - Email duplicado (409)
   - Email inválido (400)
   - Contraseña inválida (400)
   - Campos vacíos (400)
   - Múltiples teléfonos

### Cobertura
Los tests cubren:
- ✅ Capa de dominio (lógica de negocio)
- ✅ Capa de aplicación (controladores REST)
- ✅ Capa de infraestructura (adaptadores)
- ✅ Validaciones
- ✅ Manejo de errores

---

## 📖 Documentación de API con Swagger

La API incluye documentación interactiva con **OpenAPI 3.0 (Swagger)**.

### Acceder a Swagger UI

Una vez que la aplicación esté corriendo, accede a:

**URL**: http://localhost:8080/swagger-ui/index.html

### Características de Swagger

✅ **Documentación interactiva** de todos los endpoints  
✅ **Probar la API** directamente desde el navegador  
✅ **Ejemplos de request/response** pre-configurados  
✅ **Esquemas de datos** detallados  
✅ **Códigos de respuesta** HTTP documentados  

### Endpoints Documentados

#### POST /api/users/register
- **Descripción**: Registrar nuevo usuario
- **Request Body**: UserRegistrationRequest (JSON)
- **Responses**:
  - `201 Created`: Usuario registrado exitosamente
  - `400 Bad Request`: Datos inválidos
  - `409 Conflict`: Email ya registrado

### OpenAPI JSON

La especificación OpenAPI en formato JSON está disponible en:

**URL**: http://localhost:8080/v3/api-docs

---

## 🔒 Validación Content-Type: SOLO JSON

El endpoint **SOLO acepta y retorna JSON**. Cualquier otro Content-Type es rechazado.

### ✅ Tests de Validación (11 tests adicionales)

Se han implementado tests específicos para verificar que:
- ✅ SOLO acepta `Content-Type: application/json`
- ✅ SOLO retorna `Content-Type: application/json`  
- ✅ Rechaza XML, Text, Form-Data, etc. con HTTP 415
- ✅ Errores también se retornan en JSON

### Ejecutar Tests de Content-Type

```bash
./gradlew test --tests ContentTypeValidationTest
# 11 tests - todos deben pasar ✅
```

### Pruebas Manuales con cURL

#### ✅ Request JSON Válido (FUNCIONA)
```bash
curl -i -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@example.com","password":"Test123","phones":[{"number":"123","citycode":"1","contrycode":"57"}]}'

# HTTP/1.1 201 Created
# Content-Type: application/json
```

#### ❌ Request sin Content-Type (RECHAZADO)
```bash
curl -i -X POST http://localhost:8080/api/users/register \
  -d '{"name":"Test","email":"test@example.com"}'

# HTTP/1.1 415 Unsupported Media Type
# Content-Type: application/json
# {"mensaje": "Content-Type 'null' no está soportado..."}
```

#### ❌ Request con XML (RECHAZADO)
```bash
curl -i -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/xml" \
  -d '<user><name>Test</name></user>'

# HTTP/1.1 415 Unsupported Media Type
# Content-Type: application/json
# {"mensaje": "Content-Type 'application/xml' no está soportado..."}
```

#### ❌ Request con Form Data (RECHAZADO)
```bash
curl -i -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d 'name=Test&email=test@example.com'

# HTTP/1.1 415 Unsupported Media Type
# Content-Type: application/json
```

**Documentación completa**: Ver `PRUEBAS_SOLO_JSON.md`

---

## Características Destacadas

✅ **Domain-Driven Design (DDD)** aplicado correctamente  
✅ **Arquitectura Hexagonal** (Ports & Adapters)  
✅ **Principios SOLID** aplicados  
✅ **DRY y Clean Code**  
✅ Records de Java para DTOs inmutables  
✅ Uso apropiado de Lombok (sin @Data)  
✅ Validaciones con expresiones regulares configurables  
✅ JWT para tokens de autenticación  
✅ Manejo global de excepciones con formato JSON uniforme  
✅ Base de datos H2 en memoria  
✅ UUIDs para identificadores  
✅ Mappers para separar capas  
✅ Respuestas HTTP apropiadas (201, 400, 409, 500)  
✅ **Documentación Swagger/OpenAPI 3.0** interactiva  
✅ **Tests unitarios y de integración** completos (43 tests)  
✅ **Cobertura de todas las capas** (Domain, Application, Infrastructure)  
✅ **Dominio independiente** de frameworks y tecnologías  

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

## Autor
Desarrollado siguiendo las mejores prácticas de desarrollo de software: DDD, Arquitectura Hexagonal, SOLID, Clean Code.

