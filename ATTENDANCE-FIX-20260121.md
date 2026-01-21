# Corrección: Mapeo de IDs a Entidades en Creación de Asistencia

**Fecha:** 21 de enero de 2026  
**Versión:** 2.6.1  
**Estado:** ✅ IMPLEMENTADO

---

## 🐛 Problema Identificado

Al intentar crear registros de asistencia desde el frontend, se generaba un `NullPointerException` con el siguiente error:

```
Cannot invoke "com.cesde.studentinfo.model.SubjectEnrollment.getId()" 
because the return value of "com.cesde.studentinfo.model.Attendance.getSubjectEnrollment()" is null
```

### Causa Raíz

El `AttendanceController.createAttendance()` recibía correctamente el DTO con los IDs necesarios:
- `subjectEnrollmentId`
- `classSessionId`
- `assignmentDate`
- `status`
- Otros campos opcionales

**PERO** no los mapeaba a las entidades JPA correspondientes, construyendo un objeto `Attendance` con relaciones `null`, lo que violaba las restricciones `NOT NULL` de la base de datos.

### Error en AttendanceService

El error ocurría en `AttendanceService.java` línea 64, cuando intentaba acceder a:
```java
attendance.getSubjectEnrollment().getId()  // NullPointerException ❌
```

---

## ✅ Solución Implementada

### 1. Nuevo Repositorio Creado

Se creó el repositorio que faltaba para gestionar las sesiones de clase:

#### **ClassSessionRepository.java**
```java
package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {
    
    List<ClassSession> findBySubjectAssignmentId(Long subjectAssignmentId);
    
    List<ClassSession> findBySessionDate(LocalDate sessionDate);
    
    @Query("SELECT cs FROM ClassSession cs WHERE cs.sessionDate BETWEEN :startDate AND :endDate")
    List<ClassSession> findByDateRange(@Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate);
    
    Optional<ClassSession> findBySubjectAssignmentIdAndSessionDate(Long subjectAssignmentId, 
                                                                    LocalDate sessionDate);
}
```

---

### 2. Modificación en AttendanceController

#### Cambios en las Importaciones y Campos
```java
// AGREGADO: Nuevos imports
import com.cesde.studentinfo.model.ClassSession;
import com.cesde.studentinfo.model.SubjectEnrollment;
import com.cesde.studentinfo.repository.ClassSessionRepository;
import com.cesde.studentinfo.repository.SubjectEnrollmentRepository;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    // AGREGADO: Inyección de repositorios necesarios
    private final SubjectEnrollmentRepository subjectEnrollmentRepository;
    private final ClassSessionRepository classSessionRepository;
```

#### Cambios en el Método `createAttendance()`

**ANTES (❌ Incorrecto):**
```java
@PostMapping
public ResponseEntity<ApiResponse<AttendanceResponseDTO>> createAttendance(@Valid @RequestBody AttendanceDTO dto) {
    log.info("POST /attendance - Creating new attendance record");
    
    // ❌ NO mapeaba los IDs a entidades
    Attendance attendance = Attendance.builder()
            .assignmentDate(dto.getAssignmentDate())
            .status(dto.getStatus())
            .isExcused(dto.getIsExcused() != null ? dto.getIsExcused() : false)
            .excuseReason(dto.getExcuseReason())
            .notes(dto.getNotes())
            .build(); // subjectEnrollment = NULL, classSession = NULL ❌
    
    Attendance saved = attendanceService.createAttendance(attendance);
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(AttendanceResponseDTO.fromEntity(saved), "Attendance created successfully"));
}
```

**DESPUÉS (✅ Correcto):**
```java
@PostMapping
public ResponseEntity<ApiResponse<AttendanceResponseDTO>> createAttendance(@Valid @RequestBody AttendanceDTO dto) {
    log.info("POST /attendance - Creating new attendance record for subject enrollment: {}", dto.getSubjectEnrollmentId());

    // ✅ Buscar las entidades relacionadas usando los IDs del DTO
    SubjectEnrollment subjectEnrollment = subjectEnrollmentRepository.findById(dto.getSubjectEnrollmentId())
            .orElseThrow(() -> new ResourceNotFoundException("SubjectEnrollment", dto.getSubjectEnrollmentId()));
    
    ClassSession classSession = classSessionRepository.findById(dto.getClassSessionId())
            .orElseThrow(() -> new ResourceNotFoundException("ClassSession", dto.getClassSessionId()));

    // ✅ Construir la entidad Attendance con las relaciones correctas
    Attendance attendance = Attendance.builder()
            .subjectEnrollment(subjectEnrollment)
            .classSession(classSession)
            .assignmentDate(dto.getAssignmentDate())
            .status(dto.getStatus())
            .isExcused(dto.getIsExcused() != null ? dto.getIsExcused() : false)
            .excuseReason(dto.getExcuseReason())
            .notes(dto.getNotes())
            .build();

    Attendance saved = attendanceService.createAttendance(attendance);
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(AttendanceResponseDTO.fromEntity(saved), "Attendance created successfully"));
}
```

---

## 🔍 Validaciones Implementadas

El nuevo código valida que todas las entidades relacionadas existan:

1. **SubjectEnrollment** debe existir con el ID proporcionado
2. **ClassSession** debe existir con el ID proporcionado

Si alguna entidad no existe, se lanza una `ResourceNotFoundException` con un mensaje claro indicando qué entidad y qué ID no se encontró.

---

## 📊 Flujo de Datos Correcto

```
┌─────────────────┐
│   FRONTEND      │
│  Envía AttendanceDTO │
└────────┬────────┘
         │
         │ POST /attendance
         │ {
         │   "subjectEnrollmentId": 1,
         │   "classSessionId": 5,
         │   "assignmentDate": "2026-01-21",
         │   "status": "PRESENTE"
         │ }
         ▼
┌─────────────────────────────────────┐
│   AttendanceController.createAttendance() │
│                                     │
│  1. Busca SubjectEnrollment(1)      │──► Repository
│  2. Busca ClassSession(5)           │──► Repository
│                                     │
│  3. Construye Attendance con        │
│     relaciones completas            │
└────────┬────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│   AttendanceService.createAttendance() │
│                                     │
│  - Valida no exista duplicado       │
│  - Guarda en base de datos          │
└────────┬────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│   Base de Datos                     │
│                                     │
│  INSERT INTO attendance (           │
│    subject_enrollment_id,           │ ✅ NO NULL
│    class_session_id,                │ ✅ NO NULL
│    assignment_date,                 │
│    status                           │
│  ) VALUES (...)                     │
└─────────────────────────────────────┘
```

---

## 🧪 Testing

### Probar la Solución

**1. Reiniciar la API:**
```bash
cd /home/soporte/Desarrollos/idea/2026/back-bd-API
./start-api.sh
```

**2. Crear un registro de asistencia desde Postman o Frontend:**
```bash
POST http://localhost:8080/attendance
Authorization: Bearer {token}
Content-Type: application/json

{
  "subjectEnrollmentId": 1,
  "classSessionId": 5,
  "assignmentDate": "2026-01-21",
  "status": "PRESENTE",
  "isExcused": false,
  "notes": "Asistió puntualmente"
}
```

**Respuesta Esperada (201 Created):**
```json
{
  "success": true,
  "message": "Attendance created successfully",
  "data": {
    "id": 1,
    "subjectEnrollmentId": 1,
    "classSessionId": 5,
    "studentName": "Juan Pérez",
    "subjectName": "Matemáticas",
    "sessionDate": "2026-01-21",
    "assignmentDate": "2026-01-21",
    "status": "PRESENTE",
    "isExcused": false,
    "notes": "Asistió puntualmente",
    "updateDate": "2026-01-21T12:30:00"
  }
}
```

---

## 📝 Archivos Modificados

1. ✅ **Creado:** `src/main/java/com/cesde/studentinfo/repository/ClassSessionRepository.java`
2. ✅ **Modificado:** `src/main/java/com/cesde/studentinfo/controller/AttendanceController.java`

---

## 🎯 Resultado

- ✅ **Error corregido:** Ya no se genera `NullPointerException`
- ✅ **Mapeo correcto:** Los IDs del DTO se mapean correctamente a las entidades JPA
- ✅ **Validaciones:** Se valida la existencia de todas las entidades relacionadas
- ✅ **Mensajes claros:** Errores descriptivos cuando falta alguna entidad
- ✅ **Compilación exitosa:** `BUILD SUCCESS` en 44.6 segundos
- ✅ **127 archivos compilados correctamente**

---

## 🚀 Próximos Pasos para el Frontend

El frontend **NO necesita cambios**, solo debe continuar enviando el mismo DTO:

```typescript
const attendanceData = {
  subjectEnrollmentId: number,  // ✅ ID de la inscripción a la materia
  classSessionId: number,       // ✅ ID de la sesión de clase
  assignmentDate: string,       // ✅ Fecha en formato YYYY-MM-DD
  status: 'PRESENTE' | 'AUSENTE' | 'TARDANZA',  // ✅ Estado de asistencia
  isExcused?: boolean,          // Opcional: si la ausencia está justificada
  excuseReason?: string,        // Opcional: razón de la justificación
  notes?: string                // Opcional: notas adicionales
};
```

---

## 📌 Notas Importantes

1. **Los dos IDs son obligatorios** (`@NotNull` en el DTO)
2. **El `status` es obligatorio** (PRESENTE, AUSENTE, TARDANZA)
3. **La fecha de asignación es obligatoria**
4. **El campo `isExcused` es opcional**, por defecto es `false`
5. **El sistema ya valida duplicados** - no se puede registrar asistencia dos veces para la misma combinación de estudiante y sesión

---

## 📋 Comparación con GradeController

Esta corrección sigue **exactamente el mismo patrón** que la implementada para `GradeController`:

| Aspecto | GradeController | AttendanceController |
|---------|----------------|---------------------|
| **Repositorios creados** | GradePeriodRepository, GradeComponentRepository | ClassSessionRepository |
| **Entidades mapeadas** | SubjectEnrollment, GradePeriod, GradeComponent | SubjectEnrollment, ClassSession |
| **Patrón de solución** | Buscar entidades → Construir con relaciones | Buscar entidades → Construir con relaciones |
| **Validación de errores** | ResourceNotFoundException | ResourceNotFoundException |

---

**Documentado por:** Sistema de Gestión Académica  
**Revisión:** v2.6.1  
**Relacionado con:** GRADE-FIX-20260121.md
