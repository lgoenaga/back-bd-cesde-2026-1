package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de calificaciones (Response)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeResponseDTO {

    private Long id;
    private Long subjectEnrollmentId;
    private Long gradePeriodId;
    private String gradePeriodName;
    private Long gradeComponentId;
    private String gradeComponentName;
    private BigDecimal gradeValue;
    private LocalDate assignmentDate;
    private LocalDateTime updateDate;
    private String comments;
    private Long assignedById;
    private String assignedByName;
    private LocalDateTime createdAt;

    public static GradeResponseDTO fromEntity(Grade grade) {
        return GradeResponseDTO.builder()
                .id(grade.getId())
                .subjectEnrollmentId(grade.getSubjectEnrollment().getId())
                .gradePeriodId(grade.getGradePeriod().getId())
                .gradePeriodName(grade.getGradePeriod().getName())
                .gradeComponentId(grade.getGradeComponent().getId())
                .gradeComponentName(grade.getGradeComponent().getName())
                .gradeValue(grade.getGradeValue())
                .assignmentDate(grade.getAssignmentDate())
                .updateDate(grade.getUpdateDate())
                .comments(grade.getComments())
                .assignedById(grade.getAssignedBy() != null ? grade.getAssignedBy().getId() : null)
                .assignedByName(grade.getAssignedBy() != null ?
                        grade.getAssignedBy().getFirstName() + " " + grade.getAssignedBy().getLastName() : null)
                .createdAt(grade.getCreatedAt())
                .build();
    }
}
