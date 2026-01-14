package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.CourseEnrollment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para crear/actualizar inscripciones (Request)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseEnrollmentDTO {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Academic Period ID is required")
    private Long academicPeriodId;

    private LocalDate enrollmentDate;

    private CourseEnrollment.EnrollmentStatus enrollmentStatus;

    private String notes;
}
