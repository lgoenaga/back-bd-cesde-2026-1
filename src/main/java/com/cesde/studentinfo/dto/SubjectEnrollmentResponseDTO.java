package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.SubjectEnrollment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de inscripciones a materias (Response)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectEnrollmentResponseDTO {

    private Long id;
    private Long levelEnrollmentId;
    private String studentName;

    // Subject information (always present)
    private Long subjectId;
    private String subjectName;
    private String subjectCode;

    // Professor/Assignment information (optional - may be null)
    private Long subjectAssignmentId;
    private String professorName;
    private String schedule;
    private String classroom;

    private LocalDate enrollmentDate;
    private SubjectEnrollment.SubjectStatus status;
    private BigDecimal finalGrade;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SubjectEnrollmentResponseDTO fromEntity(SubjectEnrollment enrollment) {
        SubjectEnrollmentResponseDTOBuilder builder = SubjectEnrollmentResponseDTO.builder()
                .id(enrollment.getId())
                .levelEnrollmentId(enrollment.getLevelEnrollment().getId())
                .studentName(enrollment.getLevelEnrollment().getCourseEnrollment().getStudent().getFirstName() + " " +
                           enrollment.getLevelEnrollment().getCourseEnrollment().getStudent().getLastName())

                // Subject information (always present)
                .subjectId(enrollment.getSubject().getId())
                .subjectName(enrollment.getSubject().getName())
                .subjectCode(enrollment.getSubject().getCode())

                .enrollmentDate(enrollment.getEnrollmentDate())
                .status(enrollment.getStatus())
                .finalGrade(enrollment.getFinalGrade())
                .createdAt(enrollment.getCreatedAt())
                .updatedAt(enrollment.getUpdatedAt());

        // Professor/Assignment information (optional)
        if (enrollment.getSubjectAssignment() != null) {
            builder
                .subjectAssignmentId(enrollment.getSubjectAssignment().getId())
                .professorName(enrollment.getSubjectAssignment().getProfessor().getFirstName() + " " +
                             enrollment.getSubjectAssignment().getProfessor().getLastName())
                .schedule(enrollment.getSubjectAssignment().getSchedule())
                .classroom(enrollment.getSubjectAssignment().getClassroom());
        }

        return builder.build();
    }
}
