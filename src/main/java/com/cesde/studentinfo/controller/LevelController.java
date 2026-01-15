package com.cesde.studentinfo.controller;

import com.cesde.studentinfo.dto.ApiResponse;
import com.cesde.studentinfo.dto.LevelDTO;
import com.cesde.studentinfo.dto.LevelResponseDTO;
import com.cesde.studentinfo.dto.PagedResponse;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.Course;
import com.cesde.studentinfo.model.Level;
import com.cesde.studentinfo.repository.CourseRepository;
import com.cesde.studentinfo.service.LevelService;
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
 * Controller para gestión de niveles
 */
@RestController
@RequestMapping("/levels")
@RequiredArgsConstructor
@Slf4j
public class LevelController {

    private final LevelService levelService;
    private final CourseRepository courseRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<LevelResponseDTO>>> getAllLevels() {
        log.info("GET /levels - Fetching all levels");
        List<Level> levels = levelService.getAllLevels();
        List<LevelResponseDTO> response = levels.stream()
                .map(LevelResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Levels retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LevelResponseDTO>> getLevelById(@PathVariable Long id) {
        log.info("GET /levels/{} - Fetching level by ID", id);
        Level level = levelService.getLevelById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Level", id));
        return ResponseEntity.ok(ApiResponse.success(LevelResponseDTO.fromEntity(level)));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<LevelResponseDTO>>> getLevelsByCourse(@PathVariable Long courseId) {
        log.info("GET /levels/course/{} - Fetching levels by course", courseId);
        List<Level> levels = levelService.getLevelsByCourseId(courseId);
        List<LevelResponseDTO> response = levels.stream()
                .map(LevelResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Levels retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LevelResponseDTO>> createLevel(@Valid @RequestBody LevelDTO dto) {
        log.info("POST /levels - Creating new level");

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", dto.getCourseId()));

        Level level = Level.builder()
                .course(course)
                .levelNumber(dto.getLevelNumber())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();

        Level saved = levelService.createLevel(level);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(LevelResponseDTO.fromEntity(saved), "Level created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LevelResponseDTO>> updateLevel(
            @PathVariable Long id,
            @Valid @RequestBody LevelDTO dto) {
        log.info("PUT /levels/{} - Updating level", id);

        Level updates = Level.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();

        Level updated = levelService.updateLevel(id, updates);
        return ResponseEntity.ok(ApiResponse.success(LevelResponseDTO.fromEntity(updated),
                "Level updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLevel(@PathVariable Long id) {
        log.info("DELETE /levels/{} - Deleting level", id);
        levelService.deleteLevel(id);
        return ResponseEntity.ok(ApiResponse.success("Level deleted successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countLevels() {
        log.info("GET /levels/count - Counting levels");
        long count = levelService.countLevels();
        return ResponseEntity.ok(ApiResponse.success(count, "Count retrieved successfully"));
    }

    // ==================== PAGINATED ENDPOINTS ====================

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<LevelResponseDTO>>> getAllLevelsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        log.info("GET /levels/paged - Fetching levels page={}, size={}", page, size);
        Pageable pageable = createPageable(page, size, sort);
        Page<Level> levelPage = levelService.getAllLevelsPaginated(pageable);
        PagedResponse<LevelResponseDTO> response = PagedResponse.from(
                levelPage.map(LevelResponseDTO::fromEntity));
        return ResponseEntity.ok(ApiResponse.success(response, "Levels retrieved successfully"));
    }

    @GetMapping("/course/{courseId}/paged")
    public ResponseEntity<ApiResponse<PagedResponse<LevelResponseDTO>>> getLevelsByCoursePaginated(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "levelNumber,asc") String[] sort) {

        log.info("GET /levels/course/{}/paged - Fetching levels by course page={}, size={}", courseId, page, size);
        Pageable pageable = createPageable(page, size, sort);
        Page<Level> levelPage = levelService.getLevelsByCoursePaginated(courseId, pageable);
        PagedResponse<LevelResponseDTO> response = PagedResponse.from(
                levelPage.map(LevelResponseDTO::fromEntity));
        return ResponseEntity.ok(ApiResponse.success(response, "Levels retrieved successfully"));
    }

    private Pageable createPageable(int page, int size, String[] sort) {
        String sortBy = sort.length > 0 ? sort[0] : "id";
        String direction = sort.length > 1 ? sort[1] : "desc";
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    }
}
