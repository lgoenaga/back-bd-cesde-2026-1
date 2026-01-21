package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.SubjectEnrollment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para crear/actualizar inscripciones a materias (Request)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectEnrollmentDTO {

    @NotNull(message = "Level Enrollment ID is required")
    private Long levelEnrollmentId;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    private Long subjectAssignmentId; // Optional - for professor assignment/traceability

    private LocalDate enrollmentDate;

    private SubjectEnrollment.SubjectStatus status;
}
