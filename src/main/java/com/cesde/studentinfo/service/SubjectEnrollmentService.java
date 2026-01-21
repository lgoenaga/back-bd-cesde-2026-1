package com.cesde.studentinfo.service;

import com.cesde.studentinfo.exception.BusinessException;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.LevelEnrollment;
import com.cesde.studentinfo.model.Subject;
import com.cesde.studentinfo.model.SubjectAssignment;
import com.cesde.studentinfo.model.SubjectEnrollment;
import com.cesde.studentinfo.model.Level;
import com.cesde.studentinfo.repository.LevelEnrollmentRepository;
import com.cesde.studentinfo.repository.SubjectAssignmentRepository;
import com.cesde.studentinfo.repository.SubjectEnrollmentRepository;
import com.cesde.studentinfo.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service para gestión de inscripciones de estudiantes a materias
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubjectEnrollmentService {

    private final SubjectEnrollmentRepository subjectEnrollmentRepository;
    private final LevelEnrollmentRepository levelEnrollmentRepository;
    private final SubjectAssignmentRepository subjectAssignmentRepository;
    private final SubjectRepository subjectRepository;

    @Transactional(readOnly = true)
    public List<SubjectEnrollment> getAllSubjectEnrollments() {
        log.info("Fetching all subject enrollments");
        return subjectEnrollmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<SubjectEnrollment> getAllSubjectEnrollmentsPaged(Pageable pageable) {
        log.info("Fetching subject enrollments page: {}", pageable.getPageNumber());
        return subjectEnrollmentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<SubjectEnrollment> getSubjectEnrollmentById(Long id) {
        log.info("Fetching subject enrollment by id: {}", id);
        return subjectEnrollmentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<SubjectEnrollment> getByLevelEnrollmentId(Long levelEnrollmentId) {
        log.info("Fetching subject enrollments for level enrollment: {}", levelEnrollmentId);
        return subjectEnrollmentRepository.findByLevelEnrollmentId(levelEnrollmentId);
    }

    @Transactional(readOnly = true)
    public List<SubjectEnrollment> getBySubjectAssignmentId(Long subjectAssignmentId) {
        log.info("Fetching subject enrollments for subject assignment: {}", subjectAssignmentId);
        return subjectEnrollmentRepository.findBySubjectAssignmentId(subjectAssignmentId);
    }

    @Transactional(readOnly = true)
    public List<SubjectEnrollment> getByStatus(SubjectEnrollment.SubjectStatus status) {
        log.info("Fetching subject enrollments with status: {}", status);
        return subjectEnrollmentRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public long countSubjectEnrollments() {
        log.info("Counting subject enrollments");
        return subjectEnrollmentRepository.count();
    }

    public SubjectEnrollment createSubjectEnrollment(SubjectEnrollment enrollment) {
        log.info("Creating SubjectEnrollment for LevelEnrollment ID: {} in Subject ID: {}",
                enrollment.getLevelEnrollment().getId(), enrollment.getSubject().getId());

        // Validar LevelEnrollment
        LevelEnrollment levelEnrollment = levelEnrollmentRepository
                .findById(enrollment.getLevelEnrollment().getId())
                .orElseThrow(() -> new ResourceNotFoundException("LevelEnrollment",
                        enrollment.getLevelEnrollment().getId()));

        if (levelEnrollment.getStatus() != LevelEnrollment.LevelStatus.EN_CURSO) {
            throw new BusinessException(
                "Level enrollment is not active. Only students with active level enrollments can enroll in subjects");
        }

        // Validar Subject (OBLIGATORIO)
        Subject subject = subjectRepository
                .findById(enrollment.getSubject().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject",
                        enrollment.getSubject().getId()));

        // Validación cruzada: Subject debe pertenecer al Level correcto
        Level levelFromEnrollment = levelEnrollment.getLevel();

        if (!subject.getLevel().getId().equals(levelFromEnrollment.getId())) {
            throw new BusinessException(
                "Subject does not belong to the level of this level enrollment. " +
                "Expected level: " + levelFromEnrollment.getName() +
                ", but subject belongs to: " + subject.getLevel().getName());
        }

        // Validar SubjectAssignment si se proporciona (OPCIONAL)
        SubjectAssignment subjectAssignment = null;
        if (enrollment.getSubjectAssignment() != null && enrollment.getSubjectAssignment().getId() != null) {
            subjectAssignment = subjectAssignmentRepository
                    .findById(enrollment.getSubjectAssignment().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("SubjectAssignment",
                            enrollment.getSubjectAssignment().getId()));

            // Validar que el assignment corresponde a la misma materia
            if (!subjectAssignment.getSubject().getId().equals(subject.getId())) {
                throw new BusinessException(
                    "SubjectAssignment does not match the subject. " +
                    "Assignment is for: " + subjectAssignment.getSubject().getName() +
                    ", but trying to enroll in: " + subject.getName());
            }
        }

        // Establecer fecha de inscripción si no se proporciona
        if (enrollment.getEnrollmentDate() == null) {
            enrollment.setEnrollmentDate(LocalDate.now());
        }

        // Establecer estado por defecto
        if (enrollment.getStatus() == null) {
            enrollment.setStatus(SubjectEnrollment.SubjectStatus.EN_CURSO);
        }

        // Establecer las relaciones validadas
        enrollment.setSubject(subject);
        enrollment.setSubjectAssignment(subjectAssignment);

        SubjectEnrollment saved = subjectEnrollmentRepository.save(enrollment);

        // Log diferente si no hay profesor asignado
        if (saved.getSubjectAssignment() == null) {
            log.warn("SubjectEnrollment created WITHOUT professor assignment. " +
                    "EnrollmentId: {}, Subject: {}, Student: {}",
                    saved.getId(),
                    saved.getSubject().getName(),
                    saved.getLevelEnrollment().getCourseEnrollment().getStudent().getFirstName());
        } else {
            log.info("Subject enrollment created successfully with ID: {} (Subject: {}, Professor: {})",
                    saved.getId(),
                    subject.getName(),
                    saved.getSubjectAssignment().getProfessor().getFirstName());
        }

        return saved;
    }

    public SubjectEnrollment updateSubjectEnrollment(Long id, SubjectEnrollment updates) {
        log.info("Updating subject enrollment with ID: {}", id);

        SubjectEnrollment existing = subjectEnrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubjectEnrollment", id));

        if (updates.getStatus() != null) {
            existing.setStatus(updates.getStatus());
        }

        if (updates.getFinalGrade() != null) {
            existing.setFinalGrade(updates.getFinalGrade());
        }

        SubjectEnrollment updated = subjectEnrollmentRepository.save(existing);
        log.info("Subject enrollment updated successfully");
        return updated;
    }

    public SubjectEnrollment updateSubjectEnrollmentStatus(Long id, SubjectEnrollment.SubjectStatus status) {
        log.info("Updating subject enrollment status for ID: {} to {}", id, status);

        SubjectEnrollment enrollment = subjectEnrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubjectEnrollment", id));

        enrollment.setStatus(status);

        SubjectEnrollment updated = subjectEnrollmentRepository.save(enrollment);
        log.info("Subject enrollment status updated successfully");
        return updated;
    }

    public void deleteSubjectEnrollment(Long id) {
        log.info("Deleting subject enrollment with ID: {}", id);

        if (!subjectEnrollmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("SubjectEnrollment", id);
        }

        subjectEnrollmentRepository.deleteById(id);
        log.info("Subject enrollment deleted successfully");
    }
}
