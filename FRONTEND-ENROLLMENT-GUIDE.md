# Guía de Implementación: Sistema de Inscripciones Jerárquicas

**Fecha:** Enero 20, 2026  
**Versión API:** 2.4.0  
**Audiencia:** Desarrolladores Frontend

---

## 📋 Tabla de Contenidos

1. [Resumen General](#resumen-general)
2. [Jerarquía de Inscripciones](#jerarquía-de-inscripciones)
3. [Endpoints Disponibles](#endpoints-disponibles)
4. [Flujo de Implementación](#flujo-de-implementación)
5. [Ejemplos de Código](#ejemplos-de-código)
6. [Validaciones y Reglas de Negocio](#validaciones-y-reglas-de-negocio)
7. [Manejo de Errores](#manejo-de-errores)
8. [Estados y Enums](#estados-y-enums)

---

## 🎯 Resumen General

El sistema de inscripciones sigue una **jerarquía de 3 niveles** que refleja el proceso real de matrícula académica:

```
CourseEnrollment (Inscripción al Curso)
    └── LevelEnrollment (Inscripción al Nivel)
        └── SubjectEnrollment (Inscripción a Materias)
```

### ✅ Implementado en v2.4.0

- **LevelEnrollment:** 12 endpoints (10 funcionales + 2 paginados)
- **SubjectEnrollment:** 10 endpoints (8 funcionales + 2 paginados)
- **Validaciones automáticas** de jerarquía
- **Paginación completa** en todos los listados
- **Autenticación JWT** requerida

---

## 🏗️ Jerarquía de Inscripciones

### 1️⃣ CourseEnrollment (Inscripción al Curso)

**¿Qué es?** Inscribe a un estudiante en un curso completo (ej: "Desarrollo de Software").

**Campos principales:**
- `studentId`: ID del estudiante
- `courseId`: ID del curso
- `academicPeriodId`: ID del período académico
- `enrollmentStatus`: Estado (ACTIVO, EGRESADO, RETIRADO, INACTIVO)

**Endpoint:** `/api/course-enrollments`

---

### 2️⃣ LevelEnrollment (Inscripción al Nivel) ⭐ NUEVO

**¿Qué es?** Inscribe al estudiante en un nivel específico del curso (ej: "Nivel 1 de Desarrollo de Software").

**Campos principales:**
- `courseEnrollmentId`: ✅ **Requerido** - Debe existir y estar ACTIVO
- `levelId`: ✅ **Requerido** - El nivel debe pertenecer al curso
- `academicPeriodId`: ✅ **Requerido** - Debe estar activo
- `groupId`: ⚪ Opcional - Grupo específico del nivel
- `status`: EN_CURSO, APROBADO, REPROBADO, RETIRADO

**Endpoint:** `/api/level-enrollments`

**Validaciones automáticas:**
- ✅ Verifica que CourseEnrollment existe y está ACTIVO
- ✅ Verifica que el período académico está activo
- ✅ Si no se envía `enrollmentDate`, se establece la fecha actual
- ✅ Si no se envía `status`, se establece EN_CURSO

---

### 3️⃣ SubjectEnrollment (Inscripción a Materia) ⭐ NUEVO

**¿Qué es?** Inscribe al estudiante en materias específicas del nivel (ej: "Matemáticas I", "Programación I").

**Campos principales:**
- `levelEnrollmentId`: ✅ **Requerido** - Debe existir y estar EN_CURSO
- `subjectAssignmentId`: ✅ **Requerido** - Asignación de profesor a materia
- `status`: EN_CURSO, APROBADO, REPROBADO, RETIRADO

**Endpoint:** `/api/subject-enrollments`

**Validaciones automáticas:**
- ✅ Verifica que LevelEnrollment existe y está EN_CURSO
- ✅ **Validación cruzada:** La materia debe pertenecer al nivel correcto
- ✅ Si no se envía `enrollmentDate`, se establece la fecha actual
- ✅ Si no se envía `status`, se establece EN_CURSO

---

## 🔌 Endpoints Disponibles

### Level Enrollments (12 endpoints)

| Método | Endpoint | Descripción | Paginado |
|--------|----------|-------------|----------|
| GET | `/level-enrollments` | Listar todas | ❌ |
| GET | `/level-enrollments/paged` | Listar todas | ✅ **Recomendado** |
| GET | `/level-enrollments/{id}` | Obtener por ID | ❌ |
| GET | `/level-enrollments/course-enrollment/{id}` | Por inscripción de curso | ❌ |
| GET | `/level-enrollments/level/{id}` | Por nivel | ❌ |
| GET | `/level-enrollments/period/{id}` | Por período | ❌ |
| GET | `/level-enrollments/group/{id}` | Por grupo | ❌ |
| GET | `/level-enrollments/status/{status}` | Por estado | ❌ |
| POST | `/level-enrollments` | Crear | ❌ |
| PUT | `/level-enrollments/{id}` | Actualizar | ❌ |
| PATCH | `/level-enrollments/{id}/status?status=APROBADO` | Actualizar estado | ❌ |
| DELETE | `/level-enrollments/{id}` | Eliminar | ❌ |
| GET | `/level-enrollments/count` | Contar total | ❌ |

### Subject Enrollments (10 endpoints)

| Método | Endpoint | Descripción | Paginado |
|--------|----------|-------------|----------|
| GET | `/subject-enrollments` | Listar todas | ❌ |
| GET | `/subject-enrollments/paged` | Listar todas | ✅ **Recomendado** |
| GET | `/subject-enrollments/{id}` | Obtener por ID | ❌ |
| GET | `/subject-enrollments/level-enrollment/{id}` | Por inscripción de nivel | ❌ |
| GET | `/subject-enrollments/subject-assignment/{id}` | Por asignación de materia | ❌ |
| GET | `/subject-enrollments/status/{status}` | Por estado | ❌ |
| POST | `/subject-enrollments` | Crear | ❌ |
| PUT | `/subject-enrollments/{id}` | Actualizar | ❌ |
| PATCH | `/subject-enrollments/{id}/status?status=APROBADO` | Actualizar estado | ❌ |
| DELETE | `/subject-enrollments/{id}` | Eliminar | ❌ |
| GET | `/subject-enrollments/count` | Contar total | ❌ |

---

## 🚀 Flujo de Implementación

### Paso 1: Inscribir al Estudiante en un Curso

```http
POST /api/course-enrollments
Authorization: Bearer {token}
Content-Type: application/json

{
  "studentId": 1,
  "courseId": 1,
  "academicPeriodId": 1,
  "enrollmentDate": "2026-01-20",
  "enrollmentStatus": "ACTIVO"
}
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Enrollment created successfully",
  "data": {
    "id": 10,
    "studentId": 1,
    "studentName": "Juan Pérez",
    "courseId": 1,
    "courseName": "Desarrollo de Software",
    "academicPeriodId": 1,
    "academicPeriodName": "2026-1",
    "enrollmentDate": "2026-01-20",
    "enrollmentStatus": "ACTIVO"
  }
}
```

⚠️ **Importante:** Guarda el `id` (10 en este ejemplo), lo necesitarás para el siguiente paso.

---

### Paso 2: Inscribir al Estudiante en un Nivel

```http
POST /api/level-enrollments
Authorization: Bearer {token}
Content-Type: application/json

{
  "courseEnrollmentId": 10,
  "levelId": 1,
  "academicPeriodId": 1,
  "groupId": 1,
  "enrollmentDate": "2026-01-20",
  "status": "EN_CURSO"
}
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Level enrollment created successfully",
  "data": {
    "id": 25,
    "courseEnrollmentId": 10,
    "studentName": "Juan Pérez",
    "levelId": 1,
    "levelName": "Nivel 1",
    "academicPeriodId": 1,
    "academicPeriodName": "2026-1",
    "groupId": 1,
    "groupName": "Grupo A - Matutino",
    "enrollmentDate": "2026-01-20",
    "status": "EN_CURSO",
    "finalAverage": null,
    "completionDate": null
  }
}
```

⚠️ **Importante:** Guarda el `id` (25 en este ejemplo), lo necesitarás para inscribir materias.

---

### Paso 3: Inscribir al Estudiante en Materias

Primero, obtén las materias disponibles para el nivel:

```http
GET /api/subject-assignments?levelId=1&periodId=1
Authorization: Bearer {token}
```

Luego, inscribe en cada materia:

```http
POST /api/subject-enrollments
Authorization: Bearer {token}
Content-Type: application/json

{
  "levelEnrollmentId": 25,
  "subjectAssignmentId": 5,
  "enrollmentDate": "2026-01-20",
  "status": "EN_CURSO"
}
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Subject enrollment created successfully",
  "data": {
    "id": 100,
    "levelEnrollmentId": 25,
    "studentName": "Juan Pérez",
    "subjectAssignmentId": 5,
    "subjectName": "Matemáticas I",
    "professorName": "María García",
    "enrollmentDate": "2026-01-20",
    "status": "EN_CURSO",
    "finalGrade": null
  }
}
```

🔁 **Repetir** este paso para cada materia del nivel.

---

## 💻 Ejemplos de Código

### React/TypeScript

```typescript
// types.ts
export interface LevelEnrollmentRequest {
  courseEnrollmentId: number;
  levelId: number;
  academicPeriodId: number;
  groupId?: number;
  enrollmentDate?: string;
  status?: 'EN_CURSO' | 'APROBADO' | 'REPROBADO' | 'RETIRADO';
}

export interface SubjectEnrollmentRequest {
  levelEnrollmentId: number;
  subjectAssignmentId: number;
  enrollmentDate?: string;
  status?: 'EN_CURSO' | 'APROBADO' | 'REPROBADO' | 'RETIRADO';
}

// enrollmentService.ts
const API_URL = 'http://localhost:8080/api';

export const createLevelEnrollment = async (
  data: LevelEnrollmentRequest,
  token: string
) => {
  const response = await fetch(`${API_URL}/level-enrollments`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(data)
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Error creating level enrollment');
  }
  
  return response.json();
};

export const createSubjectEnrollment = async (
  data: SubjectEnrollmentRequest,
  token: string
) => {
  const response = await fetch(`${API_URL}/subject-enrollments`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(data)
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Error creating subject enrollment');
  }
  
  return response.json();
};

// Función completa de inscripción
export const enrollStudentComplete = async (
  studentId: number,
  courseId: number,
  levelId: number,
  periodId: number,
  groupId: number,
  subjectAssignmentIds: number[],
  token: string
) => {
  try {
    // Paso 1: Inscribir en el curso
    const courseEnrollment = await createCourseEnrollment({
      studentId,
      courseId,
      academicPeriodId: periodId,
      enrollmentStatus: 'ACTIVO'
    }, token);
    
    // Paso 2: Inscribir en el nivel
    const levelEnrollment = await createLevelEnrollment({
      courseEnrollmentId: courseEnrollment.data.id,
      levelId,
      academicPeriodId: periodId,
      groupId,
      status: 'EN_CURSO'
    }, token);
    
    // Paso 3: Inscribir en las materias
    const subjectEnrollments = await Promise.all(
      subjectAssignmentIds.map(assignmentId =>
        createSubjectEnrollment({
          levelEnrollmentId: levelEnrollment.data.id,
          subjectAssignmentId: assignmentId,
          status: 'EN_CURSO'
        }, token)
      )
    );
    
    return {
      courseEnrollment: courseEnrollment.data,
      levelEnrollment: levelEnrollment.data,
      subjectEnrollments: subjectEnrollments.map(s => s.data)
    };
  } catch (error) {
    console.error('Error en inscripción completa:', error);
    throw error;
  }
};
```

### Angular

```typescript
// enrollment.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EnrollmentService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  createLevelEnrollment(data: any, token: string): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
    
    return this.http.post(
      `${this.apiUrl}/level-enrollments`,
      data,
      { headers }
    );
  }

  createSubjectEnrollment(data: any, token: string): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
    
    return this.http.post(
      `${this.apiUrl}/subject-enrollments`,
      data,
      { headers }
    );
  }

  getLevelEnrollmentsPaged(
    page: number = 0,
    size: number = 20,
    sort: string = 'enrollmentDate,desc',
    token: string
  ): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    
    return this.http.get(
      `${this.apiUrl}/level-enrollments/paged?page=${page}&size=${size}&sort=${sort}`,
      { headers }
    );
  }
}
```

---

## ✅ Validaciones y Reglas de Negocio

### LevelEnrollment

| Validación | Descripción | Error |
|------------|-------------|-------|
| CourseEnrollment existe | Debe existir en la BD | `ResourceNotFoundException` |
| CourseEnrollment activo | `enrollmentStatus == ACTIVO` | `BusinessException: Course enrollment is not active` |
| Level existe | Debe existir en la BD | `ResourceNotFoundException` |
| AcademicPeriod existe | Debe existir en la BD | `ResourceNotFoundException` |
| AcademicPeriod activo | `isActive == true` | `BusinessException: Academic period is not active` |
| Group existe (opcional) | Si se envía, debe existir | `ResourceNotFoundException` |

### SubjectEnrollment

| Validación | Descripción | Error |
|------------|-------------|-------|
| LevelEnrollment existe | Debe existir en la BD | `ResourceNotFoundException` |
| LevelEnrollment activo | `status == EN_CURSO` | `BusinessException: Level enrollment is not active` |
| SubjectAssignment existe | Debe existir en la BD | `ResourceNotFoundException` |
| **Jerarquía correcta** | `subject.level.id == levelEnrollment.level.id` | `BusinessException: Subject does not belong to the level` |

---

## ⚠️ Manejo de Errores

### Errores Comunes

#### 1. CourseEnrollment no activo

**Request:**
```json
{
  "courseEnrollmentId": 10,
  "levelId": 1,
  "academicPeriodId": 1
}
```

**Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Course enrollment is not active and cannot enroll in levels",
  "timestamp": "2026-01-20T10:30:00"
}
```

**Solución:** Verifica que el CourseEnrollment tenga `enrollmentStatus: "ACTIVO"`.

---

#### 2. Materia no pertenece al nivel correcto

**Request:**
```json
{
  "levelEnrollmentId": 25,
  "subjectAssignmentId": 99
}
```

**Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Subject does not belong to the level of this level enrollment. Subject requires level: Nivel 2",
  "timestamp": "2026-01-20T10:35:00"
}
```

**Solución:** Verifica que la materia (SubjectAssignment) pertenezca al nivel correcto antes de inscribir.

---

#### 3. Recurso no encontrado

**Request:**
```http
GET /api/level-enrollments/999
```

**Response (404 Not Found):**
```json
{
  "success": false,
  "message": "LevelEnrollment not found with id: 999",
  "timestamp": "2026-01-20T10:40:00"
}
```

---

## 📊 Estados y Enums

### LevelStatus (LevelEnrollment)

```typescript
enum LevelStatus {
  EN_CURSO = 'EN_CURSO',       // Estudiante está cursando
  APROBADO = 'APROBADO',       // Nivel aprobado
  REPROBADO = 'REPROBADO',     // Nivel reprobado
  RETIRADO = 'RETIRADO'        // Estudiante retirado
}
```

### SubjectStatus (SubjectEnrollment)

```typescript
enum SubjectStatus {
  EN_CURSO = 'EN_CURSO',       // Estudiante está cursando
  APROBADO = 'APROBADO',       // Materia aprobada
  REPROBADO = 'REPROBADO',     // Materia reprobada
  RETIRADO = 'RETIRADO'        // Estudiante retirado
}
```

### CourseEnrollmentStatus

```typescript
enum EnrollmentStatus {
  ACTIVO = 'ACTIVO',           // ✅ Puede inscribir niveles
  EGRESADO = 'EGRESADO',       // ❌ No puede inscribir niveles
  RETIRADO = 'RETIRADO',       // ❌ No puede inscribir niveles
  INACTIVO = 'INACTIVO'        // ❌ No puede inscribir niveles
}
```

---

## 🔄 Paginación

Todos los endpoints `/paged` soportan paginación:

```http
GET /api/level-enrollments/paged?page=0&size=20&sort=enrollmentDate,desc
```

**Parámetros:**
- `page`: Número de página (0-indexed) - Default: 0
- `size`: Registros por página - Default: 20
- `sort`: Campo y dirección - Default: id,desc

**Respuesta:**
```json
{
  "success": true,
  "message": "Level enrollments page retrieved successfully",
  "data": {
    "content": [ /* array de registros */ ],
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8,
    "first": true,
    "last": false,
    "empty": false,
    "sort": {
      "sorted": true,
      "sortBy": "enrollmentDate",
      "direction": "DESC"
    }
  }
}
```

---

## 📝 Checklist de Implementación

### Frontend Checklist

- [ ] Crear formulario de inscripción al curso (CourseEnrollment)
- [ ] Crear formulario de inscripción al nivel (LevelEnrollment)
- [ ] Crear selección múltiple de materias (SubjectEnrollment)
- [ ] Implementar validación: CourseEnrollment debe estar ACTIVO
- [ ] Implementar validación: LevelEnrollment debe estar EN_CURSO
- [ ] Filtrar materias por nivel y período académico
- [ ] Mostrar nombres resueltos (studentName, levelName, subjectName, etc.)
- [ ] Implementar manejo de errores con mensajes descriptivos
- [ ] Implementar paginación en listados
- [ ] Agregar confirmación antes de eliminar inscripciones
- [ ] Implementar actualización de estados (APROBADO, REPROBADO, etc.)

---

## 🆘 Soporte y Recursos

### Recursos Adicionales

- **Postman Collection:** `postman-collection.json` en la raíz del proyecto
- **README completo:** `README.md`
- **Base de datos:** `BASEDATOS.sql`

### Endpoints de Testing

```bash
# Login para obtener token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail": "admin", "password": "Lagp2022"}'

# Listar inscripciones de nivel
curl http://localhost:8080/api/level-enrollments \
  -H "Authorization: Bearer {token}"

# Listar inscripciones de materias
curl http://localhost:8080/api/subject-enrollments \
  -H "Authorization: Bearer {token}"
```

---

## 📞 Contacto

Para dudas o problemas:
- Revisar la documentación en `README.md`
- Consultar la colección de Postman
- Verificar logs de la API en `app.log`

---

**Última actualización:** Enero 20, 2026  
**Versión:** 2.4.0  
**Estado:** ✅ Producción
