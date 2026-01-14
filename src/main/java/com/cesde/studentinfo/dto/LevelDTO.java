package com.cesde.studentinfo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear/actualizar niveles (Request)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LevelDTO {

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Level number is required")
    @Min(value = 1, message = "Level number must be at least 1")
    @Max(value = 10, message = "Level number must be at most 10")
    private Integer levelNumber;

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;

    private String description;
}
