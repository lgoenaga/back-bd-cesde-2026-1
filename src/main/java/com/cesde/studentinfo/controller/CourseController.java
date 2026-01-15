package com.cesde.studentinfo.controller;

import com.cesde.studentinfo.dto.ApiResponse;
import com.cesde.studentinfo.dto.CourseDTO;
import com.cesde.studentinfo.dto.CourseResponseDTO;
import com.cesde.studentinfo.dto.PagedResponse;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.Course;
import com.cesde.studentinfo.service.CourseService;
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
 * Controller para gestión de Cursos
 * Expone endpoints REST para operaciones CRUD de cursos
 */
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponseDTO>>> getAllCourses() {
        log.info("GET /courses - Fetching all courses");
        List<Course> courses = courseService.getAllCourses();
        List<CourseResponseDTO> response = courses.stream()
                .map(CourseResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Courses retrieved successfully"));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CourseResponseDTO>>> getActiveCourses() {
        log.info("GET /courses/active - Fetching active courses");
        List<Course> courses = courseService.getActiveCourses();
        List<CourseResponseDTO> response = courses.stream()
                .map(CourseResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Active courses retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponseDTO>> getCourseById(@PathVariable Long id) {
        log.info("GET /courses/{} - Fetching course by ID", id);
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
        return ResponseEntity.ok(ApiResponse.success(CourseResponseDTO.fromEntity(course)));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<CourseResponseDTO>> getCourseByCode(@PathVariable String code) {
        log.info("GET /courses/code/{} - Fetching course by code", code);
        Course course = courseService.getCourseByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "code", code));
        return ResponseEntity.ok(ApiResponse.success(CourseResponseDTO.fromEntity(course)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CourseResponseDTO>>> searchCourses(@RequestParam String name) {
        log.info("GET /courses/search?name={} - Searching courses by name", name);
        List<Course> courses = courseService.searchCoursesByName(name);
        List<CourseResponseDTO> response = courses.stream()
                .map(CourseResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Search completed successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponseDTO>> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        log.info("POST /courses - Creating new course: {}", courseDTO.getCode());

        Course course = Course.builder()
                .name(courseDTO.getName())
                .code(courseDTO.getCode())
                .description(courseDTO.getDescription())
                .totalLevels(courseDTO.getTotalLevels())
                .isActive(courseDTO.getIsActive() != null ? courseDTO.getIsActive() : true)
                .build();

        Course savedCourse = courseService.createCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(CourseResponseDTO.fromEntity(savedCourse), "Course created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponseDTO>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDTO courseDTO) {
        log.info("PUT /courses/{} - Updating course", id);

        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));

        course.setName(courseDTO.getName());
        course.setCode(courseDTO.getCode());
        course.setDescription(courseDTO.getDescription());
        course.setTotalLevels(courseDTO.getTotalLevels());
        if (courseDTO.getIsActive() != null) {
            course.setIsActive(courseDTO.getIsActive());
        }

        Course updatedCourse = courseService.updateCourse(course);
        return ResponseEntity.ok(ApiResponse.success(CourseResponseDTO.fromEntity(updatedCourse), "Course updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        log.info("DELETE /courses/{} - Deleting course", id);
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success("Course deleted successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<CourseResponseDTO>> deactivateCourse(@PathVariable Long id) {
        log.info("PATCH /courses/{}/deactivate - Deactivating course", id);
        courseService.deactivateCourse(id);
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
        return ResponseEntity.ok(ApiResponse.success(CourseResponseDTO.fromEntity(course), "Course deactivated successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countCourses() {
        log.info("GET /courses/count - Counting courses");
        long count = courseService.countCourses();
        return ResponseEntity.ok(ApiResponse.success(count, "Count retrieved successfully"));
    }

    // ==================== PAGINATED ENDPOINTS ====================

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<CourseResponseDTO>>> getAllCoursesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        log.info("GET /courses/paged - Fetching courses page={}, size={}", page, size);
        Pageable pageable = createPageable(page, size, sort);
        Page<Course> coursePage = courseService.getAllCoursesPaginated(pageable);
        PagedResponse<CourseResponseDTO> response = PagedResponse.from(
                coursePage.map(CourseResponseDTO::fromEntity));
        return ResponseEntity.ok(ApiResponse.success(response, "Courses retrieved successfully"));
    }

    @GetMapping("/active/paged")
    public ResponseEntity<ApiResponse<PagedResponse<CourseResponseDTO>>> getActiveCoursesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        log.info("GET /courses/active/paged - Fetching active courses page={}, size={}", page, size);
        Pageable pageable = createPageable(page, size, sort);
        Page<Course> coursePage = courseService.getActiveCoursesPaginated(pageable);
        PagedResponse<CourseResponseDTO> response = PagedResponse.from(
                coursePage.map(CourseResponseDTO::fromEntity));
        return ResponseEntity.ok(ApiResponse.success(response, "Active courses retrieved successfully"));
    }

    @GetMapping("/search/paged")
    public ResponseEntity<ApiResponse<PagedResponse<CourseResponseDTO>>> searchCoursesPaginated(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        log.info("GET /courses/search/paged - Searching courses name={}, page={}, size={}", name, page, size);
        Pageable pageable = createPageable(page, size, sort);
        Page<Course> coursePage = courseService.searchCoursesByNamePaginated(name, pageable);
        PagedResponse<CourseResponseDTO> response = PagedResponse.from(
                coursePage.map(CourseResponseDTO::fromEntity));
        return ResponseEntity.ok(ApiResponse.success(response, "Search completed successfully"));
    }

    private Pageable createPageable(int page, int size, String[] sort) {
        String sortBy = sort.length > 0 ? sort[0] : "id";
        String direction = sort.length > 1 ? sort[1] : "desc";
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    }
}

