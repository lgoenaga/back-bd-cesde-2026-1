package com.cesde.studentinfo.controller;

import com.cesde.studentinfo.dto.ApiResponse;
import com.cesde.studentinfo.dto.PagedResponse;
import com.cesde.studentinfo.dto.SubjectDTO;
import com.cesde.studentinfo.dto.SubjectResponseDTO;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.Level;
import com.cesde.studentinfo.model.Subject;
import com.cesde.studentinfo.repository.LevelRepository;
import com.cesde.studentinfo.service.SubjectService;
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
 * Controller para gestión de materias
 */
@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
@Slf4j
public class SubjectController {

    private final SubjectService subjectService;
    private final LevelRepository levelRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectResponseDTO>>> getAllSubjects() {
        log.info("GET /subjects - Fetching all subjects");
        List<Subject> subjects = subjectService.getAllSubjects();
        List<SubjectResponseDTO> response = subjects.stream()
                .map(SubjectResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Subjects retrieved successfully"));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SubjectResponseDTO>>> getActiveSubjects() {
        log.info("GET /subjects/active - Fetching active subjects");
        List<Subject> subjects = subjectService.getAllActiveSubjects();
        List<SubjectResponseDTO> response = subjects.stream()
                .map(SubjectResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Active subjects retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> getSubjectById(@PathVariable Long id) {
        log.info("GET /subjects/{} - Fetching subject by ID", id);
        Subject subject = subjectService.getSubjectById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));
        return ResponseEntity.ok(ApiResponse.success(SubjectResponseDTO.fromEntity(subject)));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> getSubjectByCode(@PathVariable String code) {
        log.info("GET /subjects/code/{} - Fetching subject by code", code);
        Subject subject = subjectService.getSubjectByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "code", code));
        return ResponseEntity.ok(ApiResponse.success(SubjectResponseDTO.fromEntity(subject)));
    }

    @GetMapping("/level/{levelId}")
    public ResponseEntity<ApiResponse<List<SubjectResponseDTO>>> getSubjectsByLevel(@PathVariable Long levelId) {
        log.info("GET /subjects/level/{} - Fetching subjects by level", levelId);
        List<Subject> subjects = subjectService.getSubjectsByLevelId(levelId);
        List<SubjectResponseDTO> response = subjects.stream()
                .map(SubjectResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Subjects retrieved successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SubjectResponseDTO>>> searchSubjects(@RequestParam String name) {
        log.info("GET /subjects/search?name={} - Searching subjects", name);
        List<Subject> subjects = subjectService.searchSubjectsByName(name);
        List<SubjectResponseDTO> response = subjects.stream()
                .map(SubjectResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Search completed successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> createSubject(@Valid @RequestBody SubjectDTO dto) {
        log.info("POST /subjects - Creating new subject");

        Level level = levelRepository.findById(dto.getLevelId())
                .orElseThrow(() -> new ResourceNotFoundException("Level", dto.getLevelId()));

        Subject subject = Subject.builder()
                .level(level)
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .credits(dto.getCredits())
                .hoursPerWeek(dto.getHoursPerWeek())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();

        Subject saved = subjectService.createSubject(subject);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(SubjectResponseDTO.fromEntity(saved), "Subject created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> updateSubject(
            @PathVariable Long id,
            @Valid @RequestBody SubjectDTO dto) {
        log.info("PUT /subjects/{} - Updating subject", id);

        Subject updates = Subject.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .credits(dto.getCredits())
                .hoursPerWeek(dto.getHoursPerWeek())
                .isActive(dto.getIsActive())
                .build();

        Subject updated = subjectService.updateSubject(id, updates);
        return ResponseEntity.ok(ApiResponse.success(SubjectResponseDTO.fromEntity(updated),
                "Subject updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubject(@PathVariable Long id) {
        log.info("DELETE /subjects/{} - Deleting subject", id);
        subjectService.deleteSubject(id);
        return ResponseEntity.ok(ApiResponse.success("Subject deleted successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countSubjects() {
        log.info("GET /subjects/count - Counting subjects");
        long count = subjectService.countSubjects();
        return ResponseEntity.ok(ApiResponse.success(count, "Count retrieved successfully"));
    }

    // ==================== PAGINATED ENDPOINTS ====================

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<SubjectResponseDTO>>> getAllSubjectsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        log.info("GET /subjects/paged - Fetching subjects page={}, size={}", page, size);
        Pageable pageable = createPageable(page, size, sort);
        Page<Subject> subjectPage = subjectService.getAllSubjectsPaginated(pageable);
        PagedResponse<SubjectResponseDTO> response = PagedResponse.from(
                subjectPage.map(SubjectResponseDTO::fromEntity));
        return ResponseEntity.ok(ApiResponse.success(response, "Subjects retrieved successfully"));
    }

    @GetMapping("/active/paged")
    public ResponseEntity<ApiResponse<PagedResponse<SubjectResponseDTO>>> getActiveSubjectsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        log.info("GET /subjects/active/paged - Fetching active subjects page={}, size={}", page, size);
        Pageable pageable = createPageable(page, size, sort);
        Page<Subject> subjectPage = subjectService.getAllActiveSubjectsPaginated(pageable);
        PagedResponse<SubjectResponseDTO> response = PagedResponse.from(
                subjectPage.map(SubjectResponseDTO::fromEntity));
        return ResponseEntity.ok(ApiResponse.success(response, "Active subjects retrieved successfully"));
    }

    @GetMapping("/level/{levelId}/paged")
    public ResponseEntity<ApiResponse<PagedResponse<SubjectResponseDTO>>> getSubjectsByLevelPaginated(
            @PathVariable Long levelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        log.info("GET /subjects/level/{}/paged - Fetching subjects by level page={}, size={}", levelId, page, size);
        Pageable pageable = createPageable(page, size, sort);
        Page<Subject> subjectPage = subjectService.getSubjectsByLevelPaginated(levelId, pageable);
        PagedResponse<SubjectResponseDTO> response = PagedResponse.from(
                subjectPage.map(SubjectResponseDTO::fromEntity));
        return ResponseEntity.ok(ApiResponse.success(response, "Subjects retrieved successfully"));
    }

    @GetMapping("/search/paged")
    public ResponseEntity<ApiResponse<PagedResponse<SubjectResponseDTO>>> searchSubjectsPaginated(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        log.info("GET /subjects/search/paged - Searching subjects name={}, page={}, size={}", name, page, size);
        Pageable pageable = createPageable(page, size, sort);
        Page<Subject> subjectPage = subjectService.searchSubjectsByNamePaginated(name, pageable);
        PagedResponse<SubjectResponseDTO> response = PagedResponse.from(
                subjectPage.map(SubjectResponseDTO::fromEntity));
        return ResponseEntity.ok(ApiResponse.success(response, "Search completed successfully"));
    }

    private Pageable createPageable(int page, int size, String[] sort) {
        String sortBy = sort.length > 0 ? sort[0] : "id";
        String direction = sort.length > 1 ? sort[1] : "asc";
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    }
}
