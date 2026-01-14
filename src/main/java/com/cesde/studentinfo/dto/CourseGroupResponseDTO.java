package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.CourseGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de grupos de curso (Response)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseGroupResponseDTO {

    private Long id;
    private Long courseId;
    private String courseName;
    private Long levelId;
    private String levelName;
    private Long academicPeriodId;
    private String academicPeriodName;
    private String groupCode;
    private String groupName;
    private Integer maxStudents;
    private Integer currentStudents;
    private Integer availableSeats;
    private CourseGroup.ScheduleShift scheduleShift;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CourseGroupResponseDTO fromEntity(CourseGroup group) {
        return CourseGroupResponseDTO.builder()
                .id(group.getId())
                .courseId(group.getCourse().getId())
                .courseName(group.getCourse().getName())
                .levelId(group.getLevel().getId())
                .levelName(group.getLevel().getName())
                .academicPeriodId(group.getAcademicPeriod().getId())
                .academicPeriodName(group.getAcademicPeriod().getName())
                .groupCode(group.getGroupCode())
                .groupName(group.getGroupName())
                .maxStudents(group.getMaxStudents())
                .currentStudents(group.getCurrentStudents())
                .availableSeats(group.getMaxStudents() - group.getCurrentStudents())
                .scheduleShift(group.getScheduleShift())
                .description(group.getDescription())
                .isActive(group.getIsActive())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .build();
    }
}
