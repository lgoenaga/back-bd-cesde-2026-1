package com.cesde.studentinfo.service;

import com.cesde.studentinfo.exception.BusinessException;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.*;
import com.cesde.studentinfo.repository.*;
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
 * Service para gestión de inscripciones de estudiantes a niveles
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LevelEnrollmentService {

    private final LevelEnrollmentRepository levelEnrollmentRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final LevelRepository levelRepository;
    private final AcademicPeriodRepository academicPeriodRepository;
    private final CourseGroupRepository courseGroupRepository;

    @Transactional(readOnly = true)
    public List<LevelEnrollment> getAllLevelEnrollments() {
        log.info("Fetching all level enrollments");
        return levelEnrollmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<LevelEnrollment> getAllLevelEnrollmentsPaged(Pageable pageable) {
        log.info("Fetching level enrollments page: {}", pageable.getPageNumber());
        return levelEnrollmentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<LevelEnrollment> getLevelEnrollmentById(Long id) {
        log.info("Fetching level enrollment by id: {}", id);
        return levelEnrollmentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<LevelEnrollment> getLevelEnrollmentsByCourseEnrollmentId(Long courseEnrollmentId) {
        log.info("Fetching level enrollments for course enrollment: {}", courseEnrollmentId);
        return levelEnrollmentRepository.findByCourseEnrollmentId(courseEnrollmentId);
    }

    @Transactional(readOnly = true)
    public List<LevelEnrollment> getByLevelId(Long levelId) {
        log.info("Fetching level enrollments for level: {}", levelId);
        return levelEnrollmentRepository.findByLevelId(levelId);
    }

    @Transactional(readOnly = true)
    public List<LevelEnrollment> getByAcademicPeriodId(Long periodId) {
        log.info("Fetching level enrollments for academic period: {}", periodId);
        return levelEnrollmentRepository.findByAcademicPeriodId(periodId);
    }

    @Transactional(readOnly = true)
    public List<LevelEnrollment> getByGroupId(Long groupId) {
        log.info("Fetching level enrollments for group: {}", groupId);
        return levelEnrollmentRepository.findByGroupId(groupId);
    }

    @Transactional(readOnly = true)
    public List<LevelEnrollment> getByStatus(LevelEnrollment.LevelStatus status) {
        log.info("Fetching level enrollments with status: {}", status);
        return levelEnrollmentRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public long countLevelEnrollments() {
        log.info("Counting level enrollments");
        return levelEnrollmentRepository.count();
    }

    public LevelEnrollment createLevelEnrollment(LevelEnrollment enrollment) {
        log.info("Creating LevelEnrollment for CourseEnrollment ID: {} in Level: {}",
                enrollment.getCourseEnrollment().getId(), enrollment.getLevel().getId());

        // Validar CourseEnrollment
        CourseEnrollment courseEnrollment = courseEnrollmentRepository
                .findById(enrollment.getCourseEnrollment().getId())
                .orElseThrow(() -> new ResourceNotFoundException("CourseEnrollment",
                        enrollment.getCourseEnrollment().getId()));

        if (courseEnrollment.getEnrollmentStatus() != CourseEnrollment.EnrollmentStatus.ACTIVO) {
            throw new BusinessException("Course enrollment is not active and cannot enroll in levels");
        }

        // Validar Level
        Level level = levelRepository.findById(enrollment.getLevel().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Level", enrollment.getLevel().getId()));

        // Validar AcademicPeriod
        AcademicPeriod period = academicPeriodRepository.findById(enrollment.getAcademicPeriod().getId())
                .orElseThrow(() -> new ResourceNotFoundException("AcademicPeriod",
                        enrollment.getAcademicPeriod().getId()));

        Boolean periodIsActive = period.getIsActive();
        if (periodIsActive != null && !periodIsActive) {
            throw new BusinessException("Academic period is not active");
        }

        // Validar Group si se proporciona
        if (enrollment.getGroup() != null && enrollment.getGroup().getId() != null) {
            CourseGroup group = courseGroupRepository.findById(enrollment.getGroup().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("CourseGroup",
                            enrollment.getGroup().getId()));
            enrollment.setGroup(group);
        }

        // Establecer fecha de inscripción si no se proporciona
        if (enrollment.getEnrollmentDate() == null) {
            enrollment.setEnrollmentDate(LocalDate.now());
        }

        // Establecer estado por defecto
        if (enrollment.getStatus() == null) {
            enrollment.setStatus(LevelEnrollment.LevelStatus.EN_CURSO);
        }

        LevelEnrollment saved = levelEnrollmentRepository.save(enrollment);
        log.info("Level enrollment created successfully with ID: {}", saved.getId());
        return saved;
    }

    public LevelEnrollment updateLevelEnrollment(Long id, LevelEnrollment updates) {
        log.info("Updating level enrollment with ID: {}", id);

        LevelEnrollment existing = levelEnrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LevelEnrollment", id));

        if (updates.getStatus() != null) {
            existing.setStatus(updates.getStatus());
        }

        if (updates.getFinalAverage() != null) {
            existing.setFinalAverage(updates.getFinalAverage());
        }

        if (updates.getCompletionDate() != null) {
            existing.setCompletionDate(updates.getCompletionDate());
        }

        LevelEnrollment updated = levelEnrollmentRepository.save(existing);
        log.info("Level enrollment updated successfully");
        return updated;
    }

    public LevelEnrollment updateLevelEnrollmentStatus(Long id, LevelEnrollment.LevelStatus status) {
        log.info("Updating level enrollment status for ID: {} to {}", id, status);

        LevelEnrollment enrollment = levelEnrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LevelEnrollment", id));

        enrollment.setStatus(status);

        // Si se marca como APROBADO o REPROBADO, establecer fecha de finalización
        if ((status == LevelEnrollment.LevelStatus.APROBADO ||
            status == LevelEnrollment.LevelStatus.REPROBADO) &&
            enrollment.getCompletionDate() == null) {
            enrollment.setCompletionDate(LocalDate.now());
        }

        LevelEnrollment updated = levelEnrollmentRepository.save(enrollment);
        log.info("Level enrollment status updated successfully");
        return updated;
    }

    public void deleteLevelEnrollment(Long id) {
        log.info("Deleting level enrollment with ID: {}", id);

        if (!levelEnrollmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("LevelEnrollment", id);
        }

        levelEnrollmentRepository.deleteById(id);
        log.info("Level enrollment deleted successfully");
    }
}
