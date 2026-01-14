package com.cesde.studentinfo.controller;

import com.cesde.studentinfo.dto.ApiResponse;
import com.cesde.studentinfo.dto.AttendanceDTO;
import com.cesde.studentinfo.dto.AttendanceResponseDTO;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.Attendance;
import com.cesde.studentinfo.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller para gestión de asistencia
 */
@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAllAttendance() {
        log.info("GET /attendance - Fetching all attendance records");
        List<Attendance> attendances = attendanceService.getAllAttendance();
        List<AttendanceResponseDTO> response = attendances.stream()
                .map(AttendanceResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Attendance records retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> getAttendanceById(@PathVariable Long id) {
        log.info("GET /attendance/{} - Fetching attendance by ID", id);
        Attendance attendance = attendanceService.getAttendanceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance", id));
        return ResponseEntity.ok(ApiResponse.success(AttendanceResponseDTO.fromEntity(attendance)));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAttendanceByStudent(@PathVariable Long studentId) {
        log.info("GET /attendance/student/{} - Fetching attendance by student", studentId);
        List<Attendance> attendances = attendanceService.getAttendanceByStudentId(studentId);
        List<AttendanceResponseDTO> response = attendances.stream()
                .map(AttendanceResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Student attendance retrieved successfully"));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAttendanceBySession(@PathVariable Long sessionId) {
        log.info("GET /attendance/session/{} - Fetching attendance by session", sessionId);
        List<Attendance> attendances = attendanceService.getAttendanceBySessionId(sessionId);
        List<AttendanceResponseDTO> response = attendances.stream()
                .map(AttendanceResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Session attendance retrieved successfully"));
    }

    @GetMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAttendanceByEnrollment(@PathVariable Long enrollmentId) {
        log.info("GET /attendance/enrollment/{} - Fetching attendance by enrollment", enrollmentId);
        List<Attendance> attendances = attendanceService.getAttendanceByEnrollmentId(enrollmentId);
        List<AttendanceResponseDTO> response = attendances.stream()
                .map(AttendanceResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Enrollment attendance retrieved successfully"));
    }

    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAttendanceByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("GET /attendance/range?startDate={}&endDate={} - Fetching attendance by date range", startDate, endDate);
        List<Attendance> attendances = attendanceService.getAttendanceByDateRange(startDate, endDate);
        List<AttendanceResponseDTO> response = attendances.stream()
                .map(AttendanceResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Attendance records retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> createAttendance(@Valid @RequestBody AttendanceDTO dto) {
        log.info("POST /attendance - Creating new attendance record");

        // Nota: Aquí necesitarías buscar las entidades relacionadas
        // Por simplicidad del ejemplo, el builder necesita las entidades completas
        Attendance attendance = Attendance.builder()
                .assignmentDate(dto.getAssignmentDate())
                .status(dto.getStatus())
                .isExcused(dto.getIsExcused() != null ? dto.getIsExcused() : false)
                .excuseReason(dto.getExcuseReason())
                .notes(dto.getNotes())
                .build();

        Attendance saved = attendanceService.createAttendance(attendance);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(AttendanceResponseDTO.fromEntity(saved), "Attendance created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> updateAttendance(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceDTO dto) {
        log.info("PUT /attendance/{} - Updating attendance", id);

        Attendance updates = Attendance.builder()
                .status(dto.getStatus())
                .isExcused(dto.getIsExcused())
                .excuseReason(dto.getExcuseReason())
                .notes(dto.getNotes())
                .build();

        Attendance updated = attendanceService.updateAttendance(id, updates);
        return ResponseEntity.ok(ApiResponse.success(AttendanceResponseDTO.fromEntity(updated),
                "Attendance updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAttendance(@PathVariable Long id) {
        log.info("DELETE /attendance/{} - Deleting attendance", id);
        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok(ApiResponse.success("Attendance deleted successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countAttendance() {
        log.info("GET /attendance/count - Counting attendance records");
        long count = attendanceService.countAttendance();
        return ResponseEntity.ok(ApiResponse.success(count, "Count retrieved successfully"));
    }
}

