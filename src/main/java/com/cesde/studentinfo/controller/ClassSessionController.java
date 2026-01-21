package com.cesde.studentinfo.controller;

import com.cesde.studentinfo.dto.ApiResponse;
import com.cesde.studentinfo.dto.ClassSessionDTO;
import com.cesde.studentinfo.dto.ClassSessionResponseDTO;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.ClassSession;
import com.cesde.studentinfo.model.SubjectAssignment;
import com.cesde.studentinfo.repository.SubjectAssignmentRepository;
import com.cesde.studentinfo.service.ClassSessionService;
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
 * Controller para gestión de sesiones de clase
 */
@RestController
@RequestMapping("/class-sessions")
@RequiredArgsConstructor
@Slf4j
public class ClassSessionController {

    private final ClassSessionService classSessionService;
    private final SubjectAssignmentRepository subjectAssignmentRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClassSessionResponseDTO>>> getAllSessions() {
        log.info("GET /class-sessions - Fetching all class sessions");
        List<ClassSession> sessions = classSessionService.getAllSessions();
        List<ClassSessionResponseDTO> response = sessions.stream()
                .map(ClassSessionResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Class sessions retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassSessionResponseDTO>> getSessionById(@PathVariable Long id) {
        log.info("GET /class-sessions/{} - Fetching class session by ID", id);
        ClassSession session = classSessionService.getSessionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClassSession", id));
        return ResponseEntity.ok(ApiResponse.success(ClassSessionResponseDTO.fromEntity(session)));
    }

    @GetMapping("/by-assignment/{assignmentId}")
    public ResponseEntity<ApiResponse<List<ClassSessionResponseDTO>>> getSessionsByAssignment(
            @PathVariable Long assignmentId) {
        log.info("GET /class-sessions/by-assignment/{} - Fetching sessions by assignment", assignmentId);
        List<ClassSession> sessions = classSessionService.getSessionsBySubjectAssignment(assignmentId);
        List<ClassSessionResponseDTO> response = sessions.stream()
                .map(ClassSessionResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Sessions retrieved successfully"));
    }

    @GetMapping("/by-date")
    public ResponseEntity<ApiResponse<List<ClassSessionResponseDTO>>> getSessionsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("GET /class-sessions/by-date?date={} - Fetching sessions by date", date);
        List<ClassSession> sessions = classSessionService.getSessionsByDate(date);
        List<ClassSessionResponseDTO> response = sessions.stream()
                .map(ClassSessionResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Sessions retrieved successfully"));
    }

    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<ClassSessionResponseDTO>>> getSessionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("GET /class-sessions/range?startDate={}&endDate={} - Fetching sessions by date range",
                 startDate, endDate);
        List<ClassSession> sessions = classSessionService.getSessionsByDateRange(startDate, endDate);
        List<ClassSessionResponseDTO> response = sessions.stream()
                .map(ClassSessionResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Sessions retrieved successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<ClassSessionResponseDTO>> searchSession(
            @RequestParam Long assignmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("GET /class-sessions/search?assignmentId={}&date={} - Searching for specific session",
                 assignmentId, date);
        ClassSession session = classSessionService.findBySubjectAssignmentAndDate(assignmentId, date)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "ClassSession not found for assignment " + assignmentId + " on date " + date));
        return ResponseEntity.ok(ApiResponse.success(ClassSessionResponseDTO.fromEntity(session)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClassSessionResponseDTO>> createSession(@Valid @RequestBody ClassSessionDTO dto) {
        log.info("POST /class-sessions - Creating new class session");

        // Buscar la asignación de materia
        SubjectAssignment assignment = subjectAssignmentRepository.findById(dto.getSubjectAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("SubjectAssignment", dto.getSubjectAssignmentId()));

        // Construir la entidad ClassSession
        ClassSession session = ClassSession.builder()
                .subjectAssignment(assignment)
                .sessionDate(dto.getSessionDate())
                .sessionTime(dto.getSessionTime())
                .durationMinutes(dto.getDurationMinutes() != null ? dto.getDurationMinutes() : 120)
                .topic(dto.getTopic())
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? dto.getStatus() : ClassSession.SessionStatus.PROGRAMADA)
                .build();

        ClassSession saved = classSessionService.createSession(session);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ClassSessionResponseDTO.fromEntity(saved),
                      "Class session created successfully"));
    }

    @PostMapping("/find-or-create")
    public ResponseEntity<ApiResponse<ClassSessionResponseDTO>> findOrCreateSession(
            @Valid @RequestBody ClassSessionDTO dto) {
        log.info("POST /class-sessions/find-or-create - Finding or creating class session for assignment {} on date {}",
                 dto.getSubjectAssignmentId(), dto.getSessionDate());

        ClassSession session = classSessionService.findOrCreateSession(
            dto.getSubjectAssignmentId(),
            dto.getSessionDate(),
            dto.getSessionTime(),
            dto.getTopic()
        );

        return ResponseEntity.ok(ApiResponse.success(
            ClassSessionResponseDTO.fromEntity(session),
            "Class session retrieved or created successfully"
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassSessionResponseDTO>> updateSession(
            @PathVariable Long id,
            @Valid @RequestBody ClassSessionDTO dto) {
        log.info("PUT /class-sessions/{} - Updating class session", id);

        ClassSession updates = ClassSession.builder()
                .sessionDate(dto.getSessionDate())
                .sessionTime(dto.getSessionTime())
                .durationMinutes(dto.getDurationMinutes())
                .topic(dto.getTopic())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .build();

        ClassSession updated = classSessionService.updateSession(id, updates);
        return ResponseEntity.ok(ApiResponse.success(ClassSessionResponseDTO.fromEntity(updated),
                "Class session updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable Long id) {
        log.info("DELETE /class-sessions/{} - Deleting class session", id);
        classSessionService.deleteSession(id);
        return ResponseEntity.ok(ApiResponse.success("Class session deleted successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countSessions() {
        log.info("GET /class-sessions/count - Counting class sessions");
        long count = classSessionService.countSessions();
        return ResponseEntity.ok(ApiResponse.success(count, "Count retrieved successfully"));
    }
}
