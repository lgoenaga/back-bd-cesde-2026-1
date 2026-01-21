package com.cesde.studentinfo.service;

import com.cesde.studentinfo.exception.BusinessException;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.ClassSession;
import com.cesde.studentinfo.model.SubjectAssignment;
import com.cesde.studentinfo.repository.ClassSessionRepository;
import com.cesde.studentinfo.repository.SubjectAssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Service para gestión de sesiones de clase
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClassSessionService {

    private final ClassSessionRepository classSessionRepository;
    private final SubjectAssignmentRepository subjectAssignmentRepository;

    @Transactional(readOnly = true)
    public List<ClassSession> getAllSessions() {
        log.info("Fetching all class sessions");
        return classSessionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ClassSession> getSessionById(Long id) {
        log.info("Fetching class session by id: {}", id);
        return classSessionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<ClassSession> getSessionsBySubjectAssignment(Long assignmentId) {
        log.info("Fetching class sessions for subject assignment: {}", assignmentId);
        return classSessionRepository.findBySubjectAssignmentId(assignmentId);
    }

    @Transactional(readOnly = true)
    public List<ClassSession> getSessionsByDate(LocalDate date) {
        log.info("Fetching class sessions for date: {}", date);
        return classSessionRepository.findBySessionDate(date);
    }

    @Transactional(readOnly = true)
    public List<ClassSession> getSessionsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching class sessions between {} and {}", startDate, endDate);
        return classSessionRepository.findByDateRange(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public Optional<ClassSession> findBySubjectAssignmentAndDate(Long assignmentId, LocalDate date) {
        log.info("Searching for class session with assignment {} on date {}", assignmentId, date);
        return classSessionRepository.findBySubjectAssignmentIdAndSessionDate(assignmentId, date);
    }

    public ClassSession createSession(ClassSession session) {
        log.info("Creating class session for subject assignment: {} on date: {}",
                 session.getSubjectAssignment().getId(),
                 session.getSessionDate());

        // Validar que la asignación de materia exista
        if (session.getSubjectAssignment() == null || session.getSubjectAssignment().getId() == null) {
            throw new BusinessException("Subject assignment is required");
        }

        // Validar que no exista ya una sesión para la misma asignación en la misma fecha y hora
        Optional<ClassSession> existing = classSessionRepository.findBySubjectAssignmentIdAndSessionDate(
            session.getSubjectAssignment().getId(),
            session.getSessionDate()
        );

        if (existing.isPresent() && existing.get().getSessionTime().equals(session.getSessionTime())) {
            throw new BusinessException("A class session already exists for this assignment on this date and time");
        }

        // Establecer estado por defecto si no se especifica
        if (session.getStatus() == null) {
            session.setStatus(ClassSession.SessionStatus.PROGRAMADA);
        }

        ClassSession saved = classSessionRepository.save(session);
        log.info("Class session created successfully with id: {}", saved.getId());
        return saved;
    }

    public ClassSession findOrCreateSession(Long subjectAssignmentId, LocalDate sessionDate, LocalTime sessionTime, String topic) {
        log.info("Finding or creating class session for assignment {} on date {}", subjectAssignmentId, sessionDate);

        // Buscar sesión existente
        Optional<ClassSession> existing = classSessionRepository.findBySubjectAssignmentIdAndSessionDate(
            subjectAssignmentId,
            sessionDate
        );

        if (existing.isPresent()) {
            log.info("Class session already exists with id: {}", existing.get().getId());
            return existing.get();
        }

        // Si no existe, crear nueva
        SubjectAssignment assignment = subjectAssignmentRepository.findById(subjectAssignmentId)
            .orElseThrow(() -> new ResourceNotFoundException("SubjectAssignment", subjectAssignmentId));

        ClassSession newSession = ClassSession.builder()
            .subjectAssignment(assignment)
            .sessionDate(sessionDate)
            .sessionTime(sessionTime != null ? sessionTime : LocalTime.of(8, 0))
            .durationMinutes(120)
            .topic(topic != null ? topic : "Clase del " + sessionDate)
            .status(ClassSession.SessionStatus.PROGRAMADA)
            .build();

        ClassSession saved = classSessionRepository.save(newSession);
        log.info("New class session created with id: {}", saved.getId());
        return saved;
    }

    public ClassSession updateSession(Long id, ClassSession updates) {
        log.info("Updating class session: {}", id);

        ClassSession existing = classSessionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ClassSession", id));

        if (updates.getSessionDate() != null) {
            existing.setSessionDate(updates.getSessionDate());
        }

        if (updates.getSessionTime() != null) {
            existing.setSessionTime(updates.getSessionTime());
        }

        if (updates.getDurationMinutes() != null) {
            existing.setDurationMinutes(updates.getDurationMinutes());
        }

        if (updates.getTopic() != null) {
            existing.setTopic(updates.getTopic());
        }

        if (updates.getDescription() != null) {
            existing.setDescription(updates.getDescription());
        }

        if (updates.getStatus() != null) {
            existing.setStatus(updates.getStatus());
        }

        ClassSession saved = classSessionRepository.save(existing);
        log.info("Class session updated successfully");
        return saved;
    }

    public void deleteSession(Long id) {
        log.info("Deleting class session: {}", id);

        ClassSession session = classSessionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ClassSession", id));

        classSessionRepository.delete(session);
        log.info("Class session deleted successfully");
    }

    @Transactional(readOnly = true)
    public long countSessions() {
        return classSessionRepository.count();
    }
}
