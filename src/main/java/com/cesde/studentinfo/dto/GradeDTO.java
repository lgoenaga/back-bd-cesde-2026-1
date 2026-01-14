package com.cesde.studentinfo.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para crear/actualizar calificaciones (Request)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeDTO {

    @NotNull(message = "Subject Enrollment ID is required")
    private Long subjectEnrollmentId;

    @NotNull(message = "Grade Period ID is required")
    private Long gradePeriodId;

    @NotNull(message = "Grade Component ID is required")
    private Long gradeComponentId;

    @NotNull(message = "Grade value is required")
    @DecimalMin(value = "0.00", message = "Grade must be at least 0.00")
    @DecimalMax(value = "5.00", message = "Grade must be at most 5.00")
    private BigDecimal gradeValue;

    private LocalDate assignmentDate;

    private String comments;
}
