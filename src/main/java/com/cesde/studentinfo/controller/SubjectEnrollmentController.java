package com.cesde.studentinfo.controller;

import com.cesde.studentinfo.dto.ApiResponse;
import com.cesde.studentinfo.dto.PagedResponse;
import com.cesde.studentinfo.dto.SubjectEnrollmentDTO;
import com.cesde.studentinfo.dto.SubjectEnrollmentResponseDTO;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.LevelEnrollment;
import com.cesde.studentinfo.model.Subject;
import com.cesde.studentinfo.model.SubjectAssignment;
import com.cesde.studentinfo.model.SubjectEnrollment;
import com.cesde.studentinfo.repository.LevelEnrollmentRepository;
import com.cesde.studentinfo.repository.SubjectAssignmentRepository;
import com.cesde.studentinfo.repository.SubjectRepository;
import com.cesde.studentinfo.service.SubjectEnrollmentService;
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
import java.util.stream.Collectors;

/**
 * Controller para gestión de inscripciones a materias
 */
@RestController
@RequestMapping("/subject-enrollments")
@RequiredArgsConstructor
@Slf4j
public class SubjectEnrollmentController {

    private final SubjectEnrollmentService subjectEnrollmentService;
    private final LevelEnrollmentRepository levelEnrollmentRepository;
    private final SubjectAssignmentRepository subjectAssignmentRepository;
    private final SubjectRepository subjectRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectEnrollmentResponseDTO>>> getAllSubjectEnrollments() {
        log.info("GET /subject-enrollments - Fetching all subject enrollments");
        List<SubjectEnrollment> enrollments = subjectEnrollmentService.getAllSubjectEnrollments();
        List<SubjectEnrollmentResponseDTO> response = enrollments.stream()
                .map(SubjectEnrollmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Subject enrollments retrieved successfully"));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<SubjectEnrollmentResponseDTO>>> getAllSubjectEnrollmentsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        log.info("GET /subject-enrollments/paged - Fetching page {} with size {}", page, size);

        Sort.Direction direction = sort[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

        Page<SubjectEnrollment> enrollmentPage = subjectEnrollmentService.getAllSubjectEnrollmentsPaged(pageable);
        List<SubjectEnrollmentResponseDTO> content = enrollmentPage.getContent().stream()
                .map(SubjectEnrollmentResponseDTO::fromEntity)
                .collect(Collectors.toList());

        PagedResponse.SortInfo sortInfo = PagedResponse.SortInfo.builder()
                .sorted(enrollmentPage.getSort().isSorted())
                .sortBy(sort[0])
                .direction(sort[1].toUpperCase())
                .build();

        PagedResponse<SubjectEnrollmentResponseDTO> response = PagedResponse.<SubjectEnrollmentResponseDTO>builder()
                .content(content)
                .page(enrollmentPage.getNumber())
                .size(enrollmentPage.getSize())
                .totalElements(enrollmentPage.getTotalElements())
                .totalPages(enrollmentPage.getTotalPages())
                .first(enrollmentPage.isFirst())
                .last(enrollmentPage.isLast())
                .empty(enrollmentPage.isEmpty())
                .sort(sortInfo)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "Subject enrollments page retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectEnrollmentResponseDTO>> getSubjectEnrollmentById(@PathVariable Long id) {
        log.info("GET /subject-enrollments/{} - Fetching subject enrollment by ID", id);
        SubjectEnrollment enrollment = subjectEnrollmentService.getSubjectEnrollmentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubjectEnrollment", id));
        return ResponseEntity.ok(ApiResponse.success(SubjectEnrollmentResponseDTO.fromEntity(enrollment)));
    }

    @GetMapping("/level-enrollment/{levelEnrollmentId}")
    public ResponseEntity<ApiResponse<List<SubjectEnrollmentResponseDTO>>> getSubjectEnrollmentsByLevelEnrollment(
            @PathVariable Long levelEnrollmentId) {
        log.info("GET /subject-enrollments/level-enrollment/{} - Fetching subject enrollments", levelEnrollmentId);
        List<SubjectEnrollment> enrollments = subjectEnrollmentService.getByLevelEnrollmentId(levelEnrollmentId);
        List<SubjectEnrollmentResponseDTO> response = enrollments.stream()
                .map(SubjectEnrollmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Subject enrollments retrieved successfully"));
    }

    @GetMapping("/subject-assignment/{subjectAssignmentId}")
    public ResponseEntity<ApiResponse<List<SubjectEnrollmentResponseDTO>>> getSubjectEnrollmentsBySubjectAssignment(
            @PathVariable Long subjectAssignmentId) {
        log.info("GET /subject-enrollments/subject-assignment/{} - Fetching subject enrollments", subjectAssignmentId);
        List<SubjectEnrollment> enrollments = subjectEnrollmentService.getBySubjectAssignmentId(subjectAssignmentId);
        List<SubjectEnrollmentResponseDTO> response = enrollments.stream()
                .map(SubjectEnrollmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Subject enrollments retrieved successfully"));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<SubjectEnrollmentResponseDTO>>> getSubjectEnrollmentsByStatus(
            @PathVariable SubjectEnrollment.SubjectStatus status) {
        log.info("GET /subject-enrollments/status/{} - Fetching subject enrollments", status);
        List<SubjectEnrollment> enrollments = subjectEnrollmentService.getByStatus(status);
        List<SubjectEnrollmentResponseDTO> response = enrollments.stream()
                .map(SubjectEnrollmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Subject enrollments retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubjectEnrollmentResponseDTO>> createSubjectEnrollment(
            @Valid @RequestBody SubjectEnrollmentDTO dto) {
        log.info("POST /subject-enrollments - Creating new subject enrollment (Subject ID: {}, Assignment ID: {})",
                dto.getSubjectId(), dto.getSubjectAssignmentId());

        LevelEnrollment levelEnrollment = levelEnrollmentRepository.findById(dto.getLevelEnrollmentId())
                .orElseThrow(() -> new ResourceNotFoundException("LevelEnrollment", dto.getLevelEnrollmentId()));

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", dto.getSubjectId()));

        // SubjectAssignment is OPTIONAL
        SubjectAssignment subjectAssignment = null;
        if (dto.getSubjectAssignmentId() != null) {
            subjectAssignment = subjectAssignmentRepository.findById(dto.getSubjectAssignmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("SubjectAssignment", dto.getSubjectAssignmentId()));
        }

        SubjectEnrollment enrollment = SubjectEnrollment.builder()
                .levelEnrollment(levelEnrollment)
                .subject(subject)
                .subjectAssignment(subjectAssignment)
                .enrollmentDate(dto.getEnrollmentDate())
                .status(dto.getStatus())
                .build();

        SubjectEnrollment saved = subjectEnrollmentService.createSubjectEnrollment(enrollment);

        // Different message depending on whether professor is assigned
        String message;
        if (saved.getSubjectAssignment() != null) {
            message = "Subject enrollment created successfully with professor " +
                     saved.getSubjectAssignment().getProfessor().getFirstName() + " " +
                     saved.getSubjectAssignment().getProfessor().getLastName();
        } else {
            message = "Subject enrollment created successfully. " +
                     "⚠️ Note: Professor not assigned yet. This can be updated later.";
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(SubjectEnrollmentResponseDTO.fromEntity(saved), message));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectEnrollmentResponseDTO>> updateSubjectEnrollment(
            @PathVariable Long id,
            @Valid @RequestBody SubjectEnrollmentDTO dto) {
        log.info("PUT /subject-enrollments/{} - Updating subject enrollment", id);

        SubjectEnrollment updates = SubjectEnrollment.builder()
                .status(dto.getStatus())
                .build();

        SubjectEnrollment updated = subjectEnrollmentService.updateSubjectEnrollment(id, updates);
        return ResponseEntity.ok(ApiResponse.success(SubjectEnrollmentResponseDTO.fromEntity(updated),
                "Subject enrollment updated successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<SubjectEnrollmentResponseDTO>> updateSubjectEnrollmentStatus(
            @PathVariable Long id,
            @RequestParam SubjectEnrollment.SubjectStatus status) {
        log.info("PATCH /subject-enrollments/{}/status - Updating status to: {}", id, status);
        SubjectEnrollment updated = subjectEnrollmentService.updateSubjectEnrollmentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(SubjectEnrollmentResponseDTO.fromEntity(updated),
                "Subject enrollment status updated successfully"));
    }

    @PatchMapping("/{id}/assign-professor")
    public ResponseEntity<ApiResponse<SubjectEnrollmentResponseDTO>> assignProfessor(
            @PathVariable Long id,
            @RequestParam Long subjectAssignmentId) {
        log.info("PATCH /subject-enrollments/{}/assign-professor - Assigning professor (assignment ID: {})",
                id, subjectAssignmentId);

        SubjectEnrollment enrollment = subjectEnrollmentService.getSubjectEnrollmentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubjectEnrollment", id));

        SubjectAssignment assignment = subjectAssignmentRepository.findById(subjectAssignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("SubjectAssignment", subjectAssignmentId));

        // Validate that assignment matches the enrolled subject
        if (!assignment.getSubject().getId().equals(enrollment.getSubject().getId())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("SubjectAssignment does not match the enrolled subject. " +
                            "Enrollment is for: " + enrollment.getSubject().getName() +
                            ", but assignment is for: " + assignment.getSubject().getName()));
        }

        enrollment.setSubjectAssignment(assignment);
        SubjectEnrollment updated = subjectEnrollmentService.updateSubjectEnrollment(id, enrollment);

        return ResponseEntity.ok(ApiResponse.success(
                SubjectEnrollmentResponseDTO.fromEntity(updated),
                "Professor assigned successfully to subject enrollment"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubjectEnrollment(@PathVariable Long id) {
        log.info("DELETE /subject-enrollments/{} - Deleting subject enrollment", id);
        subjectEnrollmentService.deleteSubjectEnrollment(id);
        return ResponseEntity.ok(ApiResponse.success("Subject enrollment deleted successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countSubjectEnrollments() {
        log.info("GET /subject-enrollments/count - Counting subject enrollments");
        long count = subjectEnrollmentService.countSubjectEnrollments();
        return ResponseEntity.ok(ApiResponse.success(count, "Count retrieved successfully"));
    }
}
