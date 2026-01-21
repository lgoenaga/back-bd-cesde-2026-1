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
    private Long subjectAssignmentId;
    private String subjectName;
    private String professorName;
    private LocalDate enrollmentDate;
    private SubjectEnrollment.SubjectStatus status;
    private BigDecimal finalGrade;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SubjectEnrollmentResponseDTO fromEntity(SubjectEnrollment enrollment) {
        return SubjectEnrollmentResponseDTO.builder()
                .id(enrollment.getId())
                .levelEnrollmentId(enrollment.getLevelEnrollment().getId())
                .studentName(enrollment.getLevelEnrollment().getCourseEnrollment().getStudent().getFirstName() + " " +
                           enrollment.getLevelEnrollment().getCourseEnrollment().getStudent().getLastName())
                .subjectAssignmentId(enrollment.getSubjectAssignment().getId())
                .subjectName(enrollment.getSubjectAssignment().getSubject().getName())
                .professorName(enrollment.getSubjectAssignment().getProfessor().getFirstName() + " " +
                             enrollment.getSubjectAssignment().getProfessor().getLastName())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .status(enrollment.getStatus())
                .finalGrade(enrollment.getFinalGrade())
                .createdAt(enrollment.getCreatedAt())
                .updatedAt(enrollment.getUpdatedAt())
                .build();
    }
}
