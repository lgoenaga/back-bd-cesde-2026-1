package com.cesde.studentinfo.service;

import com.cesde.studentinfo.exception.BusinessException;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.AcademicPeriod;
import com.cesde.studentinfo.model.Course;
import com.cesde.studentinfo.model.CourseEnrollment;
import com.cesde.studentinfo.model.Student;
import com.cesde.studentinfo.repository.AcademicPeriodRepository;
import com.cesde.studentinfo.repository.CourseEnrollmentRepository;
import com.cesde.studentinfo.repository.CourseRepository;
import com.cesde.studentinfo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service para gestión de inscripciones de estudiantes en cursos
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseEnrollmentService {

    private final CourseEnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final AcademicPeriodRepository academicPeriodRepository;

    @Transactional(readOnly = true)
    public List<CourseEnrollment> getAllEnrollments() {
        log.info("Fetching all enrollments");
        return enrollmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<CourseEnrollment> getEnrollmentById(Long id) {
        log.info("Fetching enrollment by id: {}", id);
        return enrollmentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<CourseEnrollment> getEnrollmentsByStudentId(Long studentId) {
        log.info("Fetching enrollments for student: {}", studentId);
        return enrollmentRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<CourseEnrollment> getEnrollmentsByCourseId(Long courseId) {
        log.info("Fetching enrollments for course: {}", courseId);
        return enrollmentRepository.findByCourseId(courseId);
    }

    @Transactional(readOnly = true)
    public List<CourseEnrollment> getEnrollmentsByPeriodId(Long periodId) {
        log.info("Fetching enrollments for period: {}", periodId);
        return enrollmentRepository.findByAcademicPeriodId(periodId);
    }

    public CourseEnrollment createEnrollment(CourseEnrollment enrollment) {
        log.info("Creating enrollment for student: {} in course: {}",
                enrollment.getStudent().getId(), enrollment.getCourse().getId());

        // Validar estudiante
        Student student = studentRepository.findById(enrollment.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", enrollment.getStudent().getId()));

        if (!student.getIsActive()) {
            throw new BusinessException("Student is not active and cannot be enrolled");
        }

        // Validar curso
        Course course = courseRepository.findById(enrollment.getCourse().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", enrollment.getCourse().getId()));

        if (!course.getIsActive()) {
            throw new BusinessException("Course is not active");
        }

        // Validar período académico
        AcademicPeriod period = academicPeriodRepository.findById(enrollment.getAcademicPeriod().getId())
                .orElseThrow(() -> new ResourceNotFoundException("AcademicPeriod", enrollment.getAcademicPeriod().getId()));

        if (!period.getIsActive()) {
            throw new BusinessException("Academic period is not active");
        }

        // Validar duplicados
        if (enrollmentRepository.existsByStudentIdAndCourseIdAndAcademicPeriodId(
                student.getId(), course.getId(), period.getId())) {
            throw new BusinessException("Student is already enrolled in this course for this period");
        }

        // Establecer valores por defecto
        if (enrollment.getEnrollmentDate() == null) {
            enrollment.setEnrollmentDate(LocalDate.now());
        }
        if (enrollment.getEnrollmentStatus() == null) {
            enrollment.setEnrollmentStatus(CourseEnrollment.EnrollmentStatus.ACTIVO);
        }

        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setAcademicPeriod(period);

        CourseEnrollment saved = enrollmentRepository.save(enrollment);
        log.info("Enrollment created successfully with id: {}", saved.getId());
        return saved;
    }

    public CourseEnrollment updateEnrollmentStatus(Long id, CourseEnrollment.EnrollmentStatus status) {
        log.info("Updating enrollment {} status to: {}", id, status);

        CourseEnrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseEnrollment", id));

        enrollment.setEnrollmentStatus(status);

        // Si cambia a EGRESADO, establecer fecha de completion
        if (status == CourseEnrollment.EnrollmentStatus.EGRESADO && enrollment.getCompletionDate() == null) {
            enrollment.setCompletionDate(LocalDate.now());
        }

        return enrollmentRepository.save(enrollment);
    }

    public CourseEnrollment updateEnrollment(Long id, CourseEnrollment updates) {
        log.info("Updating enrollment: {}", id);

        CourseEnrollment existing = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseEnrollment", id));

        if (updates.getEnrollmentStatus() != null) {
            existing.setEnrollmentStatus(updates.getEnrollmentStatus());
        }
        if (updates.getCompletionDate() != null) {
            existing.setCompletionDate(updates.getCompletionDate());
        }
        if (updates.getNotes() != null) {
            existing.setNotes(updates.getNotes());
        }

        return enrollmentRepository.save(existing);
    }

    public void deleteEnrollment(Long id) {
        log.info("Deleting enrollment: {}", id);
        CourseEnrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseEnrollment", id));

        enrollmentRepository.deleteById(id);
        log.info("Enrollment deleted successfully");
    }

    @Transactional(readOnly = true)
    public long countEnrollments() {
        return enrollmentRepository.count();
    }
}

