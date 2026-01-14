package com.cesde.studentinfo.controller;

import com.cesde.studentinfo.dto.ApiResponse;
import com.cesde.studentinfo.dto.GradeDTO;
import com.cesde.studentinfo.dto.GradeResponseDTO;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.Grade;
import com.cesde.studentinfo.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller para gestión de calificaciones
 */
@RestController
@RequestMapping("/grades")
@RequiredArgsConstructor
@Slf4j
public class GradeController {

    private final GradeService gradeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GradeResponseDTO>>> getAllGrades() {
        log.info("GET /grades - Fetching all grades");
        List<Grade> grades = gradeService.getAllGrades();
        List<GradeResponseDTO> response = grades.stream()
                .map(GradeResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Grades retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GradeResponseDTO>> getGradeById(@PathVariable Long id) {
        log.info("GET /grades/{} - Fetching grade by ID", id);
        Grade grade = gradeService.getGradeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", id));
        return ResponseEntity.ok(ApiResponse.success(GradeResponseDTO.fromEntity(grade)));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<GradeResponseDTO>>> getGradesByStudent(@PathVariable Long studentId) {
        log.info("GET /grades/student/{} - Fetching grades by student", studentId);
        List<Grade> grades = gradeService.getGradesByStudentId(studentId);
        List<GradeResponseDTO> response = grades.stream()
                .map(GradeResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Student grades retrieved successfully"));
    }

    @GetMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<ApiResponse<List<GradeResponseDTO>>> getGradesByEnrollment(@PathVariable Long enrollmentId) {
        log.info("GET /grades/enrollment/{} - Fetching grades by enrollment", enrollmentId);
        List<Grade> grades = gradeService.getGradesByEnrollmentId(enrollmentId);
        List<GradeResponseDTO> response = grades.stream()
                .map(GradeResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Enrollment grades retrieved successfully"));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<List<GradeResponseDTO>>> getGradesByGroup(@PathVariable Long groupId) {
        log.info("GET /grades/group/{} - Fetching grades by group", groupId);
        List<Grade> grades = gradeService.getGradesByCourseGroupId(groupId);
        List<GradeResponseDTO> response = grades.stream()
                .map(GradeResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Group grades retrieved successfully"));
    }

    @GetMapping("/period/{periodId}")
    public ResponseEntity<ApiResponse<List<GradeResponseDTO>>> getGradesByPeriod(@PathVariable Long periodId) {
        log.info("GET /grades/period/{} - Fetching grades by period", periodId);
        List<Grade> grades = gradeService.getGradesByGradePeriodId(periodId);
        List<GradeResponseDTO> response = grades.stream()
                .map(GradeResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Period grades retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GradeResponseDTO>> createGrade(@Valid @RequestBody GradeDTO dto) {
        log.info("POST /grades - Creating new grade");

        // Nota: Aquí necesitarías buscar las entidades relacionadas
        // Por simplicidad, asumo que el DTO tiene los IDs necesarios
        // y el frontend los proporciona correctamente

        Grade grade = Grade.builder()
                .gradeValue(dto.getGradeValue())
                .assignmentDate(dto.getAssignmentDate())
                .comments(dto.getComments())
                .build();

        Grade saved = gradeService.createGrade(grade);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(GradeResponseDTO.fromEntity(saved), "Grade created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GradeResponseDTO>> updateGrade(
            @PathVariable Long id,
            @Valid @RequestBody GradeDTO dto) {
        log.info("PUT /grades/{} - Updating grade", id);

        Grade updates = Grade.builder()
                .gradeValue(dto.getGradeValue())
                .comments(dto.getComments())
                .build();

        Grade updated = gradeService.updateGrade(id, updates);
        return ResponseEntity.ok(ApiResponse.success(GradeResponseDTO.fromEntity(updated),
                "Grade updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGrade(@PathVariable Long id) {
        log.info("DELETE /grades/{} - Deleting grade", id);
        gradeService.deleteGrade(id);
        return ResponseEntity.ok(ApiResponse.success("Grade deleted successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countGrades() {
        log.info("GET /grades/count - Counting grades");
        long count = gradeService.countGrades();
        return ResponseEntity.ok(ApiResponse.success(count, "Count retrieved successfully"));
    }
}
