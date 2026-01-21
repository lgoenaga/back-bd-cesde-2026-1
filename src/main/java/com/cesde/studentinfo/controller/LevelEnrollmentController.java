package com.cesde.studentinfo.controller;

import com.cesde.studentinfo.dto.ApiResponse;
import com.cesde.studentinfo.dto.LevelEnrollmentDTO;
import com.cesde.studentinfo.dto.LevelEnrollmentResponseDTO;
import com.cesde.studentinfo.dto.PagedResponse;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.*;
import com.cesde.studentinfo.repository.*;
import com.cesde.studentinfo.service.LevelEnrollmentService;
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
 * Controller para gestión de inscripciones a niveles
 */
@RestController
@RequestMapping("/level-enrollments")
@RequiredArgsConstructor
@Slf4j
public class LevelEnrollmentController {

    private final LevelEnrollmentService levelEnrollmentService;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final LevelRepository levelRepository;
    private final AcademicPeriodRepository academicPeriodRepository;
    private final CourseGroupRepository courseGroupRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<LevelEnrollmentResponseDTO>>> getAllLevelEnrollments() {
        log.info("GET /level-enrollments - Fetching all level enrollments");
        List<LevelEnrollment> enrollments = levelEnrollmentService.getAllLevelEnrollments();
        List<LevelEnrollmentResponseDTO> response = enrollments.stream()
                .map(LevelEnrollmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Level enrollments retrieved successfully"));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<LevelEnrollmentResponseDTO>>> getAllLevelEnrollmentsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        log.info("GET /level-enrollments/paged - Fetching page {} with size {}", page, size);

        Sort.Direction direction = sort[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

        Page<LevelEnrollment> enrollmentPage = levelEnrollmentService.getAllLevelEnrollmentsPaged(pageable);
        List<LevelEnrollmentResponseDTO> content = enrollmentPage.getContent().stream()
                .map(LevelEnrollmentResponseDTO::fromEntity)
                .collect(Collectors.toList());

        PagedResponse.SortInfo sortInfo = PagedResponse.SortInfo.builder()
                .sorted(enrollmentPage.getSort().isSorted())
                .sortBy(sort[0])
                .direction(sort[1].toUpperCase())
                .build();

        PagedResponse<LevelEnrollmentResponseDTO> response = PagedResponse.<LevelEnrollmentResponseDTO>builder()
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

        return ResponseEntity.ok(ApiResponse.success(response, "Level enrollments page retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LevelEnrollmentResponseDTO>> getLevelEnrollmentById(@PathVariable Long id) {
        log.info("GET /level-enrollments/{} - Fetching level enrollment by ID", id);
        LevelEnrollment enrollment = levelEnrollmentService.getLevelEnrollmentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LevelEnrollment", id));
        return ResponseEntity.ok(ApiResponse.success(LevelEnrollmentResponseDTO.fromEntity(enrollment)));
    }

    @GetMapping("/course-enrollment/{courseEnrollmentId}")
    public ResponseEntity<ApiResponse<List<LevelEnrollmentResponseDTO>>> getLevelEnrollmentsByCourseEnrollment(
            @PathVariable Long courseEnrollmentId) {
        log.info("GET /level-enrollments/course-enrollment/{} - Fetching level enrollments", courseEnrollmentId);
        List<LevelEnrollment> enrollments = levelEnrollmentService.getLevelEnrollmentsByCourseEnrollmentId(courseEnrollmentId);
        List<LevelEnrollmentResponseDTO> response = enrollments.stream()
                .map(LevelEnrollmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Level enrollments retrieved successfully"));
    }

    @GetMapping("/level/{levelId}")
    public ResponseEntity<ApiResponse<List<LevelEnrollmentResponseDTO>>> getLevelEnrollmentsByLevel(
            @PathVariable Long levelId) {
        log.info("GET /level-enrollments/level/{} - Fetching level enrollments", levelId);
        List<LevelEnrollment> enrollments = levelEnrollmentService.getByLevelId(levelId);
        List<LevelEnrollmentResponseDTO> response = enrollments.stream()
                .map(LevelEnrollmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Level enrollments retrieved successfully"));
    }

    @GetMapping("/period/{periodId}")
    public ResponseEntity<ApiResponse<List<LevelEnrollmentResponseDTO>>> getLevelEnrollmentsByPeriod(
            @PathVariable Long periodId) {
        log.info("GET /level-enrollments/period/{} - Fetching level enrollments", periodId);
        List<LevelEnrollment> enrollments = levelEnrollmentService.getByAcademicPeriodId(periodId);
        List<LevelEnrollmentResponseDTO> response = enrollments.stream()
                .map(LevelEnrollmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Level enrollments retrieved successfully"));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<List<LevelEnrollmentResponseDTO>>> getLevelEnrollmentsByGroup(
            @PathVariable Long groupId) {
        log.info("GET /level-enrollments/group/{} - Fetching level enrollments", groupId);
        List<LevelEnrollment> enrollments = levelEnrollmentService.getByGroupId(groupId);
        List<LevelEnrollmentResponseDTO> response = enrollments.stream()
                .map(LevelEnrollmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Level enrollments retrieved successfully"));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<LevelEnrollmentResponseDTO>>> getLevelEnrollmentsByStatus(
            @PathVariable LevelEnrollment.LevelStatus status) {
        log.info("GET /level-enrollments/status/{} - Fetching level enrollments", status);
        List<LevelEnrollment> enrollments = levelEnrollmentService.getByStatus(status);
        List<LevelEnrollmentResponseDTO> response = enrollments.stream()
                .map(LevelEnrollmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Level enrollments retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LevelEnrollmentResponseDTO>> createLevelEnrollment(
            @Valid @RequestBody LevelEnrollmentDTO dto) {
        log.info("POST /level-enrollments - Creating new level enrollment");

        CourseEnrollment courseEnrollment = courseEnrollmentRepository.findById(dto.getCourseEnrollmentId())
                .orElseThrow(() -> new ResourceNotFoundException("CourseEnrollment", dto.getCourseEnrollmentId()));

        Level level = levelRepository.findById(dto.getLevelId())
                .orElseThrow(() -> new ResourceNotFoundException("Level", dto.getLevelId()));

        AcademicPeriod period = academicPeriodRepository.findById(dto.getAcademicPeriodId())
                .orElseThrow(() -> new ResourceNotFoundException("AcademicPeriod", dto.getAcademicPeriodId()));

        LevelEnrollment.LevelEnrollmentBuilder enrollmentBuilder = LevelEnrollment.builder()
                .courseEnrollment(courseEnrollment)
                .level(level)
                .academicPeriod(period)
                .enrollmentDate(dto.getEnrollmentDate())
                .status(dto.getStatus());

        if (dto.getGroupId() != null) {
            CourseGroup group = courseGroupRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("CourseGroup", dto.getGroupId()));
            enrollmentBuilder.group(group);
        }

        LevelEnrollment enrollment = enrollmentBuilder.build();
        LevelEnrollment saved = levelEnrollmentService.createLevelEnrollment(enrollment);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(LevelEnrollmentResponseDTO.fromEntity(saved),
                        "Level enrollment created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LevelEnrollmentResponseDTO>> updateLevelEnrollment(
            @PathVariable Long id,
            @Valid @RequestBody LevelEnrollmentDTO dto) {
        log.info("PUT /level-enrollments/{} - Updating level enrollment", id);

        LevelEnrollment updates = LevelEnrollment.builder()
                .status(dto.getStatus())
                .build();

        LevelEnrollment updated = levelEnrollmentService.updateLevelEnrollment(id, updates);
        return ResponseEntity.ok(ApiResponse.success(LevelEnrollmentResponseDTO.fromEntity(updated),
                "Level enrollment updated successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<LevelEnrollmentResponseDTO>> updateLevelEnrollmentStatus(
            @PathVariable Long id,
            @RequestParam LevelEnrollment.LevelStatus status) {
        log.info("PATCH /level-enrollments/{}/status - Updating status to: {}", id, status);
        LevelEnrollment updated = levelEnrollmentService.updateLevelEnrollmentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(LevelEnrollmentResponseDTO.fromEntity(updated),
                "Level enrollment status updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLevelEnrollment(@PathVariable Long id) {
        log.info("DELETE /level-enrollments/{} - Deleting level enrollment", id);
        levelEnrollmentService.deleteLevelEnrollment(id);
        return ResponseEntity.ok(ApiResponse.success("Level enrollment deleted successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countLevelEnrollments() {
        log.info("GET /level-enrollments/count - Counting level enrollments");
        long count = levelEnrollmentService.countLevelEnrollments();
        return ResponseEntity.ok(ApiResponse.success(count, "Count retrieved successfully"));
    }
}
