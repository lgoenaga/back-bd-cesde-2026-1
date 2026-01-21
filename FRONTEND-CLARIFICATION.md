# ⚠️ ACLARACIÓN IMPORTANTE PARA FRONTEND - Inscripción de Materias
**Fecha:** Enero 20, 2026  
**Versión API:** 2.4.1  
**Prioridad:** 🔴 ALTA - Corrección de concepto erróneo
---
## 🚨 PROBLEMA DETECTADO
El frontend tiene un **concepto INCORRECTO** sobre cómo funciona la inscripción de materias.
### ❌ Concepto Erróneo del Frontend
```typescript
// INCORRECTO ❌
"No hay asignaciones disponibles para este nivel"
// Esto implica que subject_assignments es OBLIGATORIO para inscripción
```
**Error:** El frontend cree que `subject_assignments` es **REQUISITO PREVIO** para mostrar materias.
---
## ✅ CONCEPTO CORRECTO
### La Verdad Sobre las Tablas
Hay **DOS tablas diferentes** con propósitos distintos:
| Tabla | Propósito | ¿Es obligatoria para inscripción? |
|-------|-----------|----------------------------------|
| `subjects` | **Catálogo de materias** por nivel | ✅ SÍ (las materias deben existir) |
| `subject_assignments` | **Asignación de profesores** a materias | ⚠️ SÍ para inscribir, pero puede estar vacío |
---
## 📊 Estructura Real de la Base de Datos
### 1. Tabla `subjects` - QUÉ materias tiene cada nivel
```sql
CREATE TABLE subjects (
    id BIGINT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    level_id BIGINT NOT NULL,        -- ⭐ RELACIÓN: materia → nivel
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);
```
**Endpoint:**
```http
GET /api/subjects/level/{levelId}
```
**Ejemplo:**
```http
GET /api/subjects/level/1
Response:
[
  { id: 1, levelId: 1, name: "Programación I", code: "PROG-101" },
  { id: 2, levelId: 1, name: "Matemáticas I", code: "MATH-101" },
  { id: 3, levelId: 1, name: "Inglés I", code: "ENG-101" }
]
```
---
### 2. Tabla `subject_assignments` - QUIÉN dicta cada materia
```sql
CREATE TABLE subject_assignments (
    id BIGINT PRIMARY KEY,
    subject_id BIGINT NOT NULL,      -- → subjects.id
    professor_id BIGINT NOT NULL,    -- ⭐ Aquí está el profesor
    academic_period_id BIGINT NOT NULL,
    schedule VARCHAR(200),
    classroom VARCHAR(50)
);
```
**Endpoint:**
```http
GET /api/subject-assignments/period/{periodId}
```
**Ejemplo:**
```http
GET /api/subject-assignments/period/1
Response:
[
  { 
    id: 5, 
    subjectId: 1, 
    subjectName: "Programación I",
    levelId: 1,                      // ⭐ Disponible desde v2.4.1
    levelName: "Nivel 1",
    professorFullName: "Juan Pérez",
    schedule: "Lun-Mié 8:00-10:00"
  }
]
```
---
### 3. Tabla `subject_enrollments` - Inscripciones
```sql
CREATE TABLE subject_enrollments (
    id BIGINT PRIMARY KEY,
    level_enrollment_id BIGINT NOT NULL,
    subject_assignment_id BIGINT NOT NULL,  -- ⚠️ Requiere assignment
    enrollment_date DATE NOT NULL,
    status ENUM('EN_CURSO', 'APROBADO', 'REPROBADO', 'RETIRADO')
);
```
**⚠️ IMPORTANTE:** Para inscribirse se necesita `subject_assignment_id`, NO `subject_id`.
**Razón:** La inscripción debe saber:
- ✅ Qué materia
- ✅ Qué profesor
- ✅ Qué horario
- ✅ Qué período
---
## 🎯 RESPUESTAS A TUS PREGUNTAS
### 1. ¿Cuál es la tabla para saber las materias por nivel?
**Respuesta:** La tabla `subjects` con la columna `level_id`
**Endpoint:**
```http
GET /api/subjects/level/{levelId}
```
**Ejemplo de uso:**
```typescript
// Obtener todas las materias del Nivel 1
const subjects = await fetch('/api/subjects/level/1');
console.log(subjects.data);
// [
//   { id: 1, levelId: 1, name: "Programación I" },
//   { id: 2, levelId: 1, name: "Matemáticas I" },
//   { id: 3, levelId: 1, name: "Inglés I" }
// ]
```
---
### 2. ¿Es necesario tener profesores en las materias para la inscripción?
**Respuesta:** **SÍ**, porque `subject_enrollments` requiere `subject_assignment_id`.
**PERO** si no hay asignaciones, NO es un error del sistema:
| Situación | Significado | ¿Qué mostrar? |
|-----------|-------------|---------------|
| Hay materias + NO hay asignaciones | Profesores pendientes de asignar | Info + guía para usuario |
| NO hay materias | Error de configuración | Error real del sistema |
| Hay materias + hay asignaciones | Todo correcto | Permitir inscripción |
---
### 3. ¿El mensaje del frontend es correcto?
**Respuesta:** ❌ **NO, es COMPLETAMENTE INCORRECTO**
#### ❌ Mensaje Actual (INCORRECTO)
```typescript
if (!subjectAssignments || subjectAssignments.length === 0) {
  return "No hay asignaciones disponibles para este nivel";
}
```
**Problemas:**
- ❌ Parece error del sistema
- ❌ No explica qué son "asignaciones"
- ❌ No dice qué hacer
- ❌ No muestra las materias que SÍ existen
#### ✅ Mensaje Correcto (RECOMENDADO)
```typescript
// PASO 1: Verificar materias (catálogo)
const subjects = await fetch(`/api/subjects/level/${levelId}`);
if (!subjects.data || subjects.data.length === 0) {
  // ⚠️ ESTE SÍ es un error real
  return (
    <Alert severity="error">
      Este nivel no tiene materias configuradas. 
      Contacte al administrador del sistema.
    </Alert>
  );
}
// PASO 2: Verificar asignaciones de profesores
const assignments = await fetch(`/api/subject-assignments/period/${periodId}`);
const forLevel = assignments.data?.filter(a => a.levelId === levelId) || [];
if (forLevel.length === 0) {
  // ⚠️ Esto NO es error, es situación temporal
  return (
    <Alert severity="info">
      <Typography variant="h6" gutterBottom>
        📚 Profesores Pendientes de Asignación
      </Typography>
      <Typography paragraph>
        Este nivel tiene <strong>{subjects.data.length} materias disponibles</strong>:
      </Typography>
      <List dense>
        {subjects.data.map(subject => (
          <ListItem key={subject.id}>
            <ListItemIcon>📖</ListItemIcon>
            <ListItemText 
              primary={subject.name}
              secondary={`Código: ${subject.code}`}
            />
          </ListItem>
        ))}
      </List>
      <Divider sx={{ my: 2 }} />
      <Typography variant="subtitle1" gutterBottom>
        <strong>¿Por qué no puedo inscribirme?</strong>
      </Typography>
      <Typography paragraph>
        Los profesores para estas materias aún no han sido asignados para 
        el período académico actual. Esto es normal al inicio del período.
      </Typography>
      <Typography variant="subtitle1" gutterBottom>
        <strong>¿Qué debo hacer?</strong>
      </Typography>
      <Typography component="div">
        <ol>
          <li>Contacta al coordinador académico</li>
          <li>Solicita la asignación de profesores</li>
          <li>Una vez asignados, regresa para inscribirte</li>
        </ol>
      </Typography>
      <Button 
        variant="outlined" 
        color="primary"
        startIcon={<EmailIcon />}
        onClick={() => window.location.href = '/contacto'}
      >
        Contactar Coordinación
      </Button>
    </Alert>
  );
}
// PASO 3: Mostrar materias con profesores (inscripción normal)
return (
  <Box>
    <Typography variant="h6" gutterBottom>
      ✅ Seleccione las materias a inscribir
    </Typography>
    {forLevel.map(assignment => (
      <Card key={assignment.id} sx={{ mb: 2 }}>
        <CardContent>
          <Typography variant="h6">{assignment.subjectName}</Typography>
          <Typography color="text.secondary">
            👨‍🏫 {assignment.professorFullName}
          </Typography>
          <Typography color="text.secondary">
            📅 {assignment.schedule}
          </Typography>
          <Typography color="text.secondary">
            🏫 {assignment.classroom || 'Por asignar'}
          </Typography>
        </CardContent>
        <CardActions>
          <Button 
            variant="contained" 
            onClick={() => enrollInSubject(assignment.id)}
          >
            Inscribir
          </Button>
        </CardActions>
      </Card>
    ))}
  </Box>
);
```
---
## 🔄 FLUJO CORRECTO DE VERIFICACIÓN
```typescript
async function checkEnrollmentAvailability(levelId: number, periodId: number) {
  // Paso 1: ¿Existen materias en el catálogo?
  const subjects = await fetch(`/api/subjects/level/${levelId}`);
  if (!subjects.data || subjects.data.length === 0) {
    return {
      status: 'ERROR',
      type: 'NO_SUBJECTS',
      message: 'Este nivel no tiene materias configuradas',
      canEnroll: false,
      showError: true
    };
  }
  // Paso 2: ¿Hay profesores asignados?
  const assignments = await fetch(`/api/subject-assignments/period/${periodId}`);
  const forLevel = assignments.data?.filter(a => a.levelId === levelId) || [];
  if (forLevel.length === 0) {
    return {
      status: 'PENDING',
      type: 'NO_PROFESSORS',
      message: 'Profesores pendientes de asignación',
      subjects: subjects.data,
      canEnroll: false,
      showError: false,  // ⚠️ NO es error, es info
      showGuidance: true
    };
  }
  // Paso 3: Todo listo
  return {
    status: 'READY',
    type: 'ALL_OK',
    message: 'Listo para inscripción',
    assignments: forLevel,
    canEnroll: true,
    showError: false
  };
}
// Uso en componente
const availability = await checkEnrollmentAvailability(levelId, periodId);
if (availability.status === 'ERROR') {
  return <ErrorAlert message={availability.message} />;
}
if (availability.status === 'PENDING') {
  return <PendingProfessorsInfo subjects={availability.subjects} />;
}
if (availability.status === 'READY') {
  return <EnrollmentForm assignments={availability.assignments} />;
}
```
---
## 📊 COMPARACIÓN DE ESCENARIOS
### Escenario A: Configuración Incompleta (ERROR REAL)
```
Base de datos:
  subjects: [] ❌ (vacío)
  subject_assignments: [] (vacío)
Frontend debe mostrar:
  ⚠️ ERROR: "Este nivel no tiene materias configuradas"
  Acción: Contactar administrador del SISTEMA
```
### Escenario B: Profesores Pendientes (SITUACIÓN TEMPORAL)
```
Base de datos:
  subjects: [Prog I, Math I, Ing I] ✅ (tiene datos)
  subject_assignments: [] ⚠️ (vacío temporalmente)
Frontend debe mostrar:
  ℹ️ INFO: "Profesores pendientes de asignación"
  Lista: Mostrar las 3 materias que existen
  Acción: Contactar coordinador ACADÉMICO para asignar profesores
```
### Escenario C: Todo Correcto (LISTO)
```
Base de datos:
  subjects: [Prog I, Math I, Ing I] ✅
  subject_assignments: [Prog I-Prof.Juan, Math I-Prof.María] ✅
Frontend debe mostrar:
  ✅ "Seleccione las materias a inscribir"
  Formulario: Permitir inscripción normal
```
---
## 🎯 ENDPOINTS CORRECTOS
### Para obtener materias de un nivel:
```http
GET /api/subjects/level/{levelId}
```
**Ejemplo:**
```bash
curl http://localhost:8080/api/subjects/level/1 \
  -H "Authorization: Bearer {token}"
```
**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "levelId": 1,
      "levelName": "Nivel 1 - Fundamentos",
      "code": "PROG-101",
      "name": "Programación I",
      "credits": 3.0,
      "hoursPerWeek": 4,
      "isActive": true
    },
    {
      "id": 2,
      "levelId": 1,
      "levelName": "Nivel 1 - Fundamentos",
      "code": "MATH-101",
      "name": "Matemáticas I",
      "credits": 3.0,
      "hoursPerWeek": 4,
      "isActive": true
    }
  ]
}
```
### Para obtener asignaciones con profesores:
```http
GET /api/subject-assignments/period/{periodId}
```
Luego filtrar por `levelId` en el cliente:
```typescript
const forLevel = assignments.data.filter(a => a.levelId === levelId);
```
**Response (incluye levelId desde v2.4.1):**
```json
{
  "success": true,
  "data": [
    {
      "id": 5,
      "subjectId": 1,
      "subjectName": "Programación I",
      "subjectCode": "PROG-101",
      "levelId": 1,
      "levelName": "Nivel 1 - Fundamentos",
      "professorId": 1,
      "professorFullName": "Juan Pérez",
      "professorEmail": "juan.perez@cesde.edu.co",
      "schedule": "Lunes y Miércoles 8:00-10:00",
      "classroom": "Aula 101",
      "isActive": true
    }
  ]
}
```
---
## ✅ CHECKLIST DE CORRECCIÓN
- [ ] Eliminar mensaje "No hay asignaciones disponibles para este nivel"
- [ ] Implementar verificación de materias con `/api/subjects/level/{levelId}`
- [ ] Diferenciar entre "no hay materias" (ERROR) y "no hay profesores" (INFO)
- [ ] Mostrar lista de materias disponibles incluso sin profesores
- [ ] Cambiar Alert de `severity="warning"` a `severity="info"`
- [ ] Agregar guía clara para el usuario (qué hacer)
- [ ] Agregar botón de "Contactar Coordinación"
- [ ] Actualizar documentación interna del equipo frontend
- [ ] Capacitar al equipo sobre la diferencia entre `subjects` y `subject_assignments`
---
## 📝 RESUMEN EJECUTIVO
### Para el Product Owner / Scrum Master
**Situación actual:** El frontend muestra un mensaje de error confuso que bloquea la experiencia del usuario.
**Problema:** Confunde dos conceptos:
- Materias (catálogo permanente)
- Asignaciones de profesores (temporal por período)
**Impacto:** Los usuarios piensan que hay un error del sistema cuando en realidad solo falta un proceso administrativo normal.
**Solución:** 
1. Verificar primero si existen materias
2. Luego verificar si hay profesores asignados
3. Mostrar mensajes apropiados para cada caso
4. Guiar al usuario sobre qué hacer
**Esfuerzo estimado:** 2-4 horas de desarrollo + testing
**Prioridad:** Alta (UX crítica)
---
## 🎓 GLOSARIO
| Término | Significado | Tabla BD | Endpoint |
|---------|-------------|----------|----------|
| **Materia** | Asignatura del plan de estudios | `subjects` | `/api/subjects/level/{id}` |
| **Asignación** | Profesor asignado a una materia en un período | `subject_assignments` | `/api/subject-assignments/period/{id}` |
| **Inscripción** | Estudiante matriculado en una materia | `subject_enrollments` | `/api/subject-enrollments` |
| **Nivel** | Etapa del curso (Nivel 1, 2, 3, etc.) | `levels` | `/api/levels/course/{id}` |
---
## 📞 Soporte
**Documentación relacionada:**
- `ENROLLMENT-COMPLETE-GUIDE.md` - Guía completa de inscripciones
- `README.md` - Documentación general de la API
- `BASEDATOS.sql` - Estructura completa de la base de datos
**Para aclaraciones técnicas:** Contactar equipo de backend
**Para cambios de proceso:** Contactar coordinador académico
---
**Última actualización:** Enero 20, 2026  
**Versión:** 2.4.1  
**Prioridad:** 🔴 ALTA - Requiere acción inmediata
