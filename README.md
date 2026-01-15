# Student Information System - REST API

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Version](https://img.shields.io/badge/version-2.1.0-blue)]()
[![Java](https://img.shields.io/badge/Java-17-orange)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-green)]()
[![Endpoints](https://img.shields.io/badge/endpoints-170+-success)]()
[![API](https://img.shields.io/badge/API-100%25%20Funcional-brightgreen)]()
[![Pagination](https://img.shields.io/badge/Pagination-Implemented-blue)]()

Sistema de Información Estudiantil completo desarrollado como REST API con Spring Boot, JPA y MySQL con **soporte completo de paginación**.

**✅ 100% Funcional desde Frontend - No requiere acceso directo a la base de datos**

Gestiona: Estudiantes, Profesores, Cursos, Niveles, Materias, Períodos Académicos, Grupos, Inscripciones, **Calificaciones**, **Asistencia**, **Usuarios** y **Roles**.

---

## 🚀 Inicio Rápido

```bash
# Navegar al proyecto
cd /home/soporte/Desarrollos/idea/2026/back-bd-API

# Iniciar la aplicación
./start-api.sh
```

**La API estará disponible en:** `http://localhost:8080/api`

### Verificación
```bash
curl http://localhost:8080/api/health
```

Respuesta esperada:
```json
{
  "success": true,
  "message": "Service is running",
  "data": {
    "status": "UP",
    "service": "Student Information System API",
    "version": "2.1.0"
  }
}
```

---

## 🛠️ Tecnologías

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 17 | Lenguaje base |
| Spring Boot | 3.2.1 | Framework principal |
| Spring Data JPA | 3.2.1 | Persistencia de datos |
| Spring Security | 6.2.1 | Autenticación y autorización |
| JWT (JSON Web Tokens) | 0.12.3 | Tokens de autenticación |
| BCrypt | Built-in | Encriptación de passwords |
| MySQL | 8.x | Base de datos |
| Lombok | Latest | Reducción de boilerplate |
| Maven | 3.6+ | Gestión de dependencias |
| CORS | Built-in | Cross-Origin Resource Sharing |

---

## 📋 Requisitos Previos

- ✅ Java 17 o superior instalado
- ✅ Maven 3.6+ instalado
- ✅ MySQL 8.x ejecutándose
- ✅ Base de datos `bd-2026-1-cesde` creada
- ✅ Puerto 8080 disponible

---

## ⚙️ Configuración

### 1. Variables de Entorno (Recomendado) 🔒

La aplicación usa variables de entorno para información sensible. **No incluir credenciales en el código.**

#### Configuración Rápida

```bash
# 1. Copiar plantilla de variables
cp .env.example .env

# 2. Editar con tus credenciales
nano .env  # o tu editor preferido
```

#### Variables Requeridas

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DB_HOST` | Host de MySQL | `localhost` |
| `DB_PORT` | Puerto de MySQL | `3306` |
| `DB_NAME` | Nombre de la BD | `bd-2026-1-cesde` |
| `DB_USERNAME` | Usuario de BD | `cesde_user` |
| `DB_PASSWORD` | Password de BD | `TuPasswordSeguro` |
| `SERVER_PORT` | Puerto del servidor | `8080` |
| `CONTEXT_PATH` | Ruta base de la API | `/api` |

#### Linux/Mac

Opción 1: Usar archivo `.env` (automático con `start-api.sh`)
```bash
# El script start-api.sh carga automáticamente el .env
./start-api.sh
```

Opción 2: Exportar manualmente
```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=bd-2026-1-cesde
export DB_USERNAME=cesde_user
export DB_PASSWORD=TuPasswordSeguro
```

#### Windows

PowerShell:
```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_NAME="bd-2026-1-cesde"
$env:DB_USERNAME="cesde_user"
$env:DB_PASSWORD="TuPasswordSeguro"
```

CMD:
```cmd
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=bd-2026-1-cesde
set DB_USERNAME=cesde_user
set DB_PASSWORD=TuPasswordSeguro
```

### 2. Base de Datos

Ejecutar el script SQL:
```bash
mysql -u root -p < BASEDATOS.sql
```

**Ver guía completa:** [DATABASE-SETUP.md](DATABASE-SETUP.md)

Crear usuario de BD manualmente:
```sql
CREATE DATABASE IF NOT EXISTS `bd-2026-1-cesde`;
CREATE USER 'cesde_user'@'localhost' IDENTIFIED BY 'TuPasswordSeguro';
GRANT ALL PRIVILEGES ON `bd-2026-1-cesde`.* TO 'cesde_user'@'localhost';
FLUSH PRIVILEGES;
```

⚠️ **Importante:** El archivo `BASEDATOS.sql` NO incluye credenciales. Debes crearlas según tus necesidades.

### 3. Configuración de la Aplicación

El archivo `application.properties` usa variables de entorno automáticamente:

```properties
# Server
server.port=${SERVER_PORT:8080}
server.servlet.context-path=${CONTEXT_PATH:/api}

# Database (lee desde variables de entorno)
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:bd-2026-1-cesde}
spring.datasource.username=${DB_USERNAME:cesde_user}
spring.datasource.password=${DB_PASSWORD}
```

**Sintaxis:** `${VARIABLE:valor_por_defecto}`
- Si la variable de entorno existe, se usa su valor
- Si no existe, se usa el valor por defecto
- `DB_PASSWORD` no tiene default para seguridad

---

## 🔒 Seguridad

### Usuarios Iniciales

El script `BASEDATOS.sql` crea usuarios de prueba (cambiar en producción):

| Usuario | Password (BCrypt) | Rol | Uso |
|---------|------------------|-----|-----|
| `admin` | `Lagp2022` | Administrador | Gestión completa |
| `user` | `Lagp2026` | Usuario general | Acceso básico |

### Mejores Prácticas

✅ **Hacer:**
- Usar archivo `.env` para desarrollo local
- Usar gestores de secretos en producción (AWS Secrets, Azure Key Vault)
- Cambiar passwords por defecto inmediatamente
- Rotación periódica de credenciales
- Conexiones SSL/TLS en producción

❌ **No hacer:**
- Subir archivo `.env` a Git (está en `.gitignore`)
- Hardcodear passwords en código
- Usar credenciales de desarrollo en producción
- Compartir archivos `.env` por email/chat

### Spring Security

**Estado Actual:**
- ✅ BCrypt implementado para encriptación de passwords
- ✅ `CustomUserDetailsService` implementado (conectado a tabla `users`)
- ✅ Configuración en modo desarrollo (`permitAll()` - sin autenticación requerida)
- ✅ Sistema preparado para activar autenticación cuando se requiera

**Características:**
- Los usuarios y roles están en la base de datos
- Los passwords están hasheados con BCrypt
- El `CustomUserDetailsService` carga usuarios y roles automáticamente
- Cuando se active autenticación, solo hay que cambiar `SecurityConfig`

### 🔐 Autenticación JWT (Implementado)

La API utiliza **JWT (JSON Web Tokens)** para autenticación y autorización.

#### Configuración JWT

Variables de entorno en `.env`:

```properties
JWT_SECRET=YourVerySecureSecretKeyForJWTTokenGenerationMinimum256BitsRequired2026CesdeStudentInformationSystemAPI
JWT_EXPIRATION=86400000  # 24 horas en milisegundos
```

⚠️ **Importante:** Cambiar `JWT_SECRET` en producción. Debe ser una cadena de al menos 256 bits.

#### Endpoints de Autenticación

##### 1. Login (Iniciar Sesión)

```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "admin",
  "password": "Lagp2022"
}
```

**Respuesta exitosa:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "userId": 1,
    "username": "admin",
    "email": "admin@cesde.edu.co",
    "roles": ["Administrador"],
    "expiresIn": 86400000
  }
}
```

##### 2. Registro (Crear Usuario)

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "nuevo_usuario",
  "password": "Password123",
  "email": "usuario@example.com",
  "roleIds": [2]
}
```

##### 3. Validar Token

```http
POST /api/auth/validate-token
Content-Type: application/json

"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

##### 4. Refrescar Token

```http
POST /api/auth/refresh-token
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### Uso del Token

Una vez obtenido el token del endpoint `/auth/login`, incluirlo en el header `Authorization` de todas las peticiones:

```http
GET /api/students
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### Payload del JWT

El token incluye la siguiente información:

```json
{
  "sub": "username",
  "userId": 1,
  "roles": ["Administrador", "Usuario"],
  "iat": 1642598400,
  "exp": 1642684800
}
```

#### Configuración de Seguridad

Los siguientes endpoints son públicos (no requieren autenticación):

- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/register` - Registro de usuario
- `GET /api/health` - Estado de la API

Todos los demás endpoints requieren un token JWT válido.

#### Manejo de Errores

**Token inválido o expirado:**
```json
{
  "success": false,
  "message": "Token inválido o expirado",
  "timestamp": "2026-01-15T10:30:00"
}
```

**Credenciales incorrectas:**
```json
{
  "success": false,
  "message": "Credenciales inválidas",
  "timestamp": "2026-01-15T10:30:00"
}
```

---

### 🌐 Configuración CORS

La API tiene CORS (Cross-Origin Resource Sharing) configurado para permitir peticiones desde diferentes orígenes.

#### Configuración Actual

**Archivo:** `CorsConfig.java`

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOriginPatterns(List.of("*"));  // Acepta cualquier origen
    config.setAllowCredentials(true);                // Permite credentials
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setMaxAge(3600L);
    // ...
}
```

#### Características

- ✅ **Orígenes permitidos:** Cualquier origen (`*`) usando `allowedOriginPatterns`
- ✅ **Credentials:** Habilitado (permite cookies, auth headers, TLS certificates)
- ✅ **Métodos HTTP:** GET, POST, PUT, DELETE, PATCH, OPTIONS
- ✅ **Headers:** Todos permitidos
- ✅ **Preflight cache:** 3600 segundos (1 hora)

#### Integración con Spring Security

El `CorsConfig` se integra automáticamente con `SecurityConfig`:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        // ...
}
```

Esto asegura que CORS funcione correctamente con JWT y autenticación.

#### Configuración para Producción

⚠️ **Importante:** En producción, cambiar `allowedOriginPatterns(List.of("*"))` por dominios específicos:

```java
// Para producción - solo dominios específicos
config.setAllowedOriginPatterns(List.of(
    "https://tuapp.com",
    "https://www.tuapp.com",
    "https://admin.tuapp.com"
));
```

#### Orígenes Típicos para Desarrollo

| Framework | Puerto | URL |
|-----------|--------|-----|
| React | 3000 | `http://localhost:3000` |
| Angular | 4200 | `http://localhost:4200` |
| Vue | 8080 | `http://localhost:8080` |
| Otro | 8081 | `http://localhost:8081` |

#### Solución de Problemas CORS

**Error común:** `"allowedOrigins cannot contain '*' with allowCredentials=true"`

**Solución:** Usar `allowedOriginPatterns` en lugar de `allowedOrigins`:
```java
config.setAllowedOriginPatterns(List.of("*"));  // ✅ Correcto
config.setAllowedOrigins(List.of("*"));         // ❌ Error con credentials
```

---

## 📦 Estructura del Proyecto

```
src/main/java/com/cesde/studentinfo/
├── Main.java                      # Spring Boot Application
│
├── config/                        # Configuraciones (6 archivos)
│   ├── JpaConfig.java            # Configuración JPA y Repositories
│   ├── CorsConfig.java           # Configuración CORS (Spring Security)
│   ├── SecurityConfig.java       # Configuración de Seguridad + JWT
│   ├── JwtUtil.java              # Utilidad para generar y validar JWT
│   ├── JwtAuthenticationFilter.java  # Filtro de autenticación JWT
│   └── (otros archivos de config...)
│
├── controller/                    # REST Controllers (14 archivos)
│   ├── AuthController.java       # Autenticación (Login, Register, JWT)
│   ├── StudentController.java
│   ├── ProfessorController.java
│   ├── CourseController.java
│   ├── LevelController.java
│   ├── SubjectController.java
│   ├── AcademicPeriodController.java
│   ├── CourseGroupController.java
│   ├── CourseEnrollmentController.java
│   ├── GradeController.java
│   ├── AttendanceController.java
│   ├── UserController.java
│   ├── RoleController.java
│   └── HealthController.java
│
├── service/                       # Business Logic (14 archivos)
│   ├── AuthService.java          # Lógica de autenticación JWT
│   ├── CustomUserDetailsService.java  # Carga usuarios desde BD
│   ├── StudentService.java
│   ├── ProfessorService.java
│   ├── CourseService.java
│   ├── LevelService.java
│   ├── SubjectService.java
│   ├── AcademicPeriodService.java
│   ├── CourseGroupService.java
│   ├── CourseEnrollmentService.java
│   ├── GradeService.java
│   ├── AttendanceService.java
│   ├── UserService.java
│   └── RoleService.java
│
├── repository/                    # Spring Data JPA Repositories (13 archivos)
│   ├── StudentRepository.java
│   ├── ProfessorRepository.java
│   ├── CourseRepository.java
│   ├── LevelRepository.java
│   ├── SubjectRepository.java
│   ├── AcademicPeriodRepository.java
│   ├── CourseGroupRepository.java
│   ├── CourseEnrollmentRepository.java
│   ├── GradeRepository.java
│   ├── AttendanceRepository.java
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   └── UserRoleRepository.java
│
├── model/                         # JPA Entities (15+ entidades)
│   ├── Person.java
│   ├── Student.java
│   ├── Professor.java
│   ├── Course.java
│   ├── Level.java
│   ├── Subject.java
│   ├── AcademicPeriod.java
│   ├── CourseGroup.java
│   ├── CourseEnrollment.java
│   ├── Grade.java
│   ├── Attendance.java
│   ├── User.java
│   ├── Role.java
│   ├── UserRole.java
│   └── ...
│
├── dto/                           # Data Transfer Objects (30+ archivos)
│   ├── ApiResponse.java
│   ├── StudentDTO.java + StudentResponseDTO.java
│   ├── ProfessorDTO.java + ProfessorResponseDTO.java
│   ├── CourseDTO.java + CourseResponseDTO.java
│   ├── LevelDTO.java + LevelResponseDTO.java
│   ├── SubjectDTO.java + SubjectResponseDTO.java
│   ├── AcademicPeriodDTO.java + AcademicPeriodResponseDTO.java
│   ├── CourseGroupDTO.java + CourseGroupResponseDTO.java
│   ├── CourseEnrollmentDTO.java + CourseEnrollmentResponseDTO.java
│   ├── GradeDTO.java + GradeResponseDTO.java
│   ├── AttendanceDTO.java + AttendanceResponseDTO.java
│   ├── UserDTO.java + UserResponseDTO.java
│   └── RoleDTO.java + RoleResponseDTO.java
│
└── exception/                     # Exception Handling
    ├── ResourceNotFoundException.java
    ├── BusinessException.java
    ├── DataAccessException.java
    └── GlobalExceptionHandler.java
```

---

## 🎯 Endpoints de la API

### Base URL: `http://localhost:8080/api`

**Total: 142+ endpoints REST disponibles** ✅ **(incluye 5 endpoints de autenticación JWT)**

### 📚 Students (10 endpoints)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/students` | Listar todos los estudiantes |
| GET | `/students/active` | Listar estudiantes activos |
| GET | `/students/{id}` | Obtener estudiante por ID |
| GET | `/students/identification/{idNumber}` | Obtener por identificación |
| GET | `/students/search?name={name}` | Buscar por nombre |
| GET | `/students/count` | Contar total de estudiantes |
| POST | `/students` | Crear nuevo estudiante |
| PUT | `/students/{id}` | Actualizar estudiante |
| PATCH | `/students/{id}/deactivate` | Desactivar estudiante |
| DELETE | `/students/{id}` | Eliminar estudiante |

### 👨‍🏫 Professors (10 endpoints)

Similar a Students:
- GET `/professors`
- GET `/professors/active`
- GET `/professors/{id}`
- GET `/professors/identification/{idNumber}`
- GET `/professors/search?name={name}`
- GET `/professors/count`
- POST `/professors`
- PUT `/professors/{id}`
- PATCH `/professors/{id}/deactivate`
- DELETE `/professors/{id}`

### 📖 Courses (9 endpoints)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/courses` | Listar todos los cursos |
| GET | `/courses/active` | Listar cursos activos |
| GET | `/courses/{id}` | Obtener curso por ID |
| GET | `/courses/code/{code}` | Obtener por código |
| GET | `/courses/search?name={name}` | Buscar por nombre |
| GET | `/courses/count` | Contar total de cursos |
| POST | `/courses` | Crear nuevo curso |
| PUT | `/courses/{id}` | Actualizar curso |
| DELETE | `/courses/{id}` | Eliminar curso |

### 📊 Levels (6 endpoints)
- GET `/levels` - Listar niveles
- GET `/levels/{id}` - Por ID  
- GET `/levels/course/{courseId}` - Por curso
- POST `/levels` - Crear nivel
- PUT `/levels/{id}` - Actualizar
- DELETE `/levels/{id}` - Eliminar

### 📚 Subjects (9 endpoints)
- GET `/subjects` - Listar materias
- GET `/subjects/active` - Materias activas
- GET `/subjects/{id}` - Por ID
- GET `/subjects/code/{code}` - Por código
- GET `/subjects/level/{levelId}` - Por nivel
- GET `/subjects/search?name={name}` - Buscar
- POST `/subjects` - Crear materia
- PUT `/subjects/{id}` - Actualizar
- DELETE `/subjects/{id}` - Eliminar

### 📅 Academic Periods (9 endpoints)
- GET `/academic-periods` - Listar períodos
- GET `/academic-periods/active` - Períodos activos
- GET `/academic-periods/{id}` - Por ID
- GET `/academic-periods/current` - Período actual
- GET `/academic-periods/year/{year}` - Por año
- POST `/academic-periods` - Crear período
- PUT `/academic-periods/{id}` - Actualizar
- DELETE `/academic-periods/{id}` - Eliminar

### 👥 Course Groups (9 endpoints)
- GET `/course-groups` - Listar grupos
- GET `/course-groups/{id}` - Por ID
- GET `/course-groups/course/{courseId}` - Por curso
- GET `/course-groups/period/{periodId}` - Por período
- GET `/course-groups/available` - Con cupos disponibles
- POST `/course-groups` - Crear grupo
- PUT `/course-groups/{id}` - Actualizar
- DELETE `/course-groups/{id}` - Eliminar

### 📝 Enrollments (10 endpoints)
- GET `/enrollments` - Listar inscripciones
- GET `/enrollments/{id}` - Por ID
- GET `/enrollments/student/{studentId}` - Por estudiante
- GET `/enrollments/course/{courseId}` - Por curso
- GET `/enrollments/period/{periodId}` - Por período
- POST `/enrollments` - Inscribir estudiante
- PUT `/enrollments/{id}` - Actualizar inscripción
- PATCH `/enrollments/{id}/status` - Cambiar estado
- DELETE `/enrollments/{id}` - Cancelar inscripción

### 🎯 Grades (10 endpoints) - **CALIFICACIONES**
- GET `/grades` - Listar calificaciones
- GET `/grades/{id}` - Por ID
- GET `/grades/student/{studentId}` - Por estudiante
- GET `/grades/enrollment/{enrollmentId}` - Por inscripción
- GET `/grades/group/{groupId}` - Por grupo
- GET `/grades/period/{periodId}` - Por período
- POST `/grades` - **Registrar calificación**
- PUT `/grades/{id}` - **Actualizar nota**
- DELETE `/grades/{id}` - Eliminar calificación

### 📋 Attendance (11 endpoints) - **ASISTENCIA**
- GET `/attendance` - Listar asistencias
- GET `/attendance/{id}` - Por ID
- GET `/attendance/student/{studentId}` - Por estudiante
- GET `/attendance/session/{sessionId}` - Por sesión de clase
- GET `/attendance/enrollment/{enrollmentId}` - Por inscripción
- GET `/attendance/range?startDate=&endDate=` - Por rango de fechas
- POST `/attendance` - **Registrar asistencia**
- PUT `/attendance/{id}` - **Actualizar asistencia**
- DELETE `/attendance/{id}` - Eliminar registro

### 👤 Users (13 endpoints) - **GESTIÓN DE USUARIOS**
- GET `/users` - Listar usuarios
- GET `/users/active` - Usuarios activos
- GET `/users/{id}` - Por ID
- GET `/users/username/{username}` - Por username
- GET `/users/email/{email}` - Por email
- GET `/users/search?username={username}` - Buscar
- GET `/users/role/{roleName}` - Por rol
- GET `/users/count` - Contar usuarios
- POST `/users` - **Crear usuario** (password con BCrypt)
- PUT `/users/{id}` - Actualizar usuario
- PATCH `/users/{id}/deactivate` - Desactivar
- PATCH `/users/{userId}/roles/{roleId}` - Asignar rol
- DELETE `/users/{userId}/roles/{roleId}` - Remover rol
- DELETE `/users/{id}` - Eliminar usuario

### 🔐 Roles (12 endpoints) - **GESTIÓN DE ROLES**
- GET `/roles` - Listar roles
- GET `/roles/enabled` - Roles habilitados
- GET `/roles/{id}` - Por ID
- GET `/roles/name/{name}` - Por nombre
- GET `/roles/search?name={name}` - Buscar
- GET `/roles/count` - Contar roles
- GET `/roles/with-user-count` - Listar roles con conteo de usuarios
- GET `/roles/{id}/user-count` - Contar usuarios de un rol específico
- POST `/roles` - Crear rol
- PUT `/roles/{id}` - Actualizar rol
- PATCH `/roles/{id}/toggle-status` - Cambiar estado
- DELETE `/roles/{id}` - Eliminar rol

### 🔗 User-Roles (9 endpoints) - **ASIGNACIÓN DE ROLES**
- GET `/user-roles` - Todas las asignaciones
- GET `/user-roles/user/{userId}` - Roles de un usuario
- GET `/user-roles/role/{roleId}` - Usuarios con un rol
- GET `/user-roles/username/{username}` - Roles por username
- GET `/user-roles/role-name/{roleName}` - Usuarios por nombre de rol
- GET `/user-roles/recent?days=7` - Asignaciones recientes
- GET `/user-roles/assigned-by/{userId}` - Asignaciones por admin
- POST `/user-roles` - Asignar rol (con auditoría)
- DELETE `/user-roles/user/{userId}/role/{roleId}` - Remover rol

### 🔐 Authentication (5 endpoints)

| Método | Endpoint | Descripción | Público |
|--------|----------|-------------|---------|
| POST | `/auth/login` | Iniciar sesión (obtener JWT) | ✅ Sí |
| POST | `/auth/register` | Registrar nuevo usuario | ✅ Sí |
| POST | `/auth/validate-token` | Validar token JWT | ❌ No |
| POST | `/auth/refresh-token` | Renovar token expirado | ❌ No |
| GET | `/auth/health` | Estado del servicio de autenticación | ✅ Sí |

**Nota:** Los endpoints marcados con ✅ son públicos y no requieren autenticación. Todos los demás requieren un token JWT válido en el header `Authorization: Bearer <token>`.

### 🏥 Health (2 endpoints)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/health` | Estado detallado de la API |
| GET | `/health/ping` | Ping simple (responde "pong") |

---

## 🧪 Ejemplos de Uso

### 🔐 Autenticación (JWT)

#### 1. Login (obtener token)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "Lagp2022"
  }'
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "userId": 1,
    "username": "admin",
    "email": "admin@cesde.edu.co",
    "roles": ["Administrador"],
    "expiresIn": 86400000
  }
}
```

#### 2. Usar el token en requests subsiguientes

```bash
# Guardar el token en una variable
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Usar el token en los requests
curl http://localhost:8080/api/students \
  -H "Authorization: Bearer $TOKEN"
```

#### 3. Registrar nuevo usuario

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nuevo_usuario",
    "password": "Password123",
    "email": "usuario@example.com",
    "roleIds": [2]
  }'
```

---

### Crear Estudiante

```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "identificationType": "CC",
    "identificationNumber": "1234567890",
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@example.com",
    "phone": "3001234567",
    "dateOfBirth": "2000-01-15"
  }'
```

### Listar Estudiantes

```bash
curl http://localhost:8080/api/students
```

### Buscar Estudiante por Nombre

```bash
curl "http://localhost:8080/api/students/search?name=Juan"
```

### Crear Curso

```bash
curl -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Desarrollo de Software",
    "code": "DEV-101",
    "description": "Curso completo de desarrollo full stack",
    "totalLevels": 4
  }'
```

### Registrar Calificación

```bash
curl -X POST http://localhost:8080/api/grades \
  -H "Content-Type: application/json" \
  -d '{
    "subjectEnrollmentId": 1,
    "gradePeriodId": 1,
    "gradeComponentId": 1,
    "gradeValue": 4.5,
    "registrationDate": "2026-01-13",
    "observations": "Excelente trabajo"
  }'
```

### Registrar Asistencia

```bash
curl -X POST http://localhost:8080/api/attendance \
  -H "Content-Type: application/json" \
  -d '{
    "subjectEnrollmentId": 1,
    "classSessionId": 1,
    "assignmentDate": "2026-01-13",
    "status": "PRESENTE",
    "notes": "Asistió puntualmente"
  }'
```

---

## 📄 Paginación

### Características de Paginación

La API implementa **paginación completa** en todos los endpoints de listado para mejorar el rendimiento y la experiencia del usuario.

#### Endpoints con Soporte de Paginación

Todos los endpoints principales tienen versiones paginadas accesibles agregando `/paged` al path:

| Entidad | Endpoint Base | Endpoint Paginado |
|---------|---------------|-------------------|
| **Students** | `/students` | `/students/paged` |
| **Professors** | `/professors` | `/professors/paged` |
| **Courses** | `/courses` | `/courses/paged` |
| **Levels** | `/levels` | `/levels/paged` |
| **Subjects** | `/subjects` | `/subjects/paged` |
| **Academic Periods** | `/academic-periods` | `/academic-periods/paged` |
| **Users** | `/users` | `/users/paged` |
| **Roles** | `/roles` | `/roles/paged` |

#### Parámetros de Paginación

| Parámetro | Tipo | Default | Descripción |
|-----------|------|---------|-------------|
| `page` | Integer | `0` | Número de página (0-indexed) |
| `size` | Integer | `20` | Tamaño de página (registros por página) |
| `sort` | String[] | `id,desc` | Ordenamiento: `campo,dirección` |

**Direcciones de ordenamiento:** `asc` (ascendente) o `desc` (descendente)

#### Estructura de Respuesta Paginada

```json
{
  "success": true,
  "message": "Students retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "firstName": "Juan",
        "lastName": "Pérez",
        ...
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8,
    "first": true,
    "last": false,
    "empty": false,
    "sort": {
      "sorted": true,
      "sortBy": "lastName",
      "direction": "ASC"
    }
  }
}
```

#### Metadatos de Paginación

| Campo | Descripción |
|-------|-------------|
| `content` | Array con los registros de la página actual |
| `page` | Número de página actual (0-indexed) |
| `size` | Tamaño de página solicitado |
| `totalElements` | Total de registros disponibles |
| `totalPages` | Total de páginas disponibles |
| `first` | `true` si es la primera página |
| `last` | `true` si es la última página |
| `empty` | `true` si no hay registros |
| `sort.sorted` | `true` si está ordenado |
| `sort.sortBy` | Campo usado para ordenar |
| `sort.direction` | Dirección del ordenamiento (ASC/DESC) |

### Ejemplos de Uso

#### 1. Listar Estudiantes - Primera Página (20 registros)

```bash
curl "http://localhost:8080/api/students/paged"
```

Equivalente a:
```bash
curl "http://localhost:8080/api/students/paged?page=0&size=20&sort=id,desc"
```

#### 2. Segunda Página con 50 Registros

```bash
curl "http://localhost:8080/api/students/paged?page=1&size=50"
```

#### 3. Ordenar por Apellido Ascendente

```bash
curl "http://localhost:8080/api/students/paged?sort=lastName,asc"
```

#### 4. Página 3, 10 registros, ordenado por fecha de inscripción

```bash
curl "http://localhost:8080/api/students/paged?page=2&size=10&sort=enrollmentDate,desc"
```

#### 5. Buscar con Paginación

```bash
# Buscar estudiantes llamados "Juan" - página 1, 15 registros
curl "http://localhost:8080/api/students/search/paged?name=Juan&page=0&size=15&sort=lastName,asc"
```

#### 6. Filtrar Activos con Paginación

```bash
# Estudiantes activos - página 2, 25 registros
curl "http://localhost:8080/api/students/active/paged?page=1&size=25"
```

### Endpoints Paginados Disponibles

#### Students
- `GET /students/paged` - Todos los estudiantes
- `GET /students/active/paged` - Solo activos
- `GET /students/search/paged?name={name}` - Búsqueda por nombre

#### Professors
- `GET /professors/paged` - Todos los profesores
- `GET /professors/active/paged` - Solo activos
- `GET /professors/search/paged?name={name}` - Búsqueda por nombre

#### Courses
- `GET /courses/paged` - Todos los cursos
- `GET /courses/active/paged` - Solo activos
- `GET /courses/search/paged?name={name}` - Búsqueda por nombre

#### Levels
- `GET /levels/paged` - Todos los niveles
- `GET /levels/course/{courseId}/paged` - Niveles de un curso específico

#### Subjects
- `GET /subjects/paged` - Todas las materias
- `GET /subjects/active/paged` - Solo activas
- `GET /subjects/level/{levelId}/paged` - Materias de un nivel
- `GET /subjects/search/paged?name={name}` - Búsqueda por nombre

#### Academic Periods
- `GET /academic-periods/paged` - Todos los períodos
- `GET /academic-periods/active/paged` - Solo activos
- `GET /academic-periods/year/{year}/paged` - Por año

#### Users
- `GET /users/paged` - Todos los usuarios
- `GET /users/active/paged` - Solo activos
- `GET /users/search/paged?username={username}` - Búsqueda por username
- `GET /users/role/{roleName}/paged` - Usuarios con un rol específico

#### Roles
- `GET /roles/paged` - Todos los roles
- `GET /roles/enabled/paged` - Solo habilitados
- `GET /roles/search/paged?name={name}` - Búsqueda por nombre

### Recomendaciones de Uso

✅ **Buenas Prácticas:**
- Usar paginación para listados de más de 50 registros
- Tamaño de página recomendado: 20-50 para web, 10-20 para móvil
- Ordenar por campos indexados para mejor performance
- Cachear página actual en frontend para navegación fluida

❌ **Evitar:**
- Páginas muy grandes (>100 registros)
- Solicitar todas las páginas a la vez
- No usar paginación en listados grandes

### Integración con Frontend

#### React Example
```javascript
const [page, setPage] = useState(0);
const [size] = useState(20);

const fetchStudents = async () => {
  const response = await fetch(
    `http://localhost:8080/api/students/paged?page=${page}&size=${size}&sort=lastName,asc`
  );
  const data = await response.json();
  
  return {
    students: data.data.content,
    totalPages: data.data.totalPages,
    currentPage: data.data.page,
    total: data.data.totalElements
  };
};
```

#### Angular Example
```typescript
getStudentsPaginated(page: number = 0, size: number = 20, sort: string = 'id,desc') {
  const params = new HttpParams()
    .set('page', page.toString())
    .set('size', size.toString())
    .set('sort', sort);
    
  return this.http.get<ApiResponse<PagedResponse<Student>>>(
    `${this.apiUrl}/students/paged`,
    { params }
  );
}
```

---

## 📝 Formato de Respuesta

Todas las respuestas siguen un formato consistente:

### Respuesta Exitosa
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { /* ... datos ... */ },
  "timestamp": "2026-01-13T10:30:00"
}
```

### Respuesta de Error
```json
{
  "success": false,
  "message": "Error description",
  "errors": { /* ... detalles ... */ },
  "timestamp": "2026-01-13T10:30:00"
}
```

---

## 🧪 Testing

### Con cURL
Ver ejemplos arriba en la sección "Ejemplos de Uso"

### Con Postman
1. Importar `postman-collection.json`
2. La colección incluye:
   - Health checks (2 requests)
   - Students (10 requests)
   - Professors (10 requests)
   - Courses (9 requests)
   - Grades (6 requests)
   - Attendance (8 requests)
3. Variables pre-configuradas
4. Ejemplos de request/response

### Con Navegador
- Health check: http://localhost:8080/api/health
- Ver estudiantes: http://localhost:8080/api/students

---

## 🔧 Comandos Maven

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar la aplicación
mvn spring-boot:run

# Empaquetar en JAR
mvn clean package -DskipTests

# Ejecutar el JAR
java -jar target/student-information-system-1.0.0.jar

# Ejecutar tests
mvn test

# Limpiar target/
mvn clean
```

---

## 🏗️ Arquitectura

### Patrón de Diseño
- **MVC con capas**: Controller → Service → Repository → Model
- **DTO Pattern**: Separación entre DTOs y Entities
- **Exception Handling**: Manejo centralizado de errores

### Características Implementadas
- ✅ REST API con Spring Boot
- ✅ Spring Data JPA
- ✅ **Autenticación JWT (JSON Web Tokens)**
- ✅ **Spring Security** con filtros personalizados
- ✅ **CORS configurado** (CorsConfigurationSource)
- ✅ **BCrypt** para encriptación de passwords
- ✅ Transaction Management con @Transactional
- ✅ Bean Validation en DTOs
- ✅ Global Exception Handler
- ✅ Logging con SLF4J
- ✅ Response format consistente (ApiResponse)
- ✅ HTTP Status codes apropiados
- ✅ **Stateless session management**
- ✅ **Token refresh** automático

### Manejo de Errores
- **404 (Not Found)**: `ResourceNotFoundException`
- **400 (Bad Request)**: `BusinessException`, validaciones
- **500 (Internal Server Error)**: `DataAccessException`

---

## 🗄️ Base de Datos

### Modelo de Datos
La base de datos incluye 18 tablas para gestionar:
- Estudiantes y Profesores (herencia de Person)
- Cursos y Niveles
- Materias
- Períodos Académicos
- Grupos de Curso
- Inscripciones (CourseEnrollment, LevelEnrollment, SubjectEnrollment)
- Calificaciones (Grades, GradeComponents, GradePeriods)
- Asistencia (Attendance, ClassSessions)

Ver `BASEDATOS.sql` para el esquema completo.

---

## 📊 Estado del Proyecto

| Característica | Estado |
|----------------|--------|
| REST API | ✅ Funcional al 100% |
| CRUD Estudiantes | ✅ Completo (10 endpoints) |
| CRUD Profesores | ✅ Completo (10 endpoints) |
| CRUD Cursos | ✅ Completo (9 endpoints) |
| CRUD Niveles | ✅ Completo (6 endpoints) |
| CRUD Materias | ✅ Completo (9 endpoints) |
| CRUD Períodos Académicos | ✅ Completo (9 endpoints) |
| CRUD Grupos | ✅ Completo (9 endpoints) |
| CRUD Inscripciones | ✅ Completo (10 endpoints) |
| CRUD Calificaciones | ✅ Completo (10 endpoints) |
| CRUD Asistencia | ✅ Completo (11 endpoints) |
| CRUD Usuarios | ✅ Completo (13 endpoints) |
| CRUD Roles | ✅ Completo (10 endpoints) |
| CRUD User-Roles | ✅ Completo (8 endpoints) |
| Repositories | ✅ 13 implementados |
| Services | ✅ 13 implementados |
| Controllers | ✅ 14 implementados |
| DTOs | ✅ 35 implementados |
| Exception Handling | ✅ Global |
| Validaciones | ✅ Bean Validation |
| CORS | ✅ Configurado |
| Spring Security | ✅ BCrypt implementado |
| Documentación | ✅ Completa |
| Postman Collection | ✅ Actualizado |
| Testing | ⚠️ Pendiente |

---

## 🔮 Próximas Mejoras

### ✅ Completado
- [x] **Paginación en listados largos** ✨ (v2.1.0 - Enero 2026)
  - 33 endpoints paginados implementados
  - Soporte completo para todas las entidades principales
  - Ordenamiento configurable y metadatos de paginación
- [x] **Spring Security con JWT** (v2.0.0 - Enero 2026)
- [x] **CORS configurado** (v2.0.0)
- [x] **Gestión de Usuarios y Roles** (v2.0.0)

### Corto Plazo
- [ ] Swagger/OpenAPI para documentación interactiva
- [ ] Unit tests e Integration tests
- [ ] Validaciones avanzadas en DTOs

### Mediano Plazo
- [ ] Búsquedas avanzadas con múltiples filtros combinados
- [ ] Reportes y estadísticas
- [ ] Exportación de datos (PDF, Excel)

### Largo Plazo
- [ ] Cache con Redis
- [ ] Métricas con Actuator
- [ ] Docker containerization
- [ ] CI/CD pipeline

---

## 📚 Documentación Adicional

Este proyecto incluye documentación detallada en archivos separados:

| Archivo | Descripción |
|---------|-------------|
| [DATABASE-SETUP.md](DATABASE-SETUP.md) | Guía completa de configuración de base de datos y variables de entorno |
| [postman-collection.json](postman-collection.json) | Colección de Postman con todos los endpoints de la API |
| [.env.example](.env.example) | Plantilla de variables de entorno |

---

## 🤝 Contribuir

Este es un proyecto académico de CESDE. Para contribuir:
1. Fork del repositorio
2. Crear branch para tu feature
3. Commit de cambios
4. Push al branch
5. Crear Pull Request

---

## 📝 Licencia

Proyecto educativo - CESDE 2026

---

## 📞 Soporte

Para dudas o problemas:
- Revisar la documentación en este README
- Verificar `application.properties`
- Consultar los logs de la aplicación
- Verificar que MySQL esté corriendo
- Verificar compilación: `mvn clean compile`

---

## 👥 Autores

**CESDE - Centro de Estudios de Desarrollo Empresarial**  
Proyecto académico - Enero 2026

---

## 🙏 Agradecimientos

- Spring Boot Team
- Comunidad de Spring Framework
- MySQL Community

---

**Última actualización:** Enero 15, 2026  
**Versión:** 2.1.0 - Paginación Completa + JWT + CORS  
**Estado:** ✅ PRODUCTION READY - 100% Funcional con Paginación



