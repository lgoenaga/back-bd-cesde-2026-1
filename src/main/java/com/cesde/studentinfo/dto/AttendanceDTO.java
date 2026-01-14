package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.Attendance;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para crear/actualizar asistencia (Request)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {

    @NotNull(message = "Subject Enrollment ID is required")
    private Long subjectEnrollmentId;

    @NotNull(message = "Class Session ID is required")
    private Long classSessionId;

    @NotNull(message = "Assignment date is required")
    private LocalDate assignmentDate;

    @NotNull(message = "Status is required")
    private Attendance.AttendanceStatus status;

    private Boolean isExcused;

    private String excuseReason;

    private String notes;
}

