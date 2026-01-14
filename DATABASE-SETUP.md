# Database Setup Guide

Esta guía explica cómo configurar la base de datos MySQL para el Student Information System API de forma segura.

## 📋 Requisitos Previos

- MySQL 8.x instalado y ejecutándose
- Acceso de administrador a MySQL (usuario root o equivalente)
- Permisos para crear bases de datos y usuarios

---

## 🔧 Configuración Paso a Paso

### 1. Crear la Base de Datos

Ejecutar el script SQL principal que creará todas las tablas, relaciones y datos iniciales:

```bash
mysql -u root -p < BASEDATOS.sql
```

El script incluye:
- Creación de la base de datos `bd-2026-1-cesde`
- Todas las tablas del sistema
- Relaciones y constraints
- Datos iniciales (cursos, niveles, materias, usuarios admin)

### 2. Crear Usuario de Base de Datos

**Opción A: Usuario local (desarrollo)**

```sql
-- Conectar a MySQL como root
mysql -u root -p

-- Crear usuario
CREATE USER 'cesde_user'@'localhost' IDENTIFIED BY 'TuPasswordSeguro';

-- Otorgar permisos
GRANT ALL PRIVILEGES ON `bd-2026-1-cesde`.* TO 'cesde_user'@'localhost';

-- Aplicar cambios
FLUSH PRIVILEGES;
```

**Opción B: Usuario remoto (producción)**

```sql
-- Para acceso desde cualquier IP (usar con precaución)
CREATE USER 'cesde_user'@'%' IDENTIFIED BY 'TuPasswordSeguro';
GRANT ALL PRIVILEGES ON `bd-2026-1-cesde`.* TO 'cesde_user'@'%';
FLUSH PRIVILEGES;

-- Para acceso desde IP específica (recomendado)
CREATE USER 'cesde_user'@'192.168.1.100' IDENTIFIED BY 'TuPasswordSeguro';
GRANT ALL PRIVILEGES ON `bd-2026-1-cesde`.* TO 'cesde_user'@'192.168.1.100';
FLUSH PRIVILEGES;
```

### 3. Configurar Variables de Entorno

**IMPORTANTE:** No incluir credenciales directamente en el código.

#### Linux/Mac

Editar el archivo `.env` en la raíz del proyecto:

```bash
# Copiar plantilla
cp .env.example .env

# Editar con tus credenciales
nano .env
```

Contenido del `.env`:

```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=bd-2026-1-cesde
DB_USERNAME=cesde_user
DB_PASSWORD=TuPasswordSeguro
```

#### Windows

Opción 1: Usar archivo `.env` (igual que Linux)

Opción 2: Variables de sistema
```cmd
setx DB_HOST "localhost"
setx DB_PORT "3306"
setx DB_NAME "bd-2026-1-cesde"
setx DB_USERNAME "cesde_user"
setx DB_PASSWORD "TuPasswordSeguro"
```

Opción 3: PowerShell (temporal)
```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_NAME="bd-2026-1-cesde"
$env:DB_USERNAME="cesde_user"
$env:DB_PASSWORD="TuPasswordSeguro"
```

### 4. Verificar Conexión

Una vez configurado, iniciar la aplicación:

```bash
./start-api.sh
```

Si la conexión es exitosa, verás en los logs:
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

---

## 🔒 Mejores Prácticas de Seguridad

### 1. Contraseñas Fuertes
- Mínimo 12 caracteres
- Mezcla de mayúsculas, minúsculas, números y símbolos
- No usar palabras del diccionario
- Ejemplo: `Xk9$mP2@qL5#nR8!`

### 2. Principio de Mínimo Privilegio
No usar el usuario `root` para la aplicación. Crear usuario específico con permisos limitados:

```sql
-- Solo permisos necesarios
GRANT SELECT, INSERT, UPDATE, DELETE ON `bd-2026-1-cesde`.* TO 'cesde_user'@'localhost';
```

### 3. Usuarios Iniciales

El script `BASEDATOS.sql` crea dos usuarios por defecto:

| Usuario | Password | Rol | Uso |
|---------|----------|-----|-----|
| admin | Lagp2022 | Administrador | Acceso completo |
| user | Lagp2026 | Usuario general | Acceso básico |

**⚠️ IMPORTANTE:** Cambiar estos passwords inmediatamente en producción mediante los endpoints de la API.

### 4. Archivo `.env`
- **NUNCA** subir el archivo `.env` a Git
- Está incluido en `.gitignore`
- Usar `.env.example` como plantilla
- Cada desarrollador debe tener su propio `.env` local

### 5. Producción
Para ambientes de producción:
- Usar gestores de secretos (AWS Secrets Manager, Azure Key Vault, HashiCorp Vault)
- Rotación periódica de credenciales
- Conexiones SSL/TLS a la base de datos
- Firewall de base de datos configurado
- Auditoría de accesos habilitada

---

## 🔍 Solución de Problemas

### Error: "Access denied for user"
```
Causa: Credenciales incorrectas o usuario sin permisos
Solución:
1. Verificar DB_USERNAME y DB_PASSWORD en .env
2. Verificar que el usuario existe: SELECT user, host FROM mysql.user;
3. Verificar permisos: SHOW GRANTS FOR 'cesde_user'@'localhost';
```

### Error: "Unknown database"
```
Causa: Base de datos no creada
Solución: Ejecutar BASEDATOS.sql
```

### Error: "Communications link failure"
```
Causa: MySQL no está ejecutándose o puerto incorrecto
Solución:
- Linux: sudo systemctl status mysql
- Windows: Services → MySQL
- Verificar DB_PORT en .env (por defecto: 3306)
```

### Conexión desde aplicación pero no desde cliente
```
Causa: Usuario configurado solo para localhost
Solución: Crear usuario con host apropiado o '%' para cualquier host
```

---

## 📚 Referencias

- [MySQL User Account Management](https://dev.mysql.com/doc/refman/8.0/en/user-account-management.html)
- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Database Security Best Practices](https://owasp.org/www-project-web-security-testing-guide/)

---

## 🆘 Soporte

Para problemas adicionales, consultar:
1. README.md del proyecto
2. Logs de la aplicación
3. Logs de MySQL: `/var/log/mysql/error.log`

