# 🧪 Guía de Pruebas - Corrección de Creación de Notas

**Fecha:** 21 de enero de 2026  
**Versión:** 2.6.0

---

## ✅ Estado de la Implementación

- [x] Repositorios creados
- [x] Controller modificado
- [x] Proyecto compilado exitosamente
- [x] JAR generado: `student-information-system-1.0.0.jar` (52 MB)
- [x] Fecha de compilación: 21/01/2026 10:54:38

---

## 🚀 Pasos para Probar la Solución

### 1. Detener la API Actual (si está corriendo)

```bash
# Opción 1: Usando pkill
pkill -f "student-information-system"

# Opción 2: Liberar puerto 8080
lsof -ti:8080 | xargs kill -9
```

### 2. Iniciar la API con los Cambios

```bash
cd /home/soporte/Desarrollos/idea/2026/back-bd-API
./start-api.sh
```

**Salida esperada:**
```
Starting Student Information System API...
Loading environment variables...
Starting application...
```

### 3. Verificar que la API está corriendo

```bash
# Esperar 15 segundos para que inicie
sleep 15

# Probar health check
curl http://localhost:8080/health
```

**Respuesta esperada:**
```json
{
  "status": "UP",
  "message": "Application is running"
}
```

---

## 🎯 Pruebas de Funcionalidad

### Prerequisitos

Antes de crear una nota, necesitas:

1. **Token JWT válido** (autenticación)
2. **IDs válidos de:**
   - `subjectEnrollmentId` - ID de una inscripción a materia existente
   - `gradePeriodId` - ID de un período de calificación (1, 2, o 3)
   - `gradeComponentId` - ID de un componente de calificación

### Paso 1: Obtener Token

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Guarda el token de la respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": { ... }
}
```

### Paso 2: Verificar Datos Necesarios

#### Listar Subject Enrollments (Inscripciones a Materias)
```bash
curl -X GET http://localhost:8080/subject-enrollments \
  -H "Authorization: Bearer {TU_TOKEN}"
```

#### Listar Grade Periods (Períodos)
```bash
curl -X GET http://localhost:8080/grade-periods \
  -H "Authorization: Bearer {TU_TOKEN}"
```

#### Listar Grade Components (Componentes)
```bash
curl -X GET http://localhost:8080/grade-components \
  -H "Authorization: Bearer {TU_TOKEN}"
```

### Paso 3: Crear una Nota (TEST PRINCIPAL)

```bash
curl -X POST http://localhost:8080/grades \
  -H "Authorization: Bearer {TU_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "subjectEnrollmentId": 1,
    "gradePeriodId": 1,
    "gradeComponentId": 1,
    "gradeValue": 4.5,
    "assignmentDate": "2026-01-21",
    "comments": "Excelente desempeño en el parcial"
  }'
```

### Respuesta Exitosa Esperada ✅

```json
{
  "success": true,
  "message": "Grade created successfully",
  "data": {
    "id": 1,
    "subjectEnrollmentId": 1,
    "studentName": "Juan Pérez",
    "subjectName": "Matemáticas",
    "gradePeriodName": "Primer Periodo",
    "gradeComponentName": "Parcial",
    "gradeValue": 4.50,
    "assignmentDate": "2026-01-21",
    "updateDate": "2026-01-21T10:55:00",
    "comments": "Excelente desempeño en el parcial"
  },
  "timestamp": "2026-01-21T10:55:00"
}
```

### Respuesta de Error (ID Inválido) 

```json
{
  "success": false,
  "message": "SubjectEnrollment with id 999 not found",
  "timestamp": "2026-01-21T10:55:00"
}
```

---

## 🧪 Casos de Prueba

### Caso 1: Nota Válida (Happy Path) ✅
```json
{
  "subjectEnrollmentId": 1,
  "gradePeriodId": 1,
  "gradeComponentId": 1,
  "gradeValue": 4.5
}
```
**Resultado esperado:** `201 Created`

### Caso 2: Nota con Valor Mínimo ✅
```json
{
  "subjectEnrollmentId": 1,
  "gradePeriodId": 1,
  "gradeComponentId": 1,
  "gradeValue": 0.0
}
```
**Resultado esperado:** `201 Created`

### Caso 3: Nota con Valor Máximo ✅
```json
{
  "subjectEnrollmentId": 1,
  "gradePeriodId": 1,
  "gradeComponentId": 1,
  "gradeValue": 5.0
}
```
**Resultado esperado:** `201 Created`

### Caso 4: Nota Fuera de Rango ❌
```json
{
  "subjectEnrollmentId": 1,
  "gradePeriodId": 1,
  "gradeComponentId": 1,
  "gradeValue": 6.0
}
```
**Resultado esperado:** `400 Bad Request` - "Grade must be at most 5.00"

### Caso 5: SubjectEnrollment Inexistente ❌
```json
{
  "subjectEnrollmentId": 99999,
  "gradePeriodId": 1,
  "gradeComponentId": 1,
  "gradeValue": 4.5
}
```
**Resultado esperado:** `404 Not Found` - "SubjectEnrollment with id 99999 not found"

### Caso 6: GradePeriod Inexistente ❌
```json
{
  "subjectEnrollmentId": 1,
  "gradePeriodId": 99,
  "gradeComponentId": 1,
  "gradeValue": 4.5
}
```
**Resultado esperado:** `404 Not Found` - "GradePeriod with id 99 not found"

### Caso 7: GradeComponent Inexistente ❌
```json
{
  "subjectEnrollmentId": 1,
  "gradePeriodId": 1,
  "gradeComponentId": 99,
  "gradeValue": 4.5
}
```
**Resultado esperado:** `404 Not Found` - "GradeComponent with id 99 not found"

---

## 📊 Verificar en la Base de Datos

```sql
-- Ver la nota creada
SELECT * FROM grades ORDER BY id DESC LIMIT 1;

-- Ver con relaciones
SELECT 
    g.id,
    g.grade_value,
    g.assignment_date,
    se.id as subject_enrollment_id,
    gp.name as period_name,
    gc.name as component_name
FROM grades g
JOIN subject_enrollments se ON g.subject_enrollment_id = se.id
JOIN grade_periods gp ON g.grade_period_id = gp.id
JOIN grade_components gc ON g.grade_component_id = gc.id
ORDER BY g.id DESC
LIMIT 1;
```

---

## 📝 Revisar Logs

### Ver logs en tiempo real
```bash
tail -f /home/soporte/Desarrollos/idea/2026/back-bd-API/app.log
```

### Buscar errores recientes
```bash
grep ERROR /home/soporte/Desarrollos/idea/2026/back-bd-API/app.log | tail -20
```

### Log esperado en creación exitosa
```
2026-01-21T10:55:00.123-05:00  INFO ... c.c.s.controller.GradeController   : POST /grades - Creating new grade for subject enrollment: 1
2026-01-21T10:55:00.234-05:00  INFO ... c.c.s.service.GradeService          : Creating grade for subject enrollment: 1
2026-01-21T10:55:00.345-05:00  INFO ... c.c.s.service.GradeService          : Grade created successfully with id: 1
```

---

## 🔧 Troubleshooting

### Problema 1: Puerto 8080 ya en uso
```bash
# Solución
lsof -ti:8080 | xargs kill -9
./start-api.sh
```

### Problema 2: Error de conexión a BD
```bash
# Verificar variables de entorno
cat .env

# Verificar que MySQL esté corriendo
sudo systemctl status mysql
```

### Problema 3: Token expirado
```bash
# Generar nuevo token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

---

## ✅ Checklist de Verificación

- [ ] API iniciada correctamente
- [ ] Health check responde OK
- [ ] Token JWT obtenido
- [ ] Crear nota con datos válidos (201 Created)
- [ ] Validación de rango funciona (400 para valor > 5)
- [ ] Validación de entidades funciona (404 para IDs inexistentes)
- [ ] Logs no muestran NullPointerException
- [ ] Datos guardados correctamente en BD

---

## 🎉 Resultado Esperado

Al completar todas las pruebas, deberías tener:

✅ **Notas creadas exitosamente** desde el frontend/Postman  
✅ **Sin errores de NullPointerException**  
✅ **Validaciones funcionando correctamente**  
✅ **Relaciones JPA correctamente establecidas**  

---

## 📞 Siguiente Paso

Una vez verificado que el backend funciona correctamente, el **frontend puede proceder** a implementar la funcionalidad de creación de notas usando el mismo DTO que ya venía enviando.

**No se requieren cambios en el frontend** ✅

---

**Fin de la Guía de Pruebas**
