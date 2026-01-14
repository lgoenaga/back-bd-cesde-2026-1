package com.cesde.studentinfo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.cesde.studentinfo.model.CourseGroup;

/**
 * DTO para crear/actualizar grupos de curso (Request)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseGroupDTO {

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Level ID is required")
    private Long levelId;

    @NotNull(message = "Academic Period ID is required")
    private Long academicPeriodId;

    @NotBlank(message = "Group code is required")
    @Size(max = 20, message = "Group code must not exceed 20 characters")
    private String groupCode;

    @NotBlank(message = "Group name is required")
    @Size(max = 100, message = "Group name must not exceed 100 characters")
    private String groupName;

    @Min(value = 1, message = "Max students must be at least 1")
    private Integer maxStudents;

    private CourseGroup.ScheduleShift scheduleShift;

    private String description;

    private Boolean isActive;
}
