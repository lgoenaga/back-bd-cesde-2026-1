package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.ClassSession;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para crear/actualizar sesiones de clase (Request)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassSessionDTO {

    @NotNull(message = "Subject Assignment ID is required")
    private Long subjectAssignmentId;

    @NotNull(message = "Session date is required")
    private LocalDate sessionDate;

    @NotNull(message = "Session time is required")
    private LocalTime sessionTime;

    @Positive(message = "Duration must be positive")
    @Builder.Default
    private Integer durationMinutes = 120;

    @Size(max = 200, message = "Topic must not exceed 200 characters")
    private String topic;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private ClassSession.SessionStatus status;
}
