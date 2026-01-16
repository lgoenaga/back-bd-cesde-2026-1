package com.cesde.studentinfo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new SubjectAssignment
 * Used in POST requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectAssignmentRequestDTO {

    @NotNull(message = "Subject ID is required")
    @Positive(message = "Subject ID must be positive")
    private Long subjectId;

    @NotNull(message = "Professor ID is required")
    @Positive(message = "Professor ID must be positive")
    private Long professorId;

    @NotNull(message = "Academic Period ID is required")
    @Positive(message = "Academic Period ID must be positive")
    private Long academicPeriodId;

    private Long groupId;

    @Size(max = 200, message = "Schedule cannot exceed 200 characters")
    private String schedule;

    @Size(max = 50, message = "Classroom cannot exceed 50 characters")
    private String classroom;

    @Positive(message = "Max students must be positive")
    private Integer maxStudents;

    @Builder.Default
    private Boolean isActive = true;
}
