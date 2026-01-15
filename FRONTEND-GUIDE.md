# 📱 Guía de Integración para Frontend
**Versión API:** 2.1.0  
**Fecha:** Enero 15, 2026  
**Para:** Desarrolladores Frontend
---
## 🎯 Resumen Ejecutivo
El backend de la API tiene **paginación completamente implementada** con 33 endpoints listos para usar.
**⚠️ IMPORTANTE:** El backend proporciona dos tipos de endpoints:
1. **Sin paginación** (ej: `/students`) → Retorna **lista completa**
2. **Con paginación** (ej: `/students/paged`) → Retorna **PagedResponse** con metadatos
**✅ RECOMENDACIÓN:** Usa siempre los endpoints con `/paged` para tablas y listados.
---
## 🔑 Puntos Clave
### Diferencia Entre Endpoints
#### ❌ Endpoints Sin Paginación
```
GET http://localhost:8080/api/students
```
**Respuesta:**
```json
{
  "success": true,
  "message": "Students retrieved successfully",
  "data": [
    { "id": 1, "firstName": "Juan", "lastName": "Pérez", ... },
    { "id": 2, "firstName": "María", "lastName": "García", ... }
    // ... TODOS los registros (puede ser 500+)
  ]
}
```
**Problema:** Si hay 500 estudiantes, retorna los 500. Esto puede causar:
- Lentitud en el frontend
- Alto consumo de memoria
- Mala experiencia de usuario
---
#### ✅ Endpoints Con Paginación (RECOMENDADO)
```
GET http://localhost:8080/api/students/paged?page=0&size=20&sort=lastName,asc
```
**Respuesta:**
```json
{
  "success": true,
  "message": "Students retrieved successfully",
  "data": {
    "content": [
      { "id": 1, "firstName": "Juan", "lastName": "Pérez", ... }
      // ... solo 20 registros
    ],
    "page": 0,
    "size": 20,
    "totalElements": 500,
    "totalPages": 25,
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
**Ventajas:**
- ✅ Solo trae 20 registros por request
- ✅ Incluye metadatos para paginación UI
- ✅ Mejor performance
- ✅ Mejor UX
---
## 📊 Endpoints Paginados Disponibles (33 total)
### Students (3 endpoints)
```
GET /api/students/paged                          - Todos
GET /api/students/active/paged                   - Solo activos
GET /api/students/search/paged?name={name}       - Búsqueda
```
### Professors (3 endpoints)
```
GET /api/professors/paged
GET /api/professors/active/paged
GET /api/professors/search/paged?name={name}
```
### Courses (3 endpoints)
```
GET /api/courses/paged
GET /api/courses/active/paged
GET /api/courses/search/paged?name={name}
```
### Levels (2 endpoints)
```
GET /api/levels/paged
GET /api/levels/course/{courseId}/paged
```
### Subjects (4 endpoints)
```
GET /api/subjects/paged
GET /api/subjects/active/paged
GET /api/subjects/level/{levelId}/paged
GET /api/subjects/search/paged?name={name}
```
### Academic Periods (3 endpoints)
```
GET /api/academic-periods/paged
GET /api/academic-periods/active/paged
GET /api/academic-periods/year/{year}/paged
```
### Users (4 endpoints)
```
GET /api/users/paged
GET /api/users/active/paged
GET /api/users/search/paged?username={username}
GET /api/users/role/{roleName}/paged
```
### Roles (3 endpoints)
```
GET /api/roles/paged
GET /api/roles/enabled/paged
GET /api/roles/search/paged?name={name}
```
---
## 🔧 Parámetros de Paginación
| Parámetro | Tipo | Default | Descripción | Ejemplo |
|-----------|------|---------|-------------|---------|
| `page` | number | `0` | Número de página (0-indexed) | `0`, `1`, `2` |
| `size` | number | `20` | Registros por página | `10`, `20`, `50` |
| `sort` | string | `id,desc` | Campo y dirección | `lastName,asc` |
### Ejemplos de URLs
```bash
# Valores por defecto (página 0, 20 registros)
GET /api/students/paged
# Segunda página con 50 registros
GET /api/students/paged?page=1&size=50
# Ordenar por apellido ascendente
GET /api/students/paged?sort=lastName,asc
# Combinado: página 2, 15 registros, ordenado
GET /api/students/paged?page=1&size=15&sort=enrollmentDate,desc
# Búsqueda con paginación
GET /api/students/search/paged?name=Juan&page=0&size=20
```
---
## 💻 Implementación TypeScript
### 1. Interfaces
```typescript
// Respuesta de la API
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}
// Respuesta paginada
export interface PagedResponse<T> {
  content: T[];           // Registros de esta página
  page: number;           // Página actual (0-indexed)
  size: number;           // Tamaño solicitado
  totalElements: number;  // Total de registros en BD
  totalPages: number;     // Total de páginas
  first: boolean;         // ¿Es la primera página?
  last: boolean;          // ¿Es la última página?
  empty: boolean;         // ¿No hay registros?
  sort: {
    sorted: boolean;
    sortBy: string;
    direction: 'ASC' | 'DESC';
  };
}
// Ejemplo: Entidad Student
export interface Student {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  identificationNumber: string;
  phone?: string;
  dateOfBirth: string;
  isActive: boolean;
}
```
### 2. Función Fetch Genérica
```typescript
async function fetchPaginated<T>(
  endpoint: string,
  page: number = 0,
  size: number = 20,
  sort: string = 'id,desc'
): Promise<PagedResponse<T>> {
  const params = new URLSearchParams({
    page: page.toString(),
    size: size.toString(),
    sort: sort,
  });
  const response = await fetch(
    `http://localhost:8080/api${endpoint}?${params}`,
    {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json',
      },
    }
  );
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  const result: ApiResponse<PagedResponse<T>> = await response.json();
  if (!result.success) {
    throw new Error(result.message);
  }
  return result.data;
}
```
### 3. Uso
```typescript
// Obtener primera página de estudiantes
const studentsPage = await fetchPaginated<Student>(
  '/students/paged',
  0,    // página
  20,   // tamaño
  'lastName,asc'  // ordenamiento
);
console.log(studentsPage.content);       // Array de 20 estudiantes
console.log(studentsPage.totalElements); // Total: ej. 500
console.log(studentsPage.totalPages);    // Total páginas: ej. 25
// Mostrar al usuario
const message = `Mostrando ${studentsPage.content.length} de ${studentsPage.totalElements} estudiantes`;
```
---
## 📋 Metadatos del PagedResponse
| Campo | Descripción | Uso en Frontend |
|-------|-------------|-----------------|
| `content` | Array de registros | Mostrar en tabla |
| `page` | Página actual (0-indexed) | Estado de paginación |
| `size` | Tamaño solicitado | Configuración UI |
| `totalElements` | Total en BD | "Mostrando X de Y" |
| `totalPages` | Total de páginas | Navegación (última página) |
| `first` | ¿Primera página? | Deshabilitar botón "Anterior" |
| `last` | ¿Última página? | Deshabilitar botón "Siguiente" |
| `empty` | ¿Sin registros? | Mostrar mensaje "No hay datos" |
| `sort.sorted` | ¿Está ordenado? | Indicador visual de orden |
| `sort.sortBy` | Campo de orden | Mostrar columna activa |
| `sort.direction` | Dirección (ASC/DESC) | Icono de flecha ↑↓ |
---
## 🎨 Ejemplo de Componente React
```typescript
import { useState, useEffect } from 'react';
function StudentList() {
  const [students, setStudents] = useState<Student[]>([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(20);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);
  useEffect(() => {
    loadStudents();
  }, [page, size]);
  async function loadStudents() {
    setLoading(true);
    try {
      const data = await fetchPaginated<Student>(
        '/students/paged',
        page,
        size,
        'lastName,asc'
      );
      setStudents(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch (error) {
      console.error('Error loading students:', error);
    } finally {
      setLoading(false);
    }
  }
  return (
    <div>
      <h1>Estudiantes</h1>
      {loading ? (
        <p>Cargando...</p>
      ) : (
        <>
          <p>Mostrando {students.length} de {totalElements} estudiantes</p>
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Apellido</th>
                <th>Email</th>
              </tr>
            </thead>
            <tbody>
              {students.map(student => (
                <tr key={student.id}>
                  <td>{student.id}</td>
                  <td>{student.firstName}</td>
                  <td>{student.lastName}</td>
                  <td>{student.email}</td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="pagination">
            <button 
              onClick={() => setPage(p => p - 1)} 
              disabled={page === 0}
            >
              Anterior
            </button>
            <span>Página {page + 1} de {totalPages}</span>
            <button 
              onClick={() => setPage(p => p + 1)} 
              disabled={page >= totalPages - 1}
            >
              Siguiente
            </button>
            <select 
              value={size} 
              onChange={e => { setSize(+e.target.value); setPage(0); }}
            >
              <option value={10}>10 por página</option>
              <option value={20}>20 por página</option>
              <option value={50}>50 por página</option>
            </select>
          </div>
        </>
      )}
    </div>
  );
}
```
---
## ✅ Guía de Decisión
### ¿Qué endpoint usar?
| Escenario | Endpoint | Razón |
|-----------|----------|-------|
| Tabla de estudiantes | `/students/paged` | ✅ Puede tener cientos |
| Tabla de profesores | `/professors/paged` | ✅ Performance |
| Búsqueda de usuarios | `/users/search/paged` | ✅ Resultados variables |
| Dropdown de roles | `/roles` | ⚠️ Son pocos (~4) |
| Select de períodos | `/academic-periods/active` | ⚠️ Generalmente <10 |
**Regla general:** Si es una **tabla** o **listado**, usa `/paged`.
---
## ⚡ Mejores Prácticas
### ✅ Hacer
1. Usar endpoints `/paged` para todas las tablas
2. Mostrar información de paginación al usuario
3. Permitir cambiar tamaño de página (10/20/50)
4. Deshabilitar botones según `first` y `last`
5. Mostrar indicador de carga
6. Mantener estado de paginación en navegación
7. Usar debounce en búsquedas
### ❌ Evitar
1. Cargar listas completas en tablas grandes
2. Páginas muy grandes (>100 registros)
3. No mostrar total de registros
4. Solicitar todas las páginas a la vez
5. Tamaños inconsistentes entre vistas
---
## 🔐 Autenticación
Todos los endpoints requieren JWT token:
```typescript
headers: {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
}
```
Obtener token:
```typescript
const response = await fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    usernameOrEmail: 'admin',
    password: 'Lagp2022'
  })
});
const result = await response.json();
const token = result.data.token;
localStorage.setItem('token', token);
```
---
## 🐛 Troubleshooting
### Problema: "No se cargan los datos"
✅ Verificar:
- ¿Usas el endpoint con `/paged`?
- ¿Tienes token JWT en headers?
- ¿URL correcta? `http://localhost:8080/api`
### Problema: "Página vacía"
✅ Verificar `page` no sea mayor que `totalPages`
### Problema: "CORS error"
✅ Backend tiene CORS configurado para `localhost`
---
## 📞 Recursos
- **README.md** - Sección completa de paginación
- **postman-collection.json** - Carpeta "Pagination Examples" con 12 requests
- **CHANGELOG-v2.1.0.md** - Detalles técnicos
---
## 🚀 Quick Start
```typescript
// 1. Fetch primera página
const response = await fetch(
  'http://localhost:8080/api/students/paged?page=0&size=20',
  {
    headers: {
      'Authorization': `Bearer ${yourToken}`
    }
  }
);
const data = await response.json();
// 2. Usar datos
const students = data.data.content;        // Array
const total = data.data.totalElements;     // Total
const pages = data.data.totalPages;        // Páginas
console.log(`${students.length} de ${total} estudiantes`);
```
---
## ✅ Checklist de Implementación
- [ ] Definir interfaces TypeScript
- [ ] Crear función fetch genérica
- [ ] Implementar componente de tabla
- [ ] Agregar botones anterior/siguiente
- [ ] Mostrar info de paginación
- [ ] Permitir cambiar tamaño de página
- [ ] Manejar loading y errores
- [ ] Incluir token JWT
- [ ] Probar con datos reales
---
**Versión API:** 2.1.0  
**Estado:** ✅ Backend listo - Esperando integración Frontend  
**Endpoints paginados:** 33 disponibles  
**Última actualización:** Enero 15, 2026
