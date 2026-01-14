package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.Attendance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de asistencia (Response)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponseDTO {

    private Long id;
    private Long subjectEnrollmentId;
    private Long studentId;
    private String studentName;
    private Long classSessionId;
    private LocalDate assignmentDate;
    private Attendance.AttendanceStatus status;
    private Boolean isExcused;
    private String excuseReason;
    private String notes;
    private Long recordedById;
    private String recordedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updateDate;

    public static AttendanceResponseDTO fromEntity(Attendance attendance) {
        return AttendanceResponseDTO.builder()
                .id(attendance.getId())
                .subjectEnrollmentId(attendance.getSubjectEnrollment().getId())
                .studentId(attendance.getSubjectEnrollment().getLevelEnrollment().getCourseEnrollment().getStudent().getId())
                .studentName(attendance.getSubjectEnrollment().getLevelEnrollment().getCourseEnrollment().getStudent().getFirstName() + " " +
                        attendance.getSubjectEnrollment().getLevelEnrollment().getCourseEnrollment().getStudent().getLastName())
                .classSessionId(attendance.getClassSession().getId())
                .assignmentDate(attendance.getAssignmentDate())
                .status(attendance.getStatus())
                .isExcused(attendance.getIsExcused())
                .excuseReason(attendance.getExcuseReason())
                .notes(attendance.getNotes())
                .recordedById(attendance.getRecordedBy() != null ? attendance.getRecordedBy().getId() : null)
                .recordedByName(attendance.getRecordedBy() != null ?
                        attendance.getRecordedBy().getFirstName() + " " + attendance.getRecordedBy().getLastName() : null)
                .createdAt(attendance.getCreatedAt())
                .updateDate(attendance.getUpdateDate())
                .build();
    }
}

