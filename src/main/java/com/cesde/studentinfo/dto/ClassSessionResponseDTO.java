package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.ClassSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO para respuestas de sesiones de clase (Response)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassSessionResponseDTO {

    private Long id;
    private Long subjectAssignmentId;
    private String subjectName;
    private String professorName;
    private String levelName;
    private LocalDate sessionDate;
    private LocalTime sessionTime;
    private Integer durationMinutes;
    private String topic;
    private String description;
    private ClassSession.SessionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ClassSessionResponseDTO fromEntity(ClassSession session) {
        if (session == null) {
            return null;
        }

        ClassSessionResponseDTOBuilder builder = ClassSessionResponseDTO.builder()
                .id(session.getId())
                .sessionDate(session.getSessionDate())
                .sessionTime(session.getSessionTime())
                .durationMinutes(session.getDurationMinutes())
                .topic(session.getTopic())
                .description(session.getDescription())
                .status(session.getStatus())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt());

        // Cargar datos de SubjectAssignment si está disponible
        if (session.getSubjectAssignment() != null) {
            builder.subjectAssignmentId(session.getSubjectAssignment().getId());

            if (session.getSubjectAssignment().getSubject() != null) {
                builder.subjectName(session.getSubjectAssignment().getSubject().getName());

                if (session.getSubjectAssignment().getSubject().getLevel() != null) {
                    builder.levelName(session.getSubjectAssignment().getSubject().getLevel().getName());
                }
            }

            if (session.getSubjectAssignment().getProfessor() != null) {
                builder.professorName(
                    session.getSubjectAssignment().getProfessor().getFirstName() + " " +
                    session.getSubjectAssignment().getProfessor().getLastName()
                );
            }
        }

        return builder.build();
    }
}
