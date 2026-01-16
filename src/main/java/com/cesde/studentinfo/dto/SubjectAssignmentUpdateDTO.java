package com.cesde.studentinfo.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing SubjectAssignment
 * Used in PUT requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectAssignmentUpdateDTO {

    @Size(max = 200, message = "Schedule cannot exceed 200 characters")
    private String schedule;

    @Size(max = 50, message = "Classroom cannot exceed 50 characters")
    private String classroom;

    @Positive(message = "Max students must be positive")
    private Integer maxStudents;

    private Boolean isActive;
}
