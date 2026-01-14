package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.CourseEnrollment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de inscripciones (Response)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseEnrollmentResponseDTO {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private Long academicPeriodId;
    private String academicPeriodName;
    private LocalDate enrollmentDate;
    private CourseEnrollment.EnrollmentStatus enrollmentStatus;
    private LocalDate completionDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CourseEnrollmentResponseDTO fromEntity(CourseEnrollment enrollment) {
        return CourseEnrollmentResponseDTO.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent().getId())
                .studentName(enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName())
                .courseId(enrollment.getCourse().getId())
                .courseName(enrollment.getCourse().getName())
                .academicPeriodId(enrollment.getAcademicPeriod().getId())
                .academicPeriodName(enrollment.getAcademicPeriod().getName())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .enrollmentStatus(enrollment.getEnrollmentStatus())
                .completionDate(enrollment.getCompletionDate())
                .notes(enrollment.getNotes())
                .createdAt(enrollment.getCreatedAt())
                .updatedAt(enrollment.getUpdatedAt())
                .build();
    }
}
