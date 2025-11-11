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

⚠️ **NUNCA uses este valor en producción**

### Paso 3: Compilar el Proyecto
```bash
./gradlew clean build
```

**Resultado esperado**:
```
BUILD SUCCESSFUL
tests passed ✅
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
Abre en tu navegador: **http://localhost:8080/swagger-ui/index.html**

**Nota**: Asegúrate de que la aplicación esté corriendo (`./gradlew bootRun`).

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

## Tecnologías Utilizadas
- Java 17
- Spring Boot 3.5.7
- Spring Data JPA
- H2 Database (en memoria)
- JWT (JSON Web Tokens)
- Lombok
- Gradle

---
## Arquitectura

Este proyecto implementa **Arquitectura Hexagonal (Ports & Adapters)** combinada con **Domain-Driven Design (DDD)**.

📖 **[Ver Documentación Completa de Arquitectura →](ARCHITECTURE.md)**

La arquitectura está basada en principios SOLID, DDD y Clean Code, con separación clara entre:
- **Domain** (núcleo de negocio independiente)
- **Application** (controladores y DTOs)
- **Infrastructure** (adaptadores y persistencia)

---
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

### Scripts de Base de Datos

El proyecto incluye scripts SQL para diferentes bases de datos:
- **[H2 Database](sql/schema-h2.sql)** (por defecto)

**Nota**: H2 crea las tablas automáticamente usando JPA. Los scripts son útiles para migrar a PostgreSQL o MySQL.

---
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

### Ejecutar JAR
```bash
java -jar build/libs/registrarusuario-0.0.1-SNAPSHOT.jar
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
---
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

### Endpoints Documentados

#### POST /api/users/register
- **Descripción**: Registrar nuevo usuario
- **Request Body**: UserRegistrationRequest (JSON)
- **Responses**:
  - `201 Created`: Usuario registrado exitosamente
  - `400 Bad Request`: Datos inválidos
  - `409 Conflict`: Email ya registrado

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


## Autor
- Luis José Villarreal Rincón

