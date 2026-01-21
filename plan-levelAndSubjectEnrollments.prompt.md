# Plan: Implementación de Controladores LevelEnrollment y SubjectEnrollment

## Prompt
El backend tiene las tablas y modelos para LevelEnrollment y SubjectEnrollment, 
pero NO tiene los controladores ni endpoints.

Este es un diagnóstico desde el front, para poder realizar una inscripción completa.
Revisar si esto es correcto: De ser correcto, se deben implementar y actualizar tanto el readme, 
como el postman-collection, no se necesita crear documentación de las modificaciones, 
solo actualizar como quedo los archivos existentes.

## Diagnóstico

El diagnóstico del frontend es **CORRECTO**. Existen los modelos `LevelEnrollment` y `SubjectEnrollment` en la base de datos y código, pero **NO tienen** controladores, servicios, repositorios ni DTOs para exponerlos como endpoints REST. Esto impide realizar inscripciones completas desde el frontend.

### Estado Actual

**Modelos existentes:**
- ✅ `CourseEnrollment` - Tiene controlador `CourseEnrollmentController`
- ❌ `LevelEnrollment` - **NO tiene controlador**
- ❌ `SubjectEnrollment` - **NO tiene controlador**

**Componentes faltantes:**
- Repositorios: `LevelEnrollmentRepository`, `SubjectEnrollmentRepository`
- DTOs: Request y Response para ambas entidades
- Services: `LevelEnrollmentService`, `SubjectEnrollmentService`
- Controllers: `LevelEnrollmentController`, `SubjectEnrollmentController`

## Steps

### 1. Crear Repositorios

Implementar `LevelEnrollmentRepository` y `SubjectEnrollmentRepository` en `repository/` con métodos de consulta necesarios:

**LevelEnrollmentRepository:**
- `findById(Long id)`
- `findByCourseEnrollmentId(Long courseEnrollmentId)`
- `findByLevelId(Long levelId)`
- `findByAcademicPeriodId(Long academicPeriodId)`
- `findByGroupId(Long groupId)`
- `findByStatus(LevelStatus status)`
- `existsByCourseEnrollmentIdAndLevelIdAndAcademicPeriodId(...)`

**SubjectEnrollmentRepository:**
- `findById(Long id)`
- `findByLevelEnrollmentId(Long levelEnrollmentId)`
- `findBySubjectAssignmentId(Long subjectAssignmentId)`
- `findByStatus(SubjectStatus status)`
- `existsByLevelEnrollmentIdAndSubjectAssignmentId(...)`

### 2. Crear DTOs Request y Response

Implementar en `dto/` siguiendo el patrón existente de `CourseEnrollmentDTO`/`CourseEnrollmentResponseDTO`:

**LevelEnrollmentDTO (Request):**
```java
- courseEnrollmentId: Long (required)
- levelId: Long (required)
- academicPeriodId: Long (required)
- groupId: Long (optional)
- enrollmentDate: LocalDate
- status: LevelStatus
```

**LevelEnrollmentResponseDTO (Response):**
```java
- id: Long
- courseEnrollmentId: Long
- studentName: String
- levelId: Long
- levelName: String
- academicPeriodId: Long
- academicPeriodName: String
- groupId: Long
- groupName: String
- enrollmentDate: LocalDate
- status: LevelStatus
- finalAverage: BigDecimal
- completionDate: LocalDate
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
- static fromEntity(LevelEnrollment): LevelEnrollmentResponseDTO
```

**SubjectEnrollmentDTO (Request):**
```java
- levelEnrollmentId: Long (required)
- subjectAssignmentId: Long (required)
- enrollmentDate: LocalDate
- status: SubjectStatus
```

**SubjectEnrollmentResponseDTO (Response):**
```java
- id: Long
- levelEnrollmentId: Long
- studentName: String
- subjectAssignmentId: Long
- subjectName: String
- professorName: String
- enrollmentDate: LocalDate
- status: SubjectStatus
- finalGrade: BigDecimal
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
- static fromEntity(SubjectEnrollment): SubjectEnrollmentResponseDTO
```

### 3. Crear Services con lógica de negocio

Implementar `LevelEnrollmentService` y `SubjectEnrollmentService` en `service/` siguiendo patrón de `CourseEnrollmentService`:

**LevelEnrollmentService:**
- `getAllLevelEnrollments(): List<LevelEnrollment>`
- `getLevelEnrollmentById(Long id): Optional<LevelEnrollment>`
- `getLevelEnrollmentsByCourseEnrollmentId(Long id): List<LevelEnrollment>`
- `getLevelEnrollmentsByLevelId(Long id): List<LevelEnrollment>`
- `getLevelEnrollmentsByAcademicPeriodId(Long id): List<LevelEnrollment>`
- `createLevelEnrollment(LevelEnrollment): LevelEnrollment`
- `updateLevelEnrollment(Long id, LevelEnrollment): LevelEnrollment`
- `updateLevelEnrollmentStatus(Long id, LevelStatus): LevelEnrollment`
- `deleteLevelEnrollment(Long id): void`
- `countLevelEnrollments(): long`

**Validaciones LevelEnrollmentService:**
- Validar que CourseEnrollment existe y está activo
- Validar que Level existe y está activo
- Validar que AcademicPeriod existe y está activo
- Validar que Group existe (si se proporciona)
- Evitar duplicados: misma inscripción de curso + nivel + período académico
- Establecer fecha de inscripción si no se proporciona

**SubjectEnrollmentService:**
- `getAllSubjectEnrollments(): List<SubjectEnrollment>`
- `getSubjectEnrollmentById(Long id): Optional<SubjectEnrollment>`
- `getSubjectEnrollmentsByLevelEnrollmentId(Long id): List<SubjectEnrollment>`
- `getSubjectEnrollmentsBySubjectAssignmentId(Long id): List<SubjectEnrollment>`
- `createSubjectEnrollment(SubjectEnrollment): SubjectEnrollment`
- `updateSubjectEnrollment(Long id, SubjectEnrollment): SubjectEnrollment`
- `updateSubjectEnrollmentStatus(Long id, SubjectStatus): SubjectEnrollment`
- `deleteSubjectEnrollment(Long id): void`
- `countSubjectEnrollments(): long`

**Validaciones SubjectEnrollmentService:**
- Validar que LevelEnrollment existe y está activo
- Validar que SubjectAssignment existe
- Evitar duplicados: misma inscripción de nivel + asignación de materia
- Validar que la materia pertenece al nivel correcto
- Establecer fecha de inscripción si no se proporciona

### 4. Crear Controllers REST

Implementar `LevelEnrollmentController` y `SubjectEnrollmentController` en `controller/` con endpoints CRUD completos:

**LevelEnrollmentController** (`/level-enrollments`):
- `GET /level-enrollments` - Listar todas las inscripciones de nivel
- `GET /level-enrollments/{id}` - Obtener inscripción por ID
- `GET /level-enrollments/course-enrollment/{id}` - Por inscripción de curso
- `GET /level-enrollments/level/{id}` - Por nivel
- `GET /level-enrollments/period/{id}` - Por período académico
- `GET /level-enrollments/group/{id}` - Por grupo
- `POST /level-enrollments` - Crear inscripción de nivel
- `PUT /level-enrollments/{id}` - Actualizar inscripción
- `PATCH /level-enrollments/{id}/status` - Actualizar estado
- `DELETE /level-enrollments/{id}` - Eliminar inscripción
- `GET /level-enrollments/count` - Contar inscripciones

**SubjectEnrollmentController** (`/subject-enrollments`):
- `GET /subject-enrollments` - Listar todas las inscripciones de materia
- `GET /subject-enrollments/{id}` - Obtener inscripción por ID
- `GET /subject-enrollments/level-enrollment/{id}` - Por inscripción de nivel
- `GET /subject-enrollments/subject-assignment/{id}` - Por asignación de materia
- `POST /subject-enrollments` - Crear inscripción de materia
- `PUT /subject-enrollments/{id}` - Actualizar inscripción
- `PATCH /subject-enrollments/{id}/status` - Actualizar estado
- `DELETE /subject-enrollments/{id}` - Eliminar inscripción
- `GET /subject-enrollments/count` - Contar inscripciones

### 5. Actualizar README.md

Agregar sección de endpoints de LevelEnrollment y SubjectEnrollment en la documentación de API, siguiendo formato existente:

```markdown
### Level Enrollments
- `GET /api/level-enrollments` - Listar todas las inscripciones de nivel
- `GET /api/level-enrollments/{id}` - Obtener inscripción por ID
- `GET /api/level-enrollments/course-enrollment/{id}` - Inscripciones por curso
- `GET /api/level-enrollments/level/{id}` - Inscripciones por nivel
- `POST /api/level-enrollments` - Crear inscripción de nivel
- `PUT /api/level-enrollments/{id}` - Actualizar inscripción
- `PATCH /api/level-enrollments/{id}/status` - Actualizar estado
- `DELETE /api/level-enrollments/{id}` - Eliminar inscripción

### Subject Enrollments
- `GET /api/subject-enrollments` - Listar todas las inscripciones de materia
- `GET /api/subject-enrollments/{id}` - Obtener inscripción por ID
- `GET /api/subject-enrollments/level-enrollment/{id}` - Inscripciones por nivel
- `GET /api/subject-enrollments/subject-assignment/{id}` - Inscripciones por materia
- `POST /api/subject-enrollments` - Crear inscripción de materia
- `PUT /api/subject-enrollments/{id}` - Actualizar inscripción
- `PATCH /api/subject-enrollments/{id}/status` - Actualizar estado
- `DELETE /api/subject-enrollments/{id}` - Eliminar inscripción
```

### 6. Actualizar postman-collection.json

Agregar carpetas "Level Enrollments" y "Subject Enrollments" con requests de ejemplo para todos los endpoints nuevos:

**Carpeta Level Enrollments:**
- Get All Level Enrollments (GET)
- Get Level Enrollment by ID (GET)
- Get Level Enrollments by Course Enrollment (GET)
- Get Level Enrollments by Level (GET)
- Get Level Enrollments by Period (GET)
- Create Level Enrollment (POST) - con ejemplo de body
- Update Level Enrollment (PUT) - con ejemplo de body
- Update Level Enrollment Status (PATCH)
- Delete Level Enrollment (DELETE)
- Count Level Enrollments (GET)

**Carpeta Subject Enrollments:**
- Get All Subject Enrollments (GET)
- Get Subject Enrollment by ID (GET)
- Get Subject Enrollments by Level Enrollment (GET)
- Get Subject Enrollments by Subject Assignment (GET)
- Create Subject Enrollment (POST) - con ejemplo de body
- Update Subject Enrollment (PUT) - con ejemplo de body
- Update Subject Enrollment Status (PATCH)
- Delete Subject Enrollment (DELETE)
- Count Subject Enrollments (GET)

## Further Considerations

### 1. Orden de creación (Jerarquía de inscripciones)

Las inscripciones deben seguir esta jerarquía:
1. **CourseEnrollment** (Estudiante se inscribe en un curso)
2. **LevelEnrollment** (Estudiante se inscribe en un nivel específico del curso)
3. **SubjectEnrollment** (Estudiante se inscribe en materias específicas del nivel)

**Validaciones críticas:**
- No se puede crear LevelEnrollment sin CourseEnrollment activo
- No se puede crear SubjectEnrollment sin LevelEnrollment activo
- La materia (SubjectAssignment) debe pertenecer al nivel correcto

### 2. Paginación

**Pregunta:** ¿Implementar paginación en los endpoints GET desde el inicio o dejarlos sin paginación como están actualmente los demás endpoints?

**Recomendación:** Implementar paginación desde el inicio para evitar problemas con grandes volúmenes de datos. Seguir el patrón ya implementado en otros controladores que tienen paginación.

### 3. Testing

No se incluyen tests en el plan actual, pero se recomienda agregarlos posteriormente para validar:
- Lógica de negocio en Services
- Endpoints en Controllers
- Validaciones de duplicados
- Manejo de excepciones

### 4. Transaccionalidad

Considerar implementar un servicio de orquestación que maneje la creación completa de inscripciones (Course → Level → Subjects) en una sola transacción, facilitando el uso desde el frontend.

### 5. Lazy Loading

Verificar que los DTOs manejen correctamente las relaciones lazy (usar `@Transactional(readOnly = true)` en los métodos de consulta y cargar relaciones necesarias con fetch joins si es necesario).

### 6. Seguridad

Los endpoints deben estar protegidos por JWT. Considerar roles:
- **Administrador/Administrativo:** CRUD completo
- **Profesor:** Solo lectura de inscripciones relacionadas con sus materias
- **Estudiante:** Solo lectura de sus propias inscripciones

## Archivos a Crear

### Repositories
- `src/main/java/com/cesde/studentinfo/repository/LevelEnrollmentRepository.java`
- `src/main/java/com/cesde/studentinfo/repository/SubjectEnrollmentRepository.java`

### DTOs
- `src/main/java/com/cesde/studentinfo/dto/LevelEnrollmentDTO.java`
- `src/main/java/com/cesde/studentinfo/dto/LevelEnrollmentResponseDTO.java`
- `src/main/java/com/cesde/studentinfo/dto/SubjectEnrollmentDTO.java`
- `src/main/java/com/cesde/studentinfo/dto/SubjectEnrollmentResponseDTO.java`

### Services
- `src/main/java/com/cesde/studentinfo/service/LevelEnrollmentService.java`
- `src/main/java/com/cesde/studentinfo/service/SubjectEnrollmentService.java`

### Controllers
- `src/main/java/com/cesde/studentinfo/controller/LevelEnrollmentController.java`
- `src/main/java/com/cesde/studentinfo/controller/SubjectEnrollmentController.java`

## Archivos a Actualizar

- `README.md` - Agregar documentación de nuevos endpoints
- `postman-collection.json` - Agregar carpetas con requests de ejemplo

## Resultado Esperado

Al finalizar la implementación, el frontend podrá:
1. Inscribir un estudiante en un curso (CourseEnrollment) ✅ Ya existe
2. Inscribir al estudiante en niveles específicos del curso (LevelEnrollment) ✅ Nuevo
3. Inscribir al estudiante en materias de cada nivel (SubjectEnrollment) ✅ Nuevo
4. Consultar, actualizar y eliminar todas las inscripciones
5. Realizar el flujo completo de inscripción académica

Esto completará el CRUD necesario para gestionar el ciclo completo de inscripciones académicas desde el frontend.
