package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.SubjectAssignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for SubjectAssignment response
 * Used in GET requests - includes full details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectAssignmentResponseDTO {

    private Long id;

    // Subject details
    private Long subjectId;
    private String subjectName;
    private String subjectCode;

    // Professor details
    private Long professorId;
    private String professorFirstName;
    private String professorLastName;
    private String professorFullName;
    private String professorEmail;

    // Academic Period details
    private Long academicPeriodId;
    private String academicPeriodName;
    private String academicPeriodStartDate;
    private String academicPeriodEndDate;

    // Group details (optional)
    private Long groupId;
    private String groupName;

    // Assignment details
    private String schedule;
    private String classroom;
    private Integer maxStudents;
    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convert SubjectAssignment entity to DTO
     */
    public static SubjectAssignmentResponseDTO fromEntity(SubjectAssignment assignment) {
        if (assignment == null) {
            return null;
        }

        SubjectAssignmentResponseDTOBuilder builder = SubjectAssignmentResponseDTO.builder()
                .id(assignment.getId())
                .schedule(assignment.getSchedule())
                .classroom(assignment.getClassroom())
                .maxStudents(assignment.getMaxStudents())
                .isActive(assignment.getIsActive())
                .createdAt(assignment.getCreatedAt())
                .updatedAt(assignment.getUpdatedAt());

        // Subject details
        if (assignment.getSubject() != null) {
            builder.subjectId(assignment.getSubject().getId())
                    .subjectName(assignment.getSubject().getName())
                    .subjectCode(assignment.getSubject().getCode());
        }

        // Professor details
        if (assignment.getProfessor() != null) {
            builder.professorId(assignment.getProfessor().getId())
                    .professorFirstName(assignment.getProfessor().getFirstName())
                    .professorLastName(assignment.getProfessor().getLastName())
                    .professorFullName(assignment.getProfessor().getFirstName() + " " +
                                       assignment.getProfessor().getLastName())
                    .professorEmail(assignment.getProfessor().getEmail());
        }

        // Academic Period details
        if (assignment.getAcademicPeriod() != null) {
            builder.academicPeriodId(assignment.getAcademicPeriod().getId())
                    .academicPeriodName(assignment.getAcademicPeriod().getName())
                    .academicPeriodStartDate(assignment.getAcademicPeriod().getStartDate() != null ?
                            assignment.getAcademicPeriod().getStartDate().toString() : null)
                    .academicPeriodEndDate(assignment.getAcademicPeriod().getEndDate() != null ?
                            assignment.getAcademicPeriod().getEndDate().toString() : null);
        }

        // Group details (optional)
        if (assignment.getGroup() != null) {
            builder.groupId(assignment.getGroup().getId())
                    .groupName(assignment.getGroup().getGroupName());
        }

        return builder.build();
    }
}
