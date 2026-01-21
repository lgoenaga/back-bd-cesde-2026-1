package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.LevelEnrollment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de inscripciones a niveles (Response)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LevelEnrollmentResponseDTO {

    private Long id;
    private Long courseEnrollmentId;
    private String studentName;
    private Long levelId;
    private String levelName;
    private Long academicPeriodId;
    private String academicPeriodName;
    private Long groupId;
    private String groupName;
    private LocalDate enrollmentDate;
    private LevelEnrollment.LevelStatus status;
    private BigDecimal finalAverage;
    private LocalDate completionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static LevelEnrollmentResponseDTO fromEntity(LevelEnrollment enrollment) {
        return LevelEnrollmentResponseDTO.builder()
                .id(enrollment.getId())
                .courseEnrollmentId(enrollment.getCourseEnrollment().getId())
                .studentName(enrollment.getCourseEnrollment().getStudent().getFirstName() + " " +
                           enrollment.getCourseEnrollment().getStudent().getLastName())
                .levelId(enrollment.getLevel().getId())
                .levelName(enrollment.getLevel().getName())
                .academicPeriodId(enrollment.getAcademicPeriod().getId())
                .academicPeriodName(enrollment.getAcademicPeriod().getName())
                .groupId(enrollment.getGroup() != null ? enrollment.getGroup().getId() : null)
                .groupName(enrollment.getGroup() != null ? enrollment.getGroup().getGroupName() : null)
                .enrollmentDate(enrollment.getEnrollmentDate())
                .status(enrollment.getStatus())
                .finalAverage(enrollment.getFinalAverage())
                .completionDate(enrollment.getCompletionDate())
                .createdAt(enrollment.getCreatedAt())
                .updatedAt(enrollment.getUpdatedAt())
                .build();
    }
}
