package com.cesde.studentinfo.controller;

import com.cesde.studentinfo.dto.ApiResponse;
import com.cesde.studentinfo.dto.PagedResponse;
import com.cesde.studentinfo.dto.SubjectAssignmentRequestDTO;
import com.cesde.studentinfo.dto.SubjectAssignmentResponseDTO;
import com.cesde.studentinfo.dto.SubjectAssignmentUpdateDTO;
import com.cesde.studentinfo.service.SubjectAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for SubjectAssignment operations
 * Manages professor assignments to subjects
 */
@RestController
@RequestMapping("/subject-assignments")
@RequiredArgsConstructor
@Slf4j
public class SubjectAssignmentController {

    private final SubjectAssignmentService subjectAssignmentService;

    /**
     * Create a new subject assignment
     * POST /api/subject-assignments
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SubjectAssignmentResponseDTO>> createAssignment(
            @Valid @RequestBody SubjectAssignmentRequestDTO dto) {
        log.info("POST /subject-assignments - Creating subject assignment");
        SubjectAssignmentResponseDTO created = subjectAssignmentService.createAssignment(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Subject assignment created successfully"));
    }

    /**
     * Get all subject assignments (no pagination)
     * GET /api/subject-assignments
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectAssignmentResponseDTO>>> getAllAssignments() {
        log.info("GET /subject-assignments - Fetching all subject assignments");
        List<SubjectAssignmentResponseDTO> assignments = subjectAssignmentService.getAllAssignments();
        return ResponseEntity.ok(ApiResponse.success(assignments, "Subject assignments retrieved successfully"));
    }

    /**
     * Get all subject assignments with pagination
     * GET /api/subject-assignments/paged
     */
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<SubjectAssignmentResponseDTO>>> getAllAssignmentsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("GET /subject-assignments/paged - page: {}, size: {}", page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SubjectAssignmentResponseDTO> pagedResult = subjectAssignmentService.getAllAssignmentsPaged(pageable);

        PagedResponse<SubjectAssignmentResponseDTO> response = PagedResponse.from(pagedResult);

        return ResponseEntity.ok(ApiResponse.success(response, "Subject assignments retrieved successfully"));
    }

    /**
     * Get active subject assignments only
     * GET /api/subject-assignments/active
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SubjectAssignmentResponseDTO>>> getActiveAssignments() {
        log.info("GET /subject-assignments/active - Fetching active assignments");
        List<SubjectAssignmentResponseDTO> assignments = subjectAssignmentService.getActiveAssignments();
        return ResponseEntity.ok(ApiResponse.success(assignments, "Active subject assignments retrieved successfully"));
    }

    /**
     * Get active subject assignments with pagination
     * GET /api/subject-assignments/active/paged
     */
    @GetMapping("/active/paged")
    public ResponseEntity<ApiResponse<PagedResponse<SubjectAssignmentResponseDTO>>> getActiveAssignmentsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("GET /subject-assignments/active/paged - page: {}, size: {}", page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SubjectAssignmentResponseDTO> pagedResult = subjectAssignmentService.getActiveAssignmentsPaged(pageable);

        PagedResponse<SubjectAssignmentResponseDTO> response = PagedResponse.from(pagedResult);

        return ResponseEntity.ok(ApiResponse.success(response, "Active subject assignments retrieved successfully"));
    }

    /**
     * Get subject assignment by ID
     * GET /api/subject-assignments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectAssignmentResponseDTO>> getAssignmentById(@PathVariable Long id) {
        log.info("GET /subject-assignments/{} - Fetching assignment", id);
        SubjectAssignmentResponseDTO assignment = subjectAssignmentService.getAssignmentById(id);
        return ResponseEntity.ok(ApiResponse.success(assignment, "Subject assignment retrieved successfully"));
    }

    /**
     * Get assignments by subject ID
     * GET /api/subject-assignments/subject/{subjectId}
     */
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<SubjectAssignmentResponseDTO>>> getAssignmentsBySubject(
            @PathVariable Long subjectId) {
        log.info("GET /subject-assignments/subject/{} - Fetching assignments by subject", subjectId);
        List<SubjectAssignmentResponseDTO> assignments = subjectAssignmentService.getAssignmentsBySubject(subjectId);
        return ResponseEntity.ok(ApiResponse.success(assignments, "Subject assignments retrieved successfully"));
    }

    /**
     * Get assignments by subject ID with pagination
     * GET /api/subject-assignments/subject/{subjectId}/paged
     */
    @GetMapping("/subject/{subjectId}/paged")
    public ResponseEntity<ApiResponse<PagedResponse<SubjectAssignmentResponseDTO>>> getAssignmentsBySubjectPaged(
            @PathVariable Long subjectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("GET /subject-assignments/subject/{}/paged - page: {}, size: {}", subjectId, page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SubjectAssignmentResponseDTO> pagedResult =
                subjectAssignmentService.getAssignmentsBySubjectPaged(subjectId, pageable);

        PagedResponse<SubjectAssignmentResponseDTO> response = PagedResponse.from(pagedResult);

        return ResponseEntity.ok(ApiResponse.success(response, "Subject assignments retrieved successfully"));
    }

    /**
     * Get assignments by professor ID
     * GET /api/subject-assignments/professor/{professorId}
     */
    @GetMapping("/professor/{professorId}")
    public ResponseEntity<ApiResponse<List<SubjectAssignmentResponseDTO>>> getAssignmentsByProfessor(
            @PathVariable Long professorId) {
        log.info("GET /subject-assignments/professor/{} - Fetching assignments by professor", professorId);
        List<SubjectAssignmentResponseDTO> assignments = subjectAssignmentService.getAssignmentsByProfessor(professorId);
        return ResponseEntity.ok(ApiResponse.success(assignments, "Subject assignments retrieved successfully"));
    }

    /**
     * Get assignments by professor ID with pagination
     * GET /api/subject-assignments/professor/{professorId}/paged
     */
    @GetMapping("/professor/{professorId}/paged")
    public ResponseEntity<ApiResponse<PagedResponse<SubjectAssignmentResponseDTO>>> getAssignmentsByProfessorPaged(
            @PathVariable Long professorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("GET /subject-assignments/professor/{}/paged - page: {}, size: {}", professorId, page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SubjectAssignmentResponseDTO> pagedResult =
                subjectAssignmentService.getAssignmentsByProfessorPaged(professorId, pageable);

        PagedResponse<SubjectAssignmentResponseDTO> response = PagedResponse.from(pagedResult);

        return ResponseEntity.ok(ApiResponse.success(response, "Subject assignments retrieved successfully"));
    }

    /**
     * Get assignments by academic period ID
     * GET /api/subject-assignments/period/{periodId}
     */
    @GetMapping("/period/{periodId}")
    public ResponseEntity<ApiResponse<List<SubjectAssignmentResponseDTO>>> getAssignmentsByPeriod(
            @PathVariable Long periodId) {
        log.info("GET /subject-assignments/period/{} - Fetching assignments by period", periodId);
        List<SubjectAssignmentResponseDTO> assignments = subjectAssignmentService.getAssignmentsByPeriod(periodId);
        return ResponseEntity.ok(ApiResponse.success(assignments, "Subject assignments retrieved successfully"));
    }

    /**
     * Get assignments by academic period ID with pagination
     * GET /api/subject-assignments/period/{periodId}/paged
     */
    @GetMapping("/period/{periodId}/paged")
    public ResponseEntity<ApiResponse<PagedResponse<SubjectAssignmentResponseDTO>>> getAssignmentsByPeriodPaged(
            @PathVariable Long periodId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("GET /subject-assignments/period/{}/paged - page: {}, size: {}", periodId, page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SubjectAssignmentResponseDTO> pagedResult =
                subjectAssignmentService.getAssignmentsByPeriodPaged(periodId, pageable);

        PagedResponse<SubjectAssignmentResponseDTO> response = PagedResponse.from(pagedResult);

        return ResponseEntity.ok(ApiResponse.success(response, "Subject assignments retrieved successfully"));
    }

    /**
     * Get assignments by subject and period
     * GET /api/subject-assignments/subject/{subjectId}/period/{periodId}
     */
    @GetMapping("/subject/{subjectId}/period/{periodId}")
    public ResponseEntity<ApiResponse<List<SubjectAssignmentResponseDTO>>> getAssignmentsBySubjectAndPeriod(
            @PathVariable Long subjectId,
            @PathVariable Long periodId) {
        log.info("GET /subject-assignments/subject/{}/period/{} - Fetching assignments", subjectId, periodId);
        List<SubjectAssignmentResponseDTO> assignments =
                subjectAssignmentService.getAssignmentsBySubjectAndPeriod(subjectId, periodId);
        return ResponseEntity.ok(ApiResponse.success(assignments, "Subject assignments retrieved successfully"));
    }

    /**
     * Update a subject assignment
     * PUT /api/subject-assignments/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectAssignmentResponseDTO>> updateAssignment(
            @PathVariable Long id,
            @Valid @RequestBody SubjectAssignmentUpdateDTO dto) {
        log.info("PUT /subject-assignments/{} - Updating assignment", id);
        SubjectAssignmentResponseDTO updated = subjectAssignmentService.updateAssignment(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Subject assignment updated successfully"));
    }

    /**
     * Delete a subject assignment (soft delete)
     * DELETE /api/subject-assignments/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAssignment(@PathVariable Long id) {
        log.info("DELETE /subject-assignments/{} - Soft deleting assignment", id);
        subjectAssignmentService.deleteAssignment(id);
        return ResponseEntity.ok(ApiResponse.success("Subject assignment deleted successfully"));
    }

    /**
     * Permanently delete a subject assignment
     * DELETE /api/subject-assignments/{id}/permanent
     */
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeleteAssignment(@PathVariable Long id) {
        log.info("DELETE /subject-assignments/{}/permanent - Permanently deleting assignment", id);
        subjectAssignmentService.permanentlyDeleteAssignment(id);
        return ResponseEntity.ok(ApiResponse.success("Subject assignment permanently deleted"));
    }
}
