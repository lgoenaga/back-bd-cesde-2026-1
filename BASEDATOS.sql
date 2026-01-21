-- ============================================================================
-- STUDENT INFORMATION SYSTEM - DATABASE SCHEMA
-- ============================================================================
-- Database: bd-2026-1-cesde
-- User: cesde_user
-- Password: [Configurar mediante variables de entorno - Ver DATABASE-SETUP.md]
-- Generated: 2026-01-08
-- Last Updated: 2026-01-20 (v2.5.0)
-- MySQL Native Syntax
-- 
-- CHANGELOG v2.5.0 (Enero 20, 2026):
-- - Tabla subject_enrollments actualizada:
--   * subject_id: Ahora OBLIGATORIO (referencia directa a subjects)
--   * subject_assignment_id: Ahora OPCIONAL (para trazabilidad de profesor)
--   * Nuevo constraint: fk_subject_enrollment_subject
--   * Nuevo indice: idx_subject_enrollment_subject
--   * UNIQUE KEY actualizado: (level_enrollment_id, subject_id)
--   * Permite inscripcion sin profesor asignado
-- ============================================================================

-- Drop database if exists and create fresh
DROP DATABASE IF EXISTS `bd-2026-1-cesde`;
CREATE DATABASE `bd-2026-1-cesde`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `bd-2026-1-cesde`;

-- ============================================================================
-- 1. CORE ENTITIES - BASE TABLES
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Table: courses (Cursos)
-- Description: Stores the three required courses
-- Business Rule: 'Desarrollo de Software', 'Diseño Grafico', 'Auxiliar Administrativo'
-- ----------------------------------------------------------------------------
CREATE TABLE `courses` (
    `id` BIGINT AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL UNIQUE COMMENT 'Course name',
    `code` VARCHAR(20) NOT NULL UNIQUE COMMENT 'Short code (e.g., DS, DG, AA)',
    `description` TEXT NULL COMMENT 'Course description',
    `total_levels` TINYINT NOT NULL DEFAULT 3 COMMENT 'Total levels in course',
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_course_code` (`code`),
    INDEX `idx_course_active` (`is_active`)
) ENGINE=InnoDB COMMENT='Available courses in the institution';

-- ----------------------------------------------------------------------------
-- Table: levels (Niveles)
-- Description: Stores the 3 levels for each course
-- Business Rule: Each course has exactly 3 levels
-- ----------------------------------------------------------------------------
CREATE TABLE `levels` (
    `id` BIGINT AUTO_INCREMENT,
    `course_id` BIGINT NOT NULL,
    `level_number` TINYINT NOT NULL COMMENT 'Level number: 1, 2, or 3',
    `name` VARCHAR(50) NOT NULL COMMENT 'e.g., Nivel 1, Nivel 2, Nivel 3',
    `description` TEXT,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_course_level` (`course_id`, `level_number`),
    INDEX `idx_level_number` (`level_number`),
    CONSTRAINT `fk_level_course`
        FOREIGN KEY (`course_id`)
        REFERENCES `courses` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT `chk_level_number`
        CHECK (`level_number` BETWEEN 1 AND 3)
) ENGINE=InnoDB COMMENT='Course levels (3 per course)';

-- ----------------------------------------------------------------------------
-- Table: subjects (Materias)
-- Description: Stores subjects for each level
-- Business Rule: 3 subjects per level, 9 subjects per course total
--                Subject names are unique per course (e.g., 'Ingles - DS')
-- ----------------------------------------------------------------------------
CREATE TABLE `subjects` (
    `id` BIGINT AUTO_INCREMENT,
    `course_id` BIGINT NOT NULL,
    `level_id` BIGINT NOT NULL,
    `name` VARCHAR(100) NOT NULL COMMENT 'Subject name with course suffix (e.g., Ingles - DS)',
    `code` VARCHAR(20) NOT NULL COMMENT 'Subject code',
    `description` TEXT,
    `hours_per_week` TINYINT NULL COMMENT 'Weekly hours',
    `credits` DECIMAL(3,1) NULL COMMENT 'Academic credits',
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_subject_name_course` (`name`, `course_id`),
    UNIQUE KEY `uk_subject_code_course` (`code`, `course_id`),
    INDEX `idx_subject_level` (`level_id`),
    INDEX `idx_subject_course` (`course_id`),
    INDEX `idx_subject_active` (`is_active`),
    CONSTRAINT `fk_subject_course`
        FOREIGN KEY (`course_id`)
        REFERENCES `courses` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT `fk_subject_level`
        FOREIGN KEY (`level_id`)
        REFERENCES `levels` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Subjects per course and level';

-- ----------------------------------------------------------------------------
-- Table: professors (Profesores)
-- Description: Stores professor information
-- Business Rule: Professors can teach any subject across any course
-- ----------------------------------------------------------------------------
CREATE TABLE `professors` (
    `id` BIGINT AUTO_INCREMENT,
    `identification_type` ENUM('CC', 'CE', 'TI', 'PAS') NOT NULL DEFAULT 'CC' COMMENT 'ID type',
    `identification_number` VARCHAR(20) NOT NULL UNIQUE,
    `first_name` VARCHAR(50) NOT NULL,
    `last_name` VARCHAR(50) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `phone` VARCHAR(20),
    `mobile` VARCHAR(20),
    `address` VARCHAR(200),
    `date_of_birth` DATE,
    `hire_date` DATE NOT NULL,
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_professor_id_number` (`identification_number`),
    INDEX `idx_professor_email` (`email`),
    INDEX `idx_professor_active` (`is_active`)
) ENGINE=InnoDB COMMENT='Professors information';

-- ----------------------------------------------------------------------------
-- Table: students (Estudiantes)
-- Description: Stores student information
-- ----------------------------------------------------------------------------
CREATE TABLE `students` (
    `id` BIGINT AUTO_INCREMENT,
    `identification_type` ENUM('CC', 'CE', 'TI', 'PAS') NOT NULL DEFAULT 'CC' COMMENT 'ID type',
    `identification_number` VARCHAR(20) NOT NULL UNIQUE,
    `first_name` VARCHAR(50) NOT NULL,
    `last_name` VARCHAR(50) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `phone` VARCHAR(20),
    `mobile` VARCHAR(20),
    `address` VARCHAR(200),
    `date_of_birth` DATE NOT NULL,
    `enrollment_date` DATE NOT NULL COMMENT 'First enrollment date',
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_student_id_number` (`identification_number`),
    INDEX `idx_student_email` (`email`),
    INDEX `idx_student_active` (`is_active`)
) ENGINE=InnoDB COMMENT='Students information';

-- ============================================================================
-- 2. PERIOD AND ENROLLMENT MANAGEMENT
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Table: academic_periods (Periodos Academicos)
-- Description: Stores academic periods (levels dictated twice per year)
-- Business Rule: Levels are dictated twice per year
-- ----------------------------------------------------------------------------
CREATE TABLE `academic_periods` (
    `id` BIGINT AUTO_INCREMENT,
    `year` YEAR NOT NULL,
    `period_number` TINYINT NOT NULL COMMENT '1 or 2 (semester)',
    `name` VARCHAR(50) NOT NULL COMMENT 'e.g., 2026-1, 2026-2',
    `start_date` DATE NOT NULL,
    `end_date` DATE NOT NULL,
    `is_active` BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Only one period can be active',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_period_year_number` (`year`, `period_number`),
    INDEX `idx_period_active` (`is_active`),
    INDEX `idx_period_dates` (`start_date`, `end_date`),
    CONSTRAINT `chk_period_number`
        CHECK (`period_number` IN (1, 2)),
    CONSTRAINT `chk_period_dates`
        CHECK (`end_date` > `start_date`)
) ENGINE=InnoDB COMMENT='Academic periods (2 per year)';

-- ----------------------------------------------------------------------------
-- Table: course_enrollments (Inscripciones a Curso)
-- Description: Stores student enrollments to specific courses
-- Business Rule: Students cannot enroll in multiple courses in the same period
--                After completing all 3 levels, they can enroll in a new course
-- ----------------------------------------------------------------------------
CREATE TABLE `course_enrollments` (
    `id` BIGINT AUTO_INCREMENT,
    `student_id` BIGINT NOT NULL,
    `course_id` BIGINT NOT NULL,
    `academic_period_id` BIGINT NOT NULL COMMENT 'Initial enrollment period',
    `enrollment_date` DATE NOT NULL,
    `enrollment_status` ENUM('ACTIVO', 'EGRESADO', 'RETIRADO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    `completion_date` DATE NULL COMMENT 'Date when all 3 levels completed',
    `notes` TEXT NULL COMMENT 'Additional notes',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_course_period` (`student_id`, `course_id`, `academic_period_id`),
    INDEX `idx_enrollment_student` (`student_id`),
    INDEX `idx_enrollment_course` (`course_id`),
    INDEX `idx_enrollment_period` (`academic_period_id`),
    INDEX `idx_enrollment_status` (`enrollment_status`),
    CONSTRAINT `fk_enrollment_student`
        FOREIGN KEY (`student_id`)
        REFERENCES `students` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT `fk_enrollment_course`
        FOREIGN KEY (`course_id`)
        REFERENCES `courses` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT `fk_enrollment_period`
        FOREIGN KEY (`academic_period_id`)
        REFERENCES `academic_periods` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Student course enrollments';

-- ----------------------------------------------------------------------------
-- Table: student_status_history (Historial de Estados)
-- Description: Tracks student status changes per course enrollment
-- Business Rule: Student can hold multiple statuses (Egresado de uno, Activo en otro)
-- ----------------------------------------------------------------------------
CREATE TABLE `student_status_history` (
    `id` BIGINT AUTO_INCREMENT,
    `course_enrollment_id` BIGINT NOT NULL,
    `status` ENUM('ACTIVO', 'EGRESADO', 'RETIRADO', 'INACTIVO') NOT NULL,
    `status_date` DATE NOT NULL,
    `reason` TEXT NULL COMMENT 'Reason for status change',
    `created_by` VARCHAR(50) NULL COMMENT 'User who made the change',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_status_enrollment` (`course_enrollment_id`),
    INDEX `idx_status_date` (`status_date`),
    INDEX `idx_status_type` (`status`),
    CONSTRAINT `fk_status_enrollment`
        FOREIGN KEY (`course_enrollment_id`)
        REFERENCES `course_enrollments` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Student status change history per course';

-- ----------------------------------------------------------------------------
-- Table: level_enrollments (Matriculas por Nivel)
-- Description: Stores student enrollments to specific levels in a period
-- Business Rule: Each level enrollment is in a specific period
-- ----------------------------------------------------------------------------
CREATE TABLE `level_enrollments` (
    `id` BIGINT AUTO_INCREMENT,
    `course_enrollment_id` BIGINT NOT NULL,
    `level_id` BIGINT NOT NULL,
    `academic_period_id` BIGINT NOT NULL,
    `enrollment_date` DATE NOT NULL,
    `status` ENUM('EN_CURSO', 'APROBADO', 'REPROBADO', 'RETIRADO') NOT NULL DEFAULT 'EN_CURSO',
    `final_average` DECIMAL(4,2) NULL COMMENT 'Final average for the level',
    `completion_date` DATE,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_course_enrollment_level_period` (`course_enrollment_id`, `level_id`, `academic_period_id`),
    INDEX `idx_level_enrollment_course` (`course_enrollment_id`),
    INDEX `idx_level_enrollment_level` (`level_id`),
    INDEX `idx_level_enrollment_period` (`academic_period_id`),
    INDEX `idx_level_enrollment_status` (`status`),
    CONSTRAINT `fk_level_enrollment_course`
        FOREIGN KEY (`course_enrollment_id`)
        REFERENCES `course_enrollments` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_level_enrollment_level`
        FOREIGN KEY (`level_id`)
        REFERENCES `levels` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT `fk_level_enrollment_period`
        FOREIGN KEY (`academic_period_id`)
        REFERENCES `academic_periods` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT `chk_final_average`
        CHECK (`final_average` BETWEEN 0 AND 5)
) ENGINE=InnoDB COMMENT='Student enrollments per level and period';

-- ----------------------------------------------------------------------------
-- Table: subject_assignments (Asignacion de Materias)
-- Description: Links professors to subjects for a specific period
-- Business Rule: Professors can teach any subject across any course
-- ----------------------------------------------------------------------------
CREATE TABLE `subject_assignments` (
    `id` BIGINT AUTO_INCREMENT,
    `subject_id` BIGINT NOT NULL,
    `professor_id` BIGINT NOT NULL,
    `academic_period_id` BIGINT NOT NULL,
    `schedule` VARCHAR(200) NULL COMMENT 'Class schedule',
    `classroom` VARCHAR(50) NULL COMMENT 'Assigned classroom',
    `max_students` TINYINT NULL COMMENT 'Maximum students allowed',
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_subject_professor_period` (`subject_id`, `professor_id`, `academic_period_id`),
    INDEX `idx_assignment_subject` (`subject_id`),
    INDEX `idx_assignment_professor` (`professor_id`),
    INDEX `idx_assignment_period` (`academic_period_id`),
    INDEX `idx_assignment_active` (`is_active`),
    CONSTRAINT `fk_assignment_subject`
        FOREIGN KEY (`subject_id`)
        REFERENCES `subjects` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT `fk_assignment_professor`
        FOREIGN KEY (`professor_id`)
        REFERENCES `professors` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT `fk_assignment_period`
        FOREIGN KEY (`academic_period_id`)
        REFERENCES `academic_periods` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Professor assignments to subjects per period';

-- ----------------------------------------------------------------------------
-- ----------------------------------------------------------------------------
-- Table: subject_enrollments (Inscripciones a Materias)
-- Description: Stores student enrollments to specific subjects
-- ACTUALIZADO v2.5.0: subject_id obligatorio, subject_assignment_id opcional
-- ----------------------------------------------------------------------------
CREATE TABLE `subject_enrollments` (
    `id` BIGINT AUTO_INCREMENT,
    `level_enrollment_id` BIGINT NOT NULL,
    `subject_id` BIGINT NOT NULL COMMENT 'Direct reference to subject (mandatory)',
    `subject_assignment_id` BIGINT NULL COMMENT 'Reference to professor assignment (optional - for traceability)',
    `enrollment_date` DATE NOT NULL,
    `status` ENUM('EN_CURSO', 'APROBADO', 'REPROBADO', 'RETIRADO') NOT NULL DEFAULT 'EN_CURSO',
    `final_grade` DECIMAL(4,2) NULL COMMENT 'Final grade (Definitiva) for the subject',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_level_enrollment_subject` (`level_enrollment_id`, `subject_id`),
    INDEX `idx_subject_enrollment_level` (`level_enrollment_id`),
    INDEX `idx_subject_enrollment_subject` (`subject_id`),
    INDEX `idx_subject_enrollment_assignment` (`subject_assignment_id`),
    INDEX `idx_subject_enrollment_status` (`status`),
    CONSTRAINT `fk_subject_enrollment_level`
        FOREIGN KEY (`level_enrollment_id`)
        REFERENCES `level_enrollments` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_subject_enrollment_subject`
        FOREIGN KEY (`subject_id`)
        REFERENCES `subjects` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT `fk_subject_enrollment_assignment`
        FOREIGN KEY (`subject_assignment_id`)
        REFERENCES `subject_assignments` (`id`)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    CONSTRAINT `chk_subject_final_grade`
        CHECK (`final_grade` BETWEEN 0 AND 5)
) ENGINE=InnoDB COMMENT='Student enrollments to subjects';

-- ============================================================================
-- 3. GRADING SYSTEM (NOTAS)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Table: grade_periods (Periodos de Calificacion)
-- Description: Defines the 3 main grading periods per subject
-- Business Rule: Final grade composed of 3 main grades
-- ----------------------------------------------------------------------------
CREATE TABLE `grade_periods` (
    `id` BIGINT AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL COMMENT 'e.g., Periodo 1, Periodo 2, Periodo 3',
    `period_number` TINYINT NOT NULL COMMENT '1, 2, or 3',
    `weight_percentage` DECIMAL(5,2) NOT NULL DEFAULT 33.33 COMMENT 'Weight in final grade',
    `description` TEXT,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_grade_period_number` (`period_number`),
    CONSTRAINT `chk_grade_period_number`
        CHECK (`period_number` BETWEEN 1 AND 3),
    CONSTRAINT `chk_weight_percentage`
        CHECK (`weight_percentage` BETWEEN 0 AND 100)
) ENGINE=InnoDB COMMENT='Three main grading periods';

-- ----------------------------------------------------------------------------
-- Table: grade_components (Componentes de Calificacion)
-- Description: Defines the 3 components per grade: Conocimiento, Desarrollo, Producto
-- Business Rule: Each of the 3 main grades has 3 sub-notes
-- ----------------------------------------------------------------------------
CREATE TABLE `grade_components` (
    `id` BIGINT AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL COMMENT 'Conocimiento, Desarrollo, Producto',
    `code` VARCHAR(10) NOT NULL UNIQUE COMMENT 'CON, DES, PRO',
    `weight_percentage` DECIMAL(5,2) NOT NULL DEFAULT 33.33 COMMENT 'Weight in period grade',
    `description` TEXT,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_component_name` (`name`),
    CONSTRAINT `chk_component_weight`
        CHECK (`weight_percentage` BETWEEN 0 AND 100)
) ENGINE=InnoDB COMMENT='Grade components: Knowledge, Development, Product';

-- ----------------------------------------------------------------------------
-- Table: grades (Notas)
-- Description: Stores individual grades (9 sub-notes per subject enrollment)
-- Business Rule: 3 periods × 3 components = 9 sub-notes per subject
-- ----------------------------------------------------------------------------
CREATE TABLE `grades` (
    `id` BIGINT AUTO_INCREMENT,
    `subject_enrollment_id` BIGINT NOT NULL,
    `grade_period_id` BIGINT NOT NULL,
    `grade_component_id` BIGINT NOT NULL,
    `grade_value` DECIMAL(4,2) NOT NULL COMMENT 'Grade value (0.00 to 5.00)',
    `assignment_date` DATE NOT NULL COMMENT 'Date when grade was assigned',
    `update_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `comments` TEXT NULL COMMENT 'Teacher comments',
    `assigned_by` BIGINT NULL COMMENT 'Professor who assigned the grade',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_enrollment_period_component` (`subject_enrollment_id`, `grade_period_id`, `grade_component_id`),
    INDEX `idx_grade_enrollment` (`subject_enrollment_id`),
    INDEX `idx_grade_period` (`grade_period_id`),
    INDEX `idx_grade_component` (`grade_component_id`),
    INDEX `idx_grade_assignment_date` (`assignment_date`),
    CONSTRAINT `fk_grade_enrollment`
        FOREIGN KEY (`subject_enrollment_id`)
        REFERENCES `subject_enrollments` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_grade_period`
        FOREIGN KEY (`grade_period_id`)
        REFERENCES `grade_periods` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT `fk_grade_component`
        FOREIGN KEY (`grade_component_id`)
        REFERENCES `grade_components` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT `fk_grade_assigned_by`
        FOREIGN KEY (`assigned_by`)
        REFERENCES `professors` (`id`)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    CONSTRAINT `chk_grade_value`
        CHECK (`grade_value` BETWEEN 0 AND 5)
) ENGINE=InnoDB COMMENT='Individual grades (9 per subject enrollment)';

-- ============================================================================
-- 4. ATTENDANCE SYSTEM (ASISTENCIA)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Table: class_sessions (Sesiones de Clase)
-- Description: Stores information about specific class sessions
-- Business Rule: Attendance is associated with a subject session
-- ----------------------------------------------------------------------------
CREATE TABLE `class_sessions` (
    `id` BIGINT AUTO_INCREMENT,
    `subject_assignment_id` BIGINT NOT NULL,
    `session_date` DATE NOT NULL,
    `session_time` TIME NOT NULL,
    `duration_minutes` SMALLINT NOT NULL DEFAULT 120,
    `topic` VARCHAR(200) NULL COMMENT 'Class topic',
    `description` TEXT NULL COMMENT 'Session description',
    `status` ENUM('PROGRAMADA', 'REALIZADA', 'CANCELADA', 'REPROGRAMADA') NOT NULL DEFAULT 'PROGRAMADA',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_assignment_datetime` (`subject_assignment_id`, `session_date`, `session_time`),
    INDEX `idx_session_assignment` (`subject_assignment_id`),
    INDEX `idx_session_date` (`session_date`),
    INDEX `idx_session_status` (`status`),
    CONSTRAINT `fk_session_assignment`
        FOREIGN KEY (`subject_assignment_id`)
        REFERENCES `subject_assignments` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Class sessions for subjects';

-- ----------------------------------------------------------------------------
-- Table: attendance (Asistencia)
-- Description: Stores attendance records per student per session
-- Business Rule: Track assignment date, update date, and excused absences
--                Excused absences are not counted in total absences
-- ----------------------------------------------------------------------------
CREATE TABLE `attendance` (
    `id` BIGINT AUTO_INCREMENT,
    `class_session_id` BIGINT NOT NULL,
    `subject_enrollment_id` BIGINT NOT NULL,
    `status` ENUM('PRESENTE', 'AUSENTE', 'TARDANZA', 'EXCUSADO') NOT NULL DEFAULT 'AUSENTE',
    `assignment_date` DATE NOT NULL COMMENT 'Date when attendance was recorded',
    `update_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_excused` BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'TRUE if absence is excused',
    `excuse_reason` TEXT NULL COMMENT 'Reason for excused absence',
    `excuse_document` VARCHAR(200) NULL COMMENT 'Path to excuse document',
    `notes` TEXT NULL COMMENT 'Additional notes',
    `recorded_by` BIGINT NULL COMMENT 'Professor who recorded attendance',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_enrollment` (`class_session_id`, `subject_enrollment_id`),
    INDEX `idx_attendance_session` (`class_session_id`),
    INDEX `idx_attendance_enrollment` (`subject_enrollment_id`),
    INDEX `idx_attendance_status` (`status`),
    INDEX `idx_attendance_excused` (`is_excused`),
    INDEX `idx_attendance_assignment_date` (`assignment_date`),
    CONSTRAINT `fk_attendance_session`
        FOREIGN KEY (`class_session_id`)
        REFERENCES `class_sessions` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_attendance_enrollment`
        FOREIGN KEY (`subject_enrollment_id`)
        REFERENCES `subject_enrollments` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_attendance_recorded_by`
        FOREIGN KEY (`recorded_by`)
        REFERENCES `professors` (`id`)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Student attendance per class session';

-- ============================================================================
-- 5. INITIAL DATA INSERTION
-- ============================================================================

-- Insert Grade Periods (3 main periods)
INSERT INTO `grade_periods` (`name`, `period_number`, `weight_percentage`, `description`) VALUES
('Periodo 1', 1, 33.33, 'Primer periodo de calificación'),
('Periodo 2', 2, 33.33, 'Segundo periodo de calificación'),
('Periodo 3', 3, 33.34, 'Tercer periodo de calificación');

-- Insert Grade Components (Knowledge, Development, Product)
INSERT INTO `grade_components` (`name`, `code`, `weight_percentage`, `description`) VALUES
('Conocimiento', 'CON', 33.33, 'Evaluación de conocimientos teóricos'),
('Desarrollo', 'DES', 33.33, 'Evaluación del proceso de desarrollo'),
('Producto', 'PRO', 33.34, 'Evaluación del producto final');

-- Insert Courses (3 required courses)
INSERT INTO `courses` (`name`, `code`, `description`, `total_levels`) VALUES
('Desarrollo de Software', 'DS', 'Programa de formación en desarrollo de software', 3),
('Diseño Grafico', 'DG', 'Programa de formación en diseño gráfico', 3),
('Auxiliar Administrativo', 'AA', 'Programa de formación en gestión administrativa', 3);

-- Insert Levels for each Course (3 levels per course = 9 total)
-- Desarrollo de Software
INSERT INTO `levels` (`course_id`, `level_number`, `name`, `description`) VALUES
(1, 1, 'Nivel 1 - DS', 'Primer nivel de Desarrollo de Software'),
(1, 2, 'Nivel 2 - DS', 'Segundo nivel de Desarrollo de Software'),
(1, 3, 'Nivel 3 - DS', 'Tercer nivel de Desarrollo de Software');

-- Diseño Grafico
INSERT INTO `levels` (`course_id`, `level_number`, `name`, `description`) VALUES
(2, 1, 'Nivel 1 - DG', 'Primer nivel de Diseño Gráfico'),
(2, 2, 'Nivel 2 - DG', 'Segundo nivel de Diseño Gráfico'),
(2, 3, 'Nivel 3 - DG', 'Tercer nivel de Diseño Gráfico');

-- Auxiliar Administrativo
INSERT INTO `levels` (`course_id`, `level_number`, `name`, `description`) VALUES
(3, 1, 'Nivel 1 - AA', 'Primer nivel de Auxiliar Administrativo'),
(3, 2, 'Nivel 2 - AA', 'Segundo nivel de Auxiliar Administrativo'),
(3, 3, 'Nivel 3 - AA', 'Tercer nivel de Auxiliar Administrativo');

-- Insert Sample Subjects (3 per level, 9 per course)
-- DESARROLLO DE SOFTWARE - Nivel 1
INSERT INTO `subjects` (`course_id`, `level_id`, `name`, `code`, `description`, `hours_per_week`, `credits`) VALUES
(1, 1, 'Fundamentos de Programación - DS', 'DS-N1-FP', 'Introducción a la programación', 6, 3.0),
(1, 1, 'Inglés Técnico I - DS', 'DS-N1-IT1', 'Inglés técnico nivel básico', 4, 2.0),
(1, 1, 'Matemáticas Aplicadas - DS', 'DS-N1-MA', 'Matemáticas para desarrollo de software', 4, 2.0);

-- DESARROLLO DE SOFTWARE - Nivel 2
INSERT INTO `subjects` (`course_id`, `level_id`, `name`, `code`, `description`, `hours_per_week`, `credits`) VALUES
(1, 2, 'Programación Orientada a Objetos - DS', 'DS-N2-POO', 'POO y diseño de software', 6, 3.0),
(1, 2, 'Base de Datos - DS', 'DS-N2-BD', 'Diseño y gestión de bases de datos', 6, 3.0),
(1, 2, 'Inglés Técnico II - DS', 'DS-N2-IT2', 'Inglés técnico nivel intermedio', 4, 2.0);

-- DESARROLLO DE SOFTWARE - Nivel 3
INSERT INTO `subjects` (`course_id`, `level_id`, `name`, `code`, `description`, `hours_per_week`, `credits`) VALUES
(1, 3, 'Desarrollo Web - DS', 'DS-N3-DW', 'Desarrollo de aplicaciones web', 8, 4.0),
(1, 3, 'Metodologías Ágiles - DS', 'DS-N3-MA', 'Scrum y metodologías ágiles', 4, 2.0),
(1, 3, 'Proyecto Final - DS', 'DS-N3-PF', 'Proyecto integrador final', 8, 4.0);

-- DISEÑO GRÁFICO - Nivel 1
INSERT INTO `subjects` (`course_id`, `level_id`, `name`, `code`, `description`, `hours_per_week`, `credits`) VALUES
(2, 4, 'Fundamentos del Diseño - DG', 'DG-N1-FD', 'Principios básicos del diseño', 6, 3.0),
(2, 4, 'Teoría del Color - DG', 'DG-N1-TC', 'Psicología y teoría del color', 4, 2.0),
(2, 4, 'Dibujo Artístico - DG', 'DG-N1-DA', 'Técnicas de dibujo y composición', 6, 3.0);

-- DISEÑO GRÁFICO - Nivel 2
INSERT INTO `subjects` (`course_id`, `level_id`, `name`, `code`, `description`, `hours_per_week`, `credits`) VALUES
(2, 5, 'Diseño Digital - DG', 'DG-N2-DD', 'Adobe Photoshop e Illustrator', 8, 4.0),
(2, 5, 'Tipografía - DG', 'DG-N2-TIP', 'Diseño tipográfico y lettering', 4, 2.0),
(2, 5, 'Fotografía Digital - DG', 'DG-N2-FD', 'Técnicas de fotografía digital', 4, 2.0);

-- DISEÑO GRÁFICO - Nivel 3
INSERT INTO `subjects` (`course_id`, `level_id`, `name`, `code`, `description`, `hours_per_week`, `credits`) VALUES
(2, 6, 'Diseño Publicitario - DG', 'DG-N3-DP', 'Publicidad y branding', 6, 3.0),
(2, 6, 'Motion Graphics - DG', 'DG-N3-MG', 'Animación y efectos visuales', 6, 3.0),
(2, 6, 'Portafolio Profesional - DG', 'DG-N3-PP', 'Desarrollo de portafolio', 6, 3.0);

-- AUXILIAR ADMINISTRATIVO - Nivel 1
INSERT INTO `subjects` (`course_id`, `level_id`, `name`, `code`, `description`, `hours_per_week`, `credits`) VALUES
(3, 7, 'Fundamentos de Administración - AA', 'AA-N1-FA', 'Principios administrativos básicos', 6, 3.0),
(3, 7, 'Ofimática I - AA', 'AA-N1-OF1', 'Word y Excel básico', 6, 3.0),
(3, 7, 'Atención al Cliente - AA', 'AA-N1-AC', 'Servicio al cliente y comunicación', 4, 2.0);

-- AUXILIAR ADMINISTRATIVO - Nivel 2
INSERT INTO `subjects` (`course_id`, `level_id`, `name`, `code`, `description`, `hours_per_week`, `credits`) VALUES
(3, 8, 'Contabilidad Básica - AA', 'AA-N2-CB', 'Fundamentos de contabilidad', 6, 3.0),
(3, 8, 'Ofimática II - AA', 'AA-N2-OF2', 'Excel avanzado y PowerPoint', 6, 3.0),
(3, 8, 'Gestión Documental - AA', 'AA-N2-GD', 'Archivo y gestión de documentos', 4, 2.0);

-- AUXILIAR ADMINISTRATIVO - Nivel 3
INSERT INTO `subjects` (`course_id`, `level_id`, `name`, `code`, `description`, `hours_per_week`, `credits`) VALUES
(3, 9, 'Talento Humano - AA', 'AA-N3-TH', 'Gestión del talento humano', 6, 3.0),
(3, 9, 'Legislación Laboral - AA', 'AA-N3-LL', 'Normativa laboral colombiana', 4, 2.0),
(3, 9, 'Práctica Empresarial - AA', 'AA-N3-PE', 'Práctica en empresa', 8, 4.0);

-- Insert Current Academic Period (2026-1)
INSERT INTO `academic_periods` (`year`, `period_number`, `name`, `start_date`, `end_date`, `is_active`) VALUES
(2026, 1, '2026-1', '2026-01-08', '2026-06-30', TRUE);

-- ============================================================================
-- 6. VIEWS FOR EASY DATA ACCESS
-- ============================================================================

-- View: Complete student information with current status
CREATE OR REPLACE VIEW `v_students_complete` AS
SELECT
    s.id,
    s.identification_type,
    s.identification_number,
    CONCAT(s.first_name, ' ', s.last_name) AS full_name,
    s.email,
    s.mobile,
    s.enrollment_date,
    s.is_active,
    GROUP_CONCAT(DISTINCT CONCAT(c.name, ' (', ce.enrollment_status, ')') SEPARATOR ', ') AS courses_enrolled
FROM students s
LEFT JOIN course_enrollments ce ON s.id = ce.student_id
LEFT JOIN courses c ON ce.course_id = c.id
GROUP BY s.id, s.identification_type, s.identification_number, s.first_name, s.last_name,
         s.email, s.mobile, s.enrollment_date, s.is_active;

-- View: Subject enrollments with grades summary
CREATE OR REPLACE VIEW `v_subject_grades_summary` AS
SELECT
    se.id AS subject_enrollment_id,
    st.identification_number AS student_id,
    CONCAT(st.first_name, ' ', st.last_name) AS student_name,
    c.name AS course_name,
    l.name AS level_name,
    sub.name AS subject_name,
    CONCAT(p.first_name, ' ', p.last_name) AS professor_name,
    ap.name AS academic_period,
    se.status AS enrollment_status,
    se.final_grade,
    COUNT(DISTINCT g.id) AS total_grades_recorded,
    AVG(g.grade_value) AS average_grade
FROM subject_enrollments se
INNER JOIN level_enrollments le ON se.level_enrollment_id = le.id
INNER JOIN course_enrollments ce ON le.course_enrollment_id = ce.id
INNER JOIN students st ON ce.student_id = st.id
INNER JOIN levels l ON le.level_id = l.id
INNER JOIN courses c ON l.course_id = c.id
INNER JOIN subject_assignments sa ON se.subject_assignment_id = sa.id
INNER JOIN subjects sub ON sa.subject_id = sub.id
INNER JOIN professors p ON sa.professor_id = p.id
INNER JOIN academic_periods ap ON le.academic_period_id = ap.id
LEFT JOIN grades g ON se.id = g.subject_enrollment_id
GROUP BY se.id, st.identification_number, st.first_name, st.last_name, c.name,
         l.name, sub.name, p.first_name, p.last_name, ap.name, se.status, se.final_grade;

-- View: Attendance summary per student per subject
CREATE OR REPLACE VIEW `v_attendance_summary` AS
SELECT
    se.id AS subject_enrollment_id,
    st.identification_number AS student_id,
    CONCAT(st.first_name, ' ', st.last_name) AS student_name,
    sub.name AS subject_name,
    ap.name AS academic_period,
    COUNT(a.id) AS total_sessions,
    SUM(CASE WHEN a.status = 'Presente' THEN 1 ELSE 0 END) AS present_count,
    SUM(CASE WHEN a.status = 'Ausente' AND a.is_excused = FALSE THEN 1 ELSE 0 END) AS unexcused_absences,
    SUM(CASE WHEN a.status = 'Ausente' AND a.is_excused = TRUE THEN 1 ELSE 0 END) AS excused_absences,
    SUM(CASE WHEN a.status = 'Tardanza' THEN 1 ELSE 0 END) AS late_count,
    ROUND((SUM(CASE WHEN a.status = 'Presente' THEN 1 ELSE 0 END) * 100.0 / COUNT(a.id)), 2) AS attendance_percentage
FROM subject_enrollments se
INNER JOIN level_enrollments le ON se.level_enrollment_id = le.id
INNER JOIN course_enrollments ce ON le.course_enrollment_id = ce.id
INNER JOIN students st ON ce.student_id = st.id
INNER JOIN subject_assignments sa ON se.subject_assignment_id = sa.id
INNER JOIN subjects sub ON sa.subject_id = sub.id
INNER JOIN academic_periods ap ON le.academic_period_id = ap.id
LEFT JOIN attendance a ON se.id = a.subject_enrollment_id
GROUP BY se.id, st.identification_number, st.first_name, st.last_name,
         sub.name, ap.name;

-- View: Detailed grades breakdown (9 sub-notes per subject)
CREATE OR REPLACE VIEW `v_grades_detail` AS
SELECT
    g.id AS grade_id,
    st.identification_number AS student_id,
    CONCAT(st.first_name, ' ', st.last_name) AS student_name,
    sub.name AS subject_name,
    ap.name AS academic_period,
    gp.name AS grade_period,
    gc.name AS grade_component,
    g.grade_value,
    g.assignment_date,
    CONCAT(prof.first_name, ' ', prof.last_name) AS assigned_by_professor
FROM grades g
INNER JOIN subject_enrollments se ON g.subject_enrollment_id = se.id
INNER JOIN grade_periods gp ON g.grade_period_id = gp.id
INNER JOIN grade_components gc ON g.grade_component_id = gc.id
INNER JOIN level_enrollments le ON se.level_enrollment_id = le.id
INNER JOIN course_enrollments ce ON le.course_enrollment_id = ce.id
INNER JOIN students st ON ce.student_id = st.id
INNER JOIN subject_assignments sa ON se.subject_assignment_id = sa.id
INNER JOIN subjects sub ON sa.subject_id = sub.id
INNER JOIN academic_periods ap ON le.academic_period_id = ap.id
LEFT JOIN professors prof ON g.assigned_by = prof.id;

-- ============================================================================
-- 7. USEFUL STORED PROCEDURES
-- ============================================================================

-- Procedure: Calculate final grade for a subject enrollment
DELIMITER //

CREATE PROCEDURE `sp_calculate_subject_final_grade`(
    IN p_subject_enrollment_id BIGINT
)
BEGIN
    DECLARE v_final_grade DECIMAL(4,2);

    -- Calculate average of all 9 grades
    SELECT AVG(grade_value) INTO v_final_grade
    FROM grades
    WHERE subject_enrollment_id = p_subject_enrollment_id;

    -- Update the subject enrollment with the calculated final grade
    UPDATE subject_enrollments
    SET final_grade = v_final_grade,
        status = CASE
            WHEN v_final_grade >= 3.0 THEN 'Aprobado'
            WHEN v_final_grade < 3.0 THEN 'Reprobado'
            ELSE status
        END
    WHERE id = p_subject_enrollment_id;

    SELECT v_final_grade AS final_grade;
END //

-- Procedure: Get student complete academic history
CREATE PROCEDURE `sp_get_student_history`(
    IN p_student_id BIGINT
)
BEGIN
    -- Student basic info
    SELECT * FROM students WHERE id = p_student_id;

    -- Course enrollments
    SELECT
        ce.id,
        c.name AS course_name,
        ap.name AS period,
        ce.enrollment_status,
        ce.enrollment_date,
        ce.completion_date
    FROM course_enrollments ce
    INNER JOIN courses c ON ce.course_id = c.id
    INNER JOIN academic_periods ap ON ce.academic_period_id = ap.id
    WHERE ce.student_id = p_student_id
    ORDER BY ce.enrollment_date;

    -- Grades per subject
    SELECT * FROM v_subject_grades_summary
    WHERE student_id = (SELECT identification_number FROM students WHERE id = p_student_id)
    ORDER BY academic_period, subject_name;

    -- Attendance summary
    SELECT * FROM v_attendance_summary
    WHERE student_id = (SELECT identification_number FROM students WHERE id = p_student_id)
    ORDER BY academic_period, subject_name;
END //

-- Procedure: Check if student can enroll in new course
CREATE PROCEDURE `sp_check_enrollment_eligibility`(
    IN p_student_id BIGINT,
    IN p_course_id BIGINT,
    IN p_period_id BIGINT,
    OUT p_can_enroll BOOLEAN,
    OUT p_message VARCHAR(200)
)
BEGIN
    DECLARE v_active_enrollments INT;
    DECLARE v_same_period_enrollments INT;

    -- Check if student has active enrollments in the same period
    SELECT COUNT(*) INTO v_same_period_enrollments
    FROM course_enrollments
    WHERE student_id = p_student_id
    AND academic_period_id = p_period_id;

    IF v_same_period_enrollments > 0 THEN
        SET p_can_enroll = FALSE;
        SET p_message = 'El estudiante ya está inscrito en un curso en este periodo';
    ELSE
        SET p_can_enroll = TRUE;
        SET p_message = 'El estudiante puede inscribirse';
    END IF;
END //

DELIMITER ;

-- ============================================================================
-- 8. USER CREATION AND PERMISSIONS
-- ============================================================================

-- Create user if not exists
CREATE USER IF NOT EXISTS 'cesde_user'@'localhost' IDENTIFIED BY '*Lagp2026*';
CREATE USER IF NOT EXISTS 'cesde_user'@'%' IDENTIFIED BY '*Lagp2026*';

-- Grant all privileges on the database
GRANT ALL PRIVILEGES ON `bd-2026-1-cesde`.* TO 'cesde_user'@'localhost';
GRANT ALL PRIVILEGES ON `bd-2026-1-cesde`.* TO 'cesde_user'@'%';

-- Apply changes
FLUSH PRIVILEGES;

-- ============================================================================
-- END OF SCHEMA
-- ============================================================================

SELECT 'Database schema created successfully!' AS message;
SELECT 'Database: bd-2026-1-cesde' AS database_name;
SELECT 'User: cesde_user' AS user_name;
SELECT 'Total tables created: 19' AS tables_count;
SELECT 'Total views created: 4' AS views_count;
SELECT 'Total stored procedures: 3' AS procedures_count;

-- ============================================================================
-- MODIFICACIÓN: AGREGAR GRUPOS A LA ESTRUCTURA DE LA BASE DE DATOS
-- ============================================================================
-- Fecha: 2026-01-08
-- Propósito: Permitir que un curso tenga múltiples grupos por nivel y período
-- Pasos incluidos:
--   1. CREATE TABLE course_groups
--   2. ALTER TABLE level_enrollments
--   3. ALTER TABLE subject_assignments
--   4. INSERT datos de ejemplo
-- ============================================================================

USE `bd-2026-1-cesde`;

-- ============================================================================
-- PASO 1: CREAR NUEVA TABLA course_groups (Grupos de Curso)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Table: course_groups (Grupos por Curso/Nivel/Período)
-- Description: Almacena los diferentes grupos que existen para cada nivel en cada período
-- Business Rule: Un nivel puede tener múltiples grupos en el mismo período
--                Cada grupo tiene un código único dentro del nivel-período
-- ----------------------------------------------------------------------------
CREATE TABLE `course_groups` (
    `id` BIGINT AUTO_INCREMENT,
    `course_id` BIGINT NOT NULL,
    `level_id` BIGINT NOT NULL,
    `academic_period_id` BIGINT NOT NULL,
    `group_code` VARCHAR(20) NOT NULL COMMENT 'Group code (e.g., A, B, C, 01, 02)',
    `group_name` VARCHAR(100) NOT NULL COMMENT 'Group name (e.g., Grupo A, Grupo Mañana)',
    `max_students` TINYINT NULL COMMENT 'Maximum students allowed in this group',
    `current_students` TINYINT NOT NULL DEFAULT 0 COMMENT 'Current number of enrolled students',
    `schedule_shift` ENUM('Mañana', 'Tarde', 'Noche', 'Mixto') NULL COMMENT 'Time shift for this group',
    `description` TEXT NULL COMMENT 'Additional group description',
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_course_level_period_code` (`course_id`, `level_id`, `academic_period_id`, `group_code`),
    INDEX `idx_group_course` (`course_id`),
    INDEX `idx_group_level` (`level_id`),
    INDEX `idx_group_period` (`academic_period_id`),
    INDEX `idx_group_active` (`is_active`),
    INDEX `idx_group_shift` (`schedule_shift`),
    CONSTRAINT `fk_group_course`
        FOREIGN KEY (`course_id`)
        REFERENCES `courses` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT `fk_group_level`
        FOREIGN KEY (`level_id`)
        REFERENCES `levels` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT `fk_group_period`
        FOREIGN KEY (`academic_period_id`)
        REFERENCES `academic_periods` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Course groups per level and period';

-- ============================================================================
-- PASO 2: MODIFICAR TABLAS EXISTENTES
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Modificación 1: level_enrollments
-- Agregar columna group_id para asignar estudiantes a grupos específicos
-- ----------------------------------------------------------------------------
ALTER TABLE `level_enrollments`
ADD COLUMN `group_id` BIGINT NULL COMMENT 'Group assigned to student' AFTER `academic_period_id`,
ADD INDEX `idx_level_enrollment_group` (`group_id`),
ADD CONSTRAINT `fk_level_enrollment_group`
    FOREIGN KEY (`group_id`)
    REFERENCES `course_groups` (`id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- ----------------------------------------------------------------------------
-- Modificación 2: subject_assignments
-- Agregar columna group_id para asignar materias a grupos específicos
-- Esto permite que un profesor tenga diferentes grupos de la misma materia
-- ----------------------------------------------------------------------------
ALTER TABLE `subject_assignments`
ADD COLUMN `group_id` BIGINT NULL COMMENT 'Specific group for this assignment' AFTER `academic_period_id`,
ADD INDEX `idx_assignment_group` (`group_id`),
ADD CONSTRAINT `fk_assignment_group`
    FOREIGN KEY (`group_id`)
    REFERENCES `course_groups` (`id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- Modificar unique key para permitir mismo profesor-materia-período pero diferente grupo
ALTER TABLE `subject_assignments`
DROP INDEX `uk_subject_professor_period`,
ADD UNIQUE KEY `uk_subject_professor_period_group` (`subject_id`, `professor_id`, `academic_period_id`, `group_id`);

-- ============================================================================
-- PASO 3: INSERTAR DATOS DE EJEMPLO - GRUPOS
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Grupos para DESARROLLO DE SOFTWARE (Course ID = 1)
-- ----------------------------------------------------------------------------

-- Grupos para Desarrollo de Software - Nivel 1 (Level ID = 1)
INSERT INTO `course_groups` (`course_id`, `level_id`, `academic_period_id`, `group_code`, `group_name`, `max_students`, `schedule_shift`, `description`) VALUES
(1, 1, 1, 'A', 'Grupo A - Mañana', 30, 'Mañana', 'Grupo matutino de Nivel 1 - Desarrollo de Software'),
(1, 1, 1, 'B', 'Grupo B - Tarde', 30, 'Tarde', 'Grupo vespertino de Nivel 1 - Desarrollo de Software'),
(1, 1, 1, 'C', 'Grupo C - Noche', 25, 'Noche', 'Grupo nocturno de Nivel 1 - Desarrollo de Software');

-- Grupos para Desarrollo de Software - Nivel 2 (Level ID = 2)
INSERT INTO `course_groups` (`course_id`, `level_id`, `academic_period_id`, `group_code`, `group_name`, `max_students`, `schedule_shift`, `description`) VALUES
(1, 2, 1, 'A', 'Grupo A - Mañana', 30, 'Mañana', 'Grupo matutino de Nivel 2 - Desarrollo de Software'),
(1, 2, 1, 'B', 'Grupo B - Tarde', 30, 'Tarde', 'Grupo vespertino de Nivel 2 - Desarrollo de Software');

-- Grupos para Desarrollo de Software - Nivel 3 (Level ID = 3)
INSERT INTO `course_groups` (`course_id`, `level_id`, `academic_period_id`, `group_code`, `group_name`, `max_students`, `schedule_shift`, `description`) VALUES
(1, 3, 1, 'A', 'Grupo A - Mixto', 35, 'Mixto', 'Grupo mixto de Nivel 3 - Desarrollo de Software');

-- ----------------------------------------------------------------------------
-- Grupos para DISEÑO GRÁFICO (Course ID = 2)
-- ----------------------------------------------------------------------------

-- Grupos para Diseño Gráfico - Nivel 1 (Level ID = 4)
INSERT INTO `course_groups` (`course_id`, `level_id`, `academic_period_id`, `group_code`, `group_name`, `max_students`, `schedule_shift`, `description`) VALUES
(2, 4, 1, 'A', 'Grupo A - Mañana', 25, 'Mañana', 'Grupo matutino de Nivel 1 - Diseño Gráfico'),
(2, 4, 1, 'B', 'Grupo B - Noche', 25, 'Noche', 'Grupo nocturno de Nivel 1 - Diseño Gráfico');

-- Grupos para Diseño Gráfico - Nivel 2 (Level ID = 5)
INSERT INTO `course_groups` (`course_id`, `level_id`, `academic_period_id`, `group_code`, `group_name`, `max_students`, `schedule_shift`, `description`) VALUES
(2, 5, 1, 'A', 'Grupo A - Tarde', 25, 'Tarde', 'Grupo vespertino de Nivel 2 - Diseño Gráfico');

-- Grupos para Diseño Gráfico - Nivel 3 (Level ID = 6)
INSERT INTO `course_groups` (`course_id`, `level_id`, `academic_period_id`, `group_code`, `group_name`, `max_students`, `schedule_shift`, `description`) VALUES
(2, 6, 1, 'A', 'Grupo A - Mañana', 20, 'Mañana', 'Grupo matutino de Nivel 3 - Diseño Gráfico');

-- ----------------------------------------------------------------------------
-- Grupos para AUXILIAR ADMINISTRATIVO (Course ID = 3)
-- ----------------------------------------------------------------------------

-- Grupos para Auxiliar Administrativo - Nivel 1 (Level ID = 7)
INSERT INTO `course_groups` (`course_id`, `level_id`, `academic_period_id`, `group_code`, `group_name`, `max_students`, `schedule_shift`, `description`) VALUES
(3, 7, 1, 'A', 'Grupo A - Tarde', 35, 'Tarde', 'Grupo vespertino de Nivel 1 - Auxiliar Administrativo'),
(3, 7, 1, 'B', 'Grupo B - Noche', 35, 'Noche', 'Grupo nocturno de Nivel 1 - Auxiliar Administrativo');

-- Grupos para Auxiliar Administrativo - Nivel 2 (Level ID = 8)
INSERT INTO `course_groups` (`course_id`, `level_id`, `academic_period_id`, `group_code`, `group_name`, `max_students`, `schedule_shift`, `description`) VALUES
(3, 8, 1, 'A', 'Grupo A - Mañana', 30, 'Mañana', 'Grupo matutino de Nivel 2 - Auxiliar Administrativo'),
(3, 8, 1, 'B', 'Grupo B - Noche', 30, 'Noche', 'Grupo nocturno de Nivel 2 - Auxiliar Administrativo');

-- Grupos para Auxiliar Administrativo - Nivel 3 (Level ID = 9)
INSERT INTO `course_groups` (`course_id`, `level_id`, `academic_period_id`, `group_code`, `group_name`, `max_students`, `schedule_shift`, `description`) VALUES
(3, 9, 1, 'A', 'Grupo A - Tarde', 30, 'Tarde', 'Grupo vespertino de Nivel 3 - Auxiliar Administrativo');

-- ============================================================================
-- CONSULTAS DE VERIFICACIÓN
-- ============================================================================

-- Ver todos los grupos creados
SELECT
    cg.id,
    c.code AS course_code,
    c.name AS course_name,
    l.level_number,
    l.name AS level_name,
    cg.group_code,
    cg.group_name,
    cg.schedule_shift,
    cg.max_students,
    cg.current_students,
    cg.is_active
FROM course_groups cg
INNER JOIN courses c ON cg.course_id = c.id
INNER JOIN levels l ON cg.level_id = l.id
ORDER BY c.id, l.level_number, cg.group_code;

-- Ver resumen de grupos por curso
SELECT
    c.name AS course_name,
    COUNT(cg.id) AS total_groups,
    SUM(cg.max_students) AS total_capacity,
    SUM(cg.current_students) AS total_enrolled
FROM courses c
INNER JOIN course_groups cg ON c.id = cg.course_id
WHERE cg.is_active = TRUE
GROUP BY c.id, c.name
ORDER BY c.name;

-- Ver grupos con espacio disponible
SELECT
    c.name AS course_name,
    l.name AS level_name,
    cg.group_code,
    cg.group_name,
    cg.schedule_shift,
    cg.max_students,
    cg.current_students,
    (cg.max_students - cg.current_students) AS available_spaces,
    ROUND((cg.current_students * 100.0 / cg.max_students), 2) AS occupancy_percentage
FROM course_groups cg
INNER JOIN courses c ON cg.course_id = c.id
INNER JOIN levels l ON cg.level_id = l.id
WHERE cg.max_students > cg.current_students
AND cg.is_active = TRUE
ORDER BY c.name, l.level_number, cg.group_code;

-- ============================================================================
-- USUARIOS Y ROLES (Sistema de Autenticación)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Table: roles
-- Description: Catálogo de roles del sistema
-- Business Rule: Administrador, Administrativo, Estudiante, Profesor
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `roles` (
  `id` BIGINT AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `description` VARCHAR(255) NULL,
  `enabled` BOOLEAN NOT NULL DEFAULT TRUE,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_name` (`name`),
  INDEX `idx_role_enabled` (`enabled`)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT='Catálogo de roles: Administrador, Administrativo, Estudiante, Profesor';

-- ----------------------------------------------------------------------------
-- Table: users
-- Description: Cuentas de acceso al sistema
-- Business Rule: Pueden vincularse opcionalmente a estudiante o profesor
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT AUTO_INCREMENT,
  `username` VARCHAR(100) NOT NULL,
  `password` VARCHAR(255) NOT NULL COMMENT 'Hash BCrypt, no texto plano',
  `email` VARCHAR(150) NULL,
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
  `student_id` BIGINT NULL COMMENT 'Opcional: vincular a students',
  `professor_id` BIGINT NULL COMMENT 'Opcional: vincular a professors',
  `last_login_at` TIMESTAMP NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`),
  UNIQUE KEY `uk_user_email` (`email`),
  INDEX `idx_user_active` (`is_active`),
  INDEX `idx_user_student` (`student_id`),
  INDEX `idx_user_professor` (`professor_id`),
  CONSTRAINT `fk_user_student`
    FOREIGN KEY (`student_id`) REFERENCES `students`(`id`)
    ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_user_professor`
    FOREIGN KEY (`professor_id`) REFERENCES `professors`(`id`)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT='Cuentas de acceso al sistema (pueden vincularse a estudiante/profesor)';

-- ----------------------------------------------------------------------------
-- Table: user_roles
-- Description: Relación N:M entre usuarios y roles
-- Business Rule: Un usuario puede tener múltiples roles
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_roles` (
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  `assigned_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `assigned_by_user_id` BIGINT NULL COMMENT 'Opcional: quién asignó el rol',
  PRIMARY KEY (`user_id`, `role_id`),
  INDEX `idx_ur_role` (`role_id`),
  INDEX `idx_ur_assigned_by` (`assigned_by_user_id`),
  CONSTRAINT `fk_ur_user`
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ur_role`
    FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_ur_assigned_by`
    FOREIGN KEY (`assigned_by_user_id`) REFERENCES `users`(`id`)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT='Relación de roles por usuario (N:M)';

-- ============================================================================
-- DATOS SEMILLA: ROLES Y USUARIOS INICIALES
-- ============================================================================

-- Insertar roles base (idempotente)
INSERT INTO `roles` (`name`, `description`)
SELECT * FROM (SELECT 'Administrador' AS n, 'Acceso total al sistema' AS d) AS t
WHERE NOT EXISTS (SELECT 1 FROM `roles` r WHERE r.`name` = 'Administrador');

INSERT INTO `roles` (`name`, `description`)
SELECT * FROM (SELECT 'Administrativo' AS n, 'Gestión académica/operativa' AS d) AS t
WHERE NOT EXISTS (SELECT 1 FROM `roles` r WHERE r.`name` = 'Administrativo');

INSERT INTO `roles` (`name`, `description`)
SELECT * FROM (SELECT 'Estudiante' AS n, 'Acceso de estudiante' AS d) AS t
WHERE NOT EXISTS (SELECT 1 FROM `roles` r WHERE r.`name` = 'Estudiante');

INSERT INTO `roles` (`name`, `description`)
SELECT * FROM (SELECT 'Profesor' AS n, 'Acceso de profesor' AS d) AS t
WHERE NOT EXISTS (SELECT 1 FROM `roles` r WHERE r.`name` = 'Profesor');

-- Insertar usuario administrador inicial (password: Lagp2022, BCrypt hash)
-- Hash generado con BCryptPasswordEncoder de Spring Security (cost factor 10)
INSERT INTO `users` (`username`, `password`, `email`, `is_active`)
SELECT * FROM (
  SELECT
    'admin' AS username,
    '$2a$10$inrseundfnAX.P4Ra/jwm.uyoxuDhXEBcHQLjXhI8O6LYDST9KP8m' AS password,
    'admin@cesde.edu.co' AS email,
    TRUE AS is_active
) AS t
WHERE NOT EXISTS (SELECT 1 FROM `users` u WHERE u.`username` = 'admin');

-- Insertar usuario general inicial (password: Lagp2026, BCrypt hash)
INSERT INTO `users` (`username`, `password`, `email`, `is_active`)
SELECT * FROM (
  SELECT
    'usuario' AS username,
    '$2a$10$j/1/6Byzh.7q6rM53RrKD.JDoGO1tFKoUSK8DjDUsmzJqjrMGjIfy' AS password,
    'usuario@cesde.edu.co' AS email,
    TRUE AS is_active
) AS t
WHERE NOT EXISTS (SELECT 1 FROM `users` u WHERE u.`username` = 'usuario');

-- Asignar rol Administrador al usuario admin
INSERT INTO `user_roles` (`user_id`, `role_id`)
SELECT u.id, r.id
FROM `users` u, `roles` r
WHERE u.username = 'admin' AND r.name = 'Administrador'
AND NOT EXISTS (
  SELECT 1 FROM `user_roles` ur
  WHERE ur.user_id = u.id AND ur.role_id = r.id
);

-- Asignar rol Estudiante al usuario general
INSERT INTO `user_roles` (`user_id`, `role_id`)
SELECT u.id, r.id
FROM `users` u, `roles` r
WHERE u.username = 'usuario' AND r.name = 'Estudiante'
AND NOT EXISTS (
  SELECT 1 FROM `user_roles` ur
  WHERE ur.user_id = u.id AND ur.role_id = r.id
);

-- ============================================================================
-- RESUMEN DE MODIFICACIONES
-- ============================================================================

SELECT '✓ Tabla course_groups creada exitosamente' AS status;
SELECT '✓ Tabla level_enrollments modificada (columna group_id agregada)' AS status;
SELECT '✓ Tabla subject_assignments modificada (columna group_id agregada)' AS status;
SELECT '✓ Tabla roles creada exitosamente' AS status;
SELECT '✓ Tabla users creada exitosamente' AS status;
SELECT '✓ Tabla user_roles creada exitosamente' AS status;
SELECT '✓ Roles base insertados (4 roles)' AS status;
SELECT '✓ Usuarios iniciales creados (admin, usuario)' AS status;
SELECT COUNT(*) AS total_groups_created FROM course_groups;
SELECT COUNT(*) AS total_roles_created FROM roles;
SELECT COUNT(*) AS total_users_created FROM users;

-- ============================================================================
-- NOTAS DE IMPLEMENTACIÓN
-- ============================================================================
/*
SIGUIENTES PASOS (NO INCLUIDOS EN ESTE ARCHIVO):

1. TRIGGERS (para mantener contador current_students):
   - trg_level_enrollment_insert_update_group_count
   - trg_level_enrollment_update_group_count
   - trg_level_enrollment_delete_update_group_count

2. VISTAS (para consultas comunes):
   - v_groups_complete
   - v_students_by_group
   - v_subjects_by_group

3. STORED PROCEDURES (para gestión de grupos):
   - sp_create_group
   - sp_assign_student_to_group
   - sp_get_group_statistics

SISTEMA DE USUARIOS Y ROLES:
- Tabla `roles`: 4 roles predefinidos (Administrador, Administrativo, Estudiante, Profesor)
- Tabla `users`: Cuentas de acceso con vinculación opcional a students/professors
- Tabla `user_roles`: Relación N:M para asignar múltiples roles por usuario

USUARIOS INICIALES:
- admin / Lagp2022 (rol: Administrador) - Acceso total
- usuario / Lagp2026 (rol: Estudiante) - Acceso básico

NOTA: Los passwords están hasheados con BCrypt (factor de coste 10).
      Los triggers, vistas y procedimientos se implementarán posteriormente
      una vez confirmado que las modificaciones estructurales funcionan correctamente.
*/

-- ============================================================================
-- FIN DE MODIFICACIONES BÁSICAS
-- ============================================================================

