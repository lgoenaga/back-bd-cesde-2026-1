-- ============================================
-- Script para insertar datos de prueba de ClassSession
-- Fecha: 2026-01-21
-- ============================================

-- NOTA: Ajustar los IDs según los datos existentes en tu base de datos
-- Verificar primero qué subject_assignment_id existen:
-- SELECT id, subject_id, professor_id FROM subject_assignments WHERE is_active = true LIMIT 5;

-- Insertar sesiones de clase de prueba
INSERT INTO class_sessions (
    subject_assignment_id,
    session_date,
    session_time,
    duration_minutes,
    topic,
    description,
    status,
    created_at,
    updated_at
) VALUES
-- Sesión 1: Hoy
(1, CURDATE(), '08:00:00', 120, 'Introducción al curso', 'Primera clase del periodo académico', 'REALIZADA', NOW(), NOW()),

-- Sesión 2: Mañana
(1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '08:00:00', 120, 'Conceptos fundamentales', 'Revisión de conceptos básicos', 'PROGRAMADA', NOW(), NOW()),

-- Sesión 3: Pasado mañana
(1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '08:00:00', 120, 'Ejercicios prácticos', 'Práctica guiada en clase', 'PROGRAMADA', NOW(), NOW()),

-- Sesión 4: En 3 días
(1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '10:00:00', 90, 'Evaluación diagnóstica', 'Evaluación inicial de conocimientos', 'PROGRAMADA', NOW(), NOW()),

-- Sesión 5: En una semana
(1, DATE_ADD(CURDATE(), INTERVAL 7 DAY), '08:00:00', 120, 'Taller grupal', 'Trabajo colaborativo en equipos', 'PROGRAMADA', NOW(), NOW());

-- Verificar que se crearon correctamente
SELECT
    cs.id,
    cs.subject_assignment_id,
    cs.session_date,
    cs.session_time,
    cs.topic,
    cs.status,
    cs.created_at
FROM class_sessions cs
ORDER BY cs.session_date, cs.session_time;

-- Contar sesiones por estado
SELECT
    status,
    COUNT(*) as total
FROM class_sessions
GROUP BY status;

-- Ver sesiones con información de la asignación
SELECT
    cs.id,
    cs.session_date,
    cs.session_time,
    cs.topic,
    cs.status,
    s.name as subject_name,
    p.first_name as professor_first_name,
    p.last_name as professor_last_name
FROM class_sessions cs
JOIN subject_assignments sa ON cs.subject_assignment_id = sa.id
JOIN subjects s ON sa.subject_id = s.id
LEFT JOIN professors p ON sa.professor_id = p.id
ORDER BY cs.session_date, cs.session_time;
