package com.cesde.studentinfo.controller;

import com.cesde.studentinfo.dto.AcademicPeriodDTO;
import com.cesde.studentinfo.dto.AcademicPeriodResponseDTO;
import com.cesde.studentinfo.dto.ApiResponse;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.AcademicPeriod;
import com.cesde.studentinfo.service.AcademicPeriodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller para gestión de períodos académicos
 */
@RestController
@RequestMapping("/academic-periods")
@RequiredArgsConstructor
@Slf4j
public class AcademicPeriodController {

    private final AcademicPeriodService academicPeriodService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AcademicPeriodResponseDTO>>> getAllPeriods() {
        log.info("GET /academic-periods - Fetching all periods");
        List<AcademicPeriod> periods = academicPeriodService.getAllPeriods();
        List<AcademicPeriodResponseDTO> response = periods.stream()
                .map(AcademicPeriodResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Academic periods retrieved successfully"));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<AcademicPeriodResponseDTO>>> getActivePeriods() {
        log.info("GET /academic-periods/active - Fetching active periods");
        List<AcademicPeriod> periods = academicPeriodService.getActivePeriods();
        List<AcademicPeriodResponseDTO> response = periods.stream()
                .map(AcademicPeriodResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Active periods retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AcademicPeriodResponseDTO>> getPeriodById(@PathVariable Long id) {
        log.info("GET /academic-periods/{} - Fetching period by ID", id);
        AcademicPeriod period = academicPeriodService.getPeriodById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicPeriod", id));
        return ResponseEntity.ok(ApiResponse.success(AcademicPeriodResponseDTO.fromEntity(period)));
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<AcademicPeriodResponseDTO>> getCurrentPeriod() {
        log.info("GET /academic-periods/current - Fetching current period");
        AcademicPeriod period = academicPeriodService.getCurrentPeriod()
                .orElseThrow(() -> new ResourceNotFoundException("No current academic period found"));
        return ResponseEntity.ok(ApiResponse.success(AcademicPeriodResponseDTO.fromEntity(period)));
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<ApiResponse<List<AcademicPeriodResponseDTO>>> getPeriodsByYear(@PathVariable int year) {
        log.info("GET /academic-periods/year/{} - Fetching periods by year", year);
        List<AcademicPeriod> periods = academicPeriodService.getPeriodsByYear(year);
        List<AcademicPeriodResponseDTO> response = periods.stream()
                .map(AcademicPeriodResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Periods retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AcademicPeriodResponseDTO>> createPeriod(
            @Valid @RequestBody AcademicPeriodDTO dto) {
        log.info("POST /academic-periods - Creating new period");

        AcademicPeriod period = AcademicPeriod.builder()
                .name(dto.getName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();

        AcademicPeriod saved = academicPeriodService.createPeriod(period);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(AcademicPeriodResponseDTO.fromEntity(saved),
                        "Academic period created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AcademicPeriodResponseDTO>> updatePeriod(
            @PathVariable Long id,
            @Valid @RequestBody AcademicPeriodDTO dto) {
        log.info("PUT /academic-periods/{} - Updating period", id);

        AcademicPeriod updates = AcademicPeriod.builder()
                .name(dto.getName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .isActive(dto.getIsActive())
                .build();

        AcademicPeriod updated = academicPeriodService.updatePeriod(id, updates);
        return ResponseEntity.ok(ApiResponse.success(AcademicPeriodResponseDTO.fromEntity(updated),
                "Academic period updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePeriod(@PathVariable Long id) {
        log.info("DELETE /academic-periods/{} - Deleting period", id);
        academicPeriodService.deletePeriod(id);
        return ResponseEntity.ok(ApiResponse.success("Academic period deleted successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countPeriods() {
        log.info("GET /academic-periods/count - Counting periods");
        long count = academicPeriodService.countPeriods();
        return ResponseEntity.ok(ApiResponse.success(count, "Count retrieved successfully"));
    }
}
