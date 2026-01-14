package com.cesde.studentinfo.controller;

import com.cesde.studentinfo.dto.ApiResponse;
import com.cesde.studentinfo.dto.CourseGroupDTO;
import com.cesde.studentinfo.dto.CourseGroupResponseDTO;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.*;
import com.cesde.studentinfo.repository.*;
import com.cesde.studentinfo.service.CourseGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller para gestión de grupos de curso
 */
@RestController
@RequestMapping("/course-groups")
@RequiredArgsConstructor
@Slf4j
public class CourseGroupController {

    private final CourseGroupService courseGroupService;
    private final CourseRepository courseRepository;
    private final LevelRepository levelRepository;
    private final AcademicPeriodRepository academicPeriodRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseGroupResponseDTO>>> getAllCourseGroups() {
        log.info("GET /course-groups - Fetching all course groups");
        List<CourseGroup> groups = courseGroupService.getAllCourseGroups();
        List<CourseGroupResponseDTO> response = groups.stream()
                .map(CourseGroupResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Course groups retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseGroupResponseDTO>> getCourseGroupById(@PathVariable Long id) {
        log.info("GET /course-groups/{} - Fetching course group by ID", id);
        CourseGroup group = courseGroupService.getCourseGroupById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseGroup", id));
        return ResponseEntity.ok(ApiResponse.success(CourseGroupResponseDTO.fromEntity(group)));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<CourseGroupResponseDTO>>> getCourseGroupsByCourse(
            @PathVariable Long courseId) {
        log.info("GET /course-groups/course/{} - Fetching groups by course", courseId);
        List<CourseGroup> groups = courseGroupService.getCourseGroupsByCourseId(courseId);
        List<CourseGroupResponseDTO> response = groups.stream()
                .map(CourseGroupResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Course groups retrieved successfully"));
    }

    @GetMapping("/period/{periodId}")
    public ResponseEntity<ApiResponse<List<CourseGroupResponseDTO>>> getCourseGroupsByPeriod(
            @PathVariable Long periodId) {
        log.info("GET /course-groups/period/{} - Fetching groups by period", periodId);
        List<CourseGroup> groups = courseGroupService.getCourseGroupsByPeriodId(periodId);
        List<CourseGroupResponseDTO> response = groups.stream()
                .map(CourseGroupResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Course groups retrieved successfully"));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<CourseGroupResponseDTO>>> getAvailableCourseGroups() {
        log.info("GET /course-groups/available - Fetching available groups");
        List<CourseGroup> groups = courseGroupService.getAvailableCourseGroups();
        List<CourseGroupResponseDTO> response = groups.stream()
                .map(CourseGroupResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Available course groups retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CourseGroupResponseDTO>> createCourseGroup(
            @Valid @RequestBody CourseGroupDTO dto) {
        log.info("POST /course-groups - Creating new course group");

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", dto.getCourseId()));

        Level level = levelRepository.findById(dto.getLevelId())
                .orElseThrow(() -> new ResourceNotFoundException("Level", dto.getLevelId()));

        AcademicPeriod period = academicPeriodRepository.findById(dto.getAcademicPeriodId())
                .orElseThrow(() -> new ResourceNotFoundException("AcademicPeriod", dto.getAcademicPeriodId()));

        CourseGroup group = CourseGroup.builder()
                .course(course)
                .level(level)
                .academicPeriod(period)
                .groupCode(dto.getGroupCode())
                .groupName(dto.getGroupName())
                .maxStudents(dto.getMaxStudents())
                .scheduleShift(dto.getScheduleShift())
                .description(dto.getDescription())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();

        CourseGroup saved = courseGroupService.createCourseGroup(group);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(CourseGroupResponseDTO.fromEntity(saved),
                        "Course group created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseGroupResponseDTO>> updateCourseGroup(
            @PathVariable Long id,
            @Valid @RequestBody CourseGroupDTO dto) {
        log.info("PUT /course-groups/{} - Updating course group", id);

        CourseGroup updates = CourseGroup.builder()
                .groupName(dto.getGroupName())
                .maxStudents(dto.getMaxStudents())
                .scheduleShift(dto.getScheduleShift())
                .description(dto.getDescription())
                .isActive(dto.getIsActive())
                .build();

        CourseGroup updated = courseGroupService.updateCourseGroup(id, updates);
        return ResponseEntity.ok(ApiResponse.success(CourseGroupResponseDTO.fromEntity(updated),
                "Course group updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourseGroup(@PathVariable Long id) {
        log.info("DELETE /course-groups/{} - Deleting course group", id);
        courseGroupService.deleteCourseGroup(id);
        return ResponseEntity.ok(ApiResponse.success("Course group deleted successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countCourseGroups() {
        log.info("GET /course-groups/count - Counting course groups");
        long count = courseGroupService.countCourseGroups();
        return ResponseEntity.ok(ApiResponse.success(count, "Count retrieved successfully"));
    }
}
