package com.cesde.studentinfo.service;

import com.cesde.studentinfo.exception.BusinessException;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.Grade;
import com.cesde.studentinfo.model.GradeComponent;
import com.cesde.studentinfo.model.GradePeriod;
import com.cesde.studentinfo.model.SubjectEnrollment;
import com.cesde.studentinfo.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service para gestión de calificaciones
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GradeService {

    private final GradeRepository gradeRepository;

    @Transactional(readOnly = true)
    public List<Grade> getAllGrades() {
        log.info("Fetching all grades");
        return gradeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Grade> getGradeById(Long id) {
        log.info("Fetching grade by id: {}", id);
        return gradeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Grade> getGradesByStudentId(Long studentId) {
        log.info("Fetching grades for student: {}", studentId);
        return gradeRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<Grade> getGradesByEnrollmentId(Long enrollmentId) {
        log.info("Fetching grades for enrollment: {}", enrollmentId);
        return gradeRepository.findByEnrollmentId(enrollmentId);
    }

    @Transactional(readOnly = true)
    public List<Grade> getGradesByCourseGroupId(Long groupId) {
        log.info("Fetching grades for course group: {}", groupId);
        return gradeRepository.findByCourseGroupId(groupId);
    }

    @Transactional(readOnly = true)
    public List<Grade> getGradesByGradePeriodId(Long periodId) {
        log.info("Fetching grades for grade period: {}", periodId);
        return gradeRepository.findByGradePeriodId(periodId);
    }

    public Grade createGrade(Grade grade) {
        log.info("Creating grade for subject enrollment: {}", grade.getSubjectEnrollment().getId());

        // Validar que la nota esté en el rango válido
        if (grade.getGradeValue().doubleValue() < 0 || grade.getGradeValue().doubleValue() > 5) {
            throw new BusinessException("Grade value must be between 0 and 5");
        }

        // Establecer fecha de asignación si no existe
        if (grade.getAssignmentDate() == null) {
            grade.setAssignmentDate(LocalDate.now());
        }

        Grade saved = gradeRepository.save(grade);
        log.info("Grade created successfully with id: {}", saved.getId());
        return saved;
    }

    public Grade updateGrade(Long id, Grade grade) {
        log.info("Updating grade: {}", id);

        Grade existing = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", id));

        if (grade.getGradeValue() != null) {
            // Validar rango
            if (grade.getGradeValue().doubleValue() < 0 || grade.getGradeValue().doubleValue() > 5) {
                throw new BusinessException("Grade value must be between 0 and 5");
            }
            existing.setGradeValue(grade.getGradeValue());
        }

        if (grade.getComments() != null) {
            existing.setComments(grade.getComments());
        }

        return gradeRepository.save(existing);
    }

    public void deleteGrade(Long id) {
        log.info("Deleting grade: {}", id);
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", id));

        gradeRepository.deleteById(id);
        log.info("Grade deleted successfully");
    }

    @Transactional(readOnly = true)
    public long countGrades() {
        return gradeRepository.count();
    }
}

