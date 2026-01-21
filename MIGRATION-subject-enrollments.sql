-- ============================================================================
-- MIGRACIÓN: Corrección de subject_enrollments
-- Fecha: 2026-01-20
-- Descripción: Cambiar de subject_assignment_id (con profesor obligatorio)
--              a subject_id (materia obligatoria) + subject_assignment_id (opcional)
-- ============================================================================

-- IMPORTANTE: Ejecutar en ambiente de desarrollo primero
-- Verificar el backup antes de ejecutar en producción

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

-- ----------------------------------------------------------------------------
-- PASO 1: Respaldar tabla actual
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `subject_enrollments_backup_20260120`;

CREATE TABLE `subject_enrollments_backup_20260120` AS
SELECT * FROM `subject_enrollments`;

SELECT 'BACKUP CREATED' as status, COUNT(*) as records
FROM `subject_enrollments_backup_20260120`;

-- ----------------------------------------------------------------------------
-- PASO 2: Eliminar constraints existentes
-- ----------------------------------------------------------------------------
ALTER TABLE `subject_enrollments`
    DROP FOREIGN KEY `fk_subject_enrollment_assignment`;

ALTER TABLE `subject_enrollments`
    DROP INDEX `uk_level_enrollment_subject`;

-- ----------------------------------------------------------------------------
-- PASO 3: Agregar nueva columna subject_id (temporalmente NULL)
-- ----------------------------------------------------------------------------
ALTER TABLE `subject_enrollments`
    ADD COLUMN `subject_id` BIGINT NULL
    COMMENT 'Direct reference to subject (mandatory for enrollment)'
    AFTER `level_enrollment_id`;

-- ----------------------------------------------------------------------------
-- PASO 4: Poblar subject_id desde subject_assignments
-- ----------------------------------------------------------------------------
UPDATE `subject_enrollments` se
INNER JOIN `subject_assignments` sa ON se.subject_assignment_id = sa.id
SET se.subject_id = sa.subject_id;

-- Verificar que todos los registros fueron actualizados
SELECT
    'DATA MIGRATION' as step,
    COUNT(*) as total_records,
    COUNT(subject_id) as records_with_subject_id,
    COUNT(*) - COUNT(subject_id) as records_missing_subject_id
FROM `subject_enrollments`;

-- Si hay registros sin subject_id, detener aquí
-- Verificar manualmente antes de continuar

-- ----------------------------------------------------------------------------
-- PASO 5: Hacer subject_id NOT NULL
-- ----------------------------------------------------------------------------
ALTER TABLE `subject_enrollments`
    MODIFY COLUMN `subject_id` BIGINT NOT NULL
    COMMENT 'Direct reference to subject (mandatory)';

-- ----------------------------------------------------------------------------
-- PASO 6: Hacer subject_assignment_id NULL (opcional)
-- ----------------------------------------------------------------------------
ALTER TABLE `subject_enrollments`
    MODIFY COLUMN `subject_assignment_id` BIGINT NULL
    COMMENT 'Reference to professor assignment (optional - for traceability)';

-- ----------------------------------------------------------------------------
-- PASO 7: Crear índice para subject_id
-- ----------------------------------------------------------------------------
ALTER TABLE `subject_enrollments`
    ADD INDEX `idx_subject_enrollment_subject` (`subject_id`);

-- ----------------------------------------------------------------------------
-- PASO 8: Recrear constraints con nueva estructura
-- ----------------------------------------------------------------------------

-- Constraint a subjects (OBLIGATORIO)
ALTER TABLE `subject_enrollments`
    ADD CONSTRAINT `fk_subject_enrollment_subject`
        FOREIGN KEY (`subject_id`)
        REFERENCES `subjects` (`id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE;

-- Constraint a subject_assignments (OPCIONAL - ON DELETE SET NULL)
ALTER TABLE `subject_enrollments`
    ADD CONSTRAINT `fk_subject_enrollment_assignment`
        FOREIGN KEY (`subject_assignment_id`)
        REFERENCES `subject_assignments` (`id`)
        ON DELETE SET NULL
        ON UPDATE CASCADE;

-- ----------------------------------------------------------------------------
-- PASO 9: Crear nuevo unique key (level_enrollment_id + subject_id)
-- ----------------------------------------------------------------------------
ALTER TABLE `subject_enrollments`
    ADD UNIQUE KEY `uk_level_enrollment_subject` (`level_enrollment_id`, `subject_id`);

-- ----------------------------------------------------------------------------
-- PASO 10: Verificación final
-- ----------------------------------------------------------------------------
SELECT 'MIGRATION COMPLETE' as status;

SELECT
    'VERIFICATION' as step,
    COUNT(*) as total_enrollments,
    COUNT(subject_id) as with_subject_id,
    COUNT(subject_assignment_id) as with_professor_assigned,
    COUNT(*) - COUNT(subject_assignment_id) as without_professor
FROM `subject_enrollments`;

-- Mostrar estructura actualizada
DESCRIBE `subject_enrollments`;

-- Verificar constraints
SELECT
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_NAME = 'subject_enrollments'
  AND TABLE_SCHEMA = DATABASE()
  AND REFERENCED_TABLE_NAME IS NOT NULL;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- ============================================================================
-- ROLLBACK (si es necesario)
-- ============================================================================
/*
-- Para revertir los cambios:

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS subject_enrollments;

CREATE TABLE subject_enrollments AS
SELECT * FROM subject_enrollments_backup_20260120;

-- Recrear estructura original (consultar BASEDATOS.sql)
-- Agregar constraints originales

SET FOREIGN_KEY_CHECKS=1;
*/

-- ============================================================================
-- FIN DE MIGRACIÓN
-- ============================================================================
