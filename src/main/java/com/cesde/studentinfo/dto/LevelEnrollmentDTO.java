package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.LevelEnrollment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para crear/actualizar inscripciones a niveles (Request)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LevelEnrollmentDTO {

    @NotNull(message = "Course Enrollment ID is required")
    private Long courseEnrollmentId;

    @NotNull(message = "Level ID is required")
    private Long levelId;

    @NotNull(message = "Academic Period ID is required")
    private Long academicPeriodId;

    private Long groupId;

    private LocalDate enrollmentDate;

    private LevelEnrollment.LevelStatus status;
}
