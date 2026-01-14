package com.cesde.studentinfo.controller;

import com.cesde.studentinfo.dto.ApiResponse;
import com.cesde.studentinfo.dto.CourseEnrollmentDTO;
import com.cesde.studentinfo.dto.CourseEnrollmentResponseDTO;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.AcademicPeriod;
import com.cesde.studentinfo.model.Course;
import com.cesde.studentinfo.model.CourseEnrollment;
import com.cesde.studentinfo.model.Student;
import com.cesde.studentinfo.repository.AcademicPeriodRepository;
import com.cesde.studentinfo.repository.CourseRepository;
import com.cesde.studentinfo.repository.StudentRepository;
import com.cesde.studentinfo.service.CourseEnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller para gestión de inscripciones
 */
@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
@Slf4j
public class CourseEnrollmentController {

    private final CourseEnrollmentService enrollmentService;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final AcademicPeriodRepository academicPeriodRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseEnrollmentResponseDTO>>> getAllEnrollments() {
        log.info("GET /enrollments - Fetching all enrollments");
        List<CourseEnrollment> enrollments = enrollmentService.getAllEnrollments();
        List<CourseEnrollmentResponseDTO> response = enrollments.stream()
                .map(CourseEnrollmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Enrollments retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseEnrollmentResponseDTO>> getEnrollmentById(@PathVariable Long id) {
        log.info("GET /enrollments/{} - Fetching enrollment by ID", id);
        CourseEnrollment enrollment = enrollmentService.getEnrollmentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseEnrollment", id));
        return ResponseEntity.ok(ApiResponse.success(CourseEnrollmentResponseDTO.fromEntity(enrollment)));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<CourseEnrollmentResponseDTO>>> getEnrollmentsByStudent(
            @PathVariable Long studentId) {
        log.info("GET /enrollments/student/{} - Fetching enrollments by student", studentId);
        List<CourseEnrollment> enrollments = enrollmentService.getEnrollmentsByStudentId(studentId);
        List<CourseEnrollmentResponseDTO> response = enrollments.stream()
                .map(CourseEnrollmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Student enrollments retrieved successfully"));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<CourseEnrollmentResponseDTO>>> getEnrollmentsByCourse(
            @PathVariable Long courseId) {
        log.info("GET /enrollments/course/{} - Fetching enrollments by course", courseId);
        List<CourseEnrollment> enrollments = enrollmentService.getEnrollmentsByCourseId(courseId);
        List<CourseEnrollmentResponseDTO> response = enrollments.stream()
                .map(CourseEnrollmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Course enrollments retrieved successfully"));
    }

    @GetMapping("/period/{periodId}")
    public ResponseEntity<ApiResponse<List<CourseEnrollmentResponseDTO>>> getEnrollmentsByPeriod(
            @PathVariable Long periodId) {
        log.info("GET /enrollments/period/{} - Fetching enrollments by period", periodId);
        List<CourseEnrollment> enrollments = enrollmentService.getEnrollmentsByPeriodId(periodId);
        List<CourseEnrollmentResponseDTO> response = enrollments.stream()
                .map(CourseEnrollmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Period enrollments retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CourseEnrollmentResponseDTO>> createEnrollment(
            @Valid @RequestBody CourseEnrollmentDTO enrollmentDTO) {
        log.info("POST /enrollments - Creating new enrollment");

        Student student = studentRepository.findById(enrollmentDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", enrollmentDTO.getStudentId()));

        Course course = courseRepository.findById(enrollmentDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", enrollmentDTO.getCourseId()));

        AcademicPeriod period = academicPeriodRepository.findById(enrollmentDTO.getAcademicPeriodId())
                .orElseThrow(() -> new ResourceNotFoundException("AcademicPeriod", enrollmentDTO.getAcademicPeriodId()));

        CourseEnrollment enrollment = CourseEnrollment.builder()
                .student(student)
                .course(course)
                .academicPeriod(period)
                .enrollmentDate(enrollmentDTO.getEnrollmentDate())
                .enrollmentStatus(enrollmentDTO.getEnrollmentStatus())
                .notes(enrollmentDTO.getNotes())
                .build();

        CourseEnrollment saved = enrollmentService.createEnrollment(enrollment);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(CourseEnrollmentResponseDTO.fromEntity(saved),
                        "Enrollment created successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CourseEnrollmentResponseDTO>> updateEnrollmentStatus(
            @PathVariable Long id,
            @RequestParam CourseEnrollment.EnrollmentStatus status) {
        log.info("PATCH /enrollments/{}/status - Updating status to: {}", id, status);
        CourseEnrollment updated = enrollmentService.updateEnrollmentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(CourseEnrollmentResponseDTO.fromEntity(updated),
                "Enrollment status updated successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseEnrollmentResponseDTO>> updateEnrollment(
            @PathVariable Long id,
            @Valid @RequestBody CourseEnrollmentDTO dto) {
        log.info("PUT /enrollments/{} - Updating enrollment", id);

        CourseEnrollment updates = CourseEnrollment.builder()
                .enrollmentStatus(dto.getEnrollmentStatus())
                .notes(dto.getNotes())
                .build();

        CourseEnrollment updated = enrollmentService.updateEnrollment(id, updates);
        return ResponseEntity.ok(ApiResponse.success(CourseEnrollmentResponseDTO.fromEntity(updated),
                "Enrollment updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEnrollment(@PathVariable Long id) {
        log.info("DELETE /enrollments/{} - Deleting enrollment", id);
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.ok(ApiResponse.success("Enrollment deleted successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countEnrollments() {
        log.info("GET /enrollments/count - Counting enrollments");
        long count = enrollmentService.countEnrollments();
        return ResponseEntity.ok(ApiResponse.success(count, "Count retrieved successfully"));
    }
}

