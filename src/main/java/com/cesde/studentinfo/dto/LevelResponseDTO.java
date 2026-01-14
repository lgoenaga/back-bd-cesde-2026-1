package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de niveles (Response)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LevelResponseDTO {

    private Long id;
    private Long courseId;
    private String courseName;
    private Integer levelNumber;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static LevelResponseDTO fromEntity(Level level) {
        return LevelResponseDTO.builder()
                .id(level.getId())
                .courseId(level.getCourse().getId())
                .courseName(level.getCourse().getName())
                .levelNumber(level.getLevelNumber())
                .name(level.getName())
                .description(level.getDescription())
                .createdAt(level.getCreatedAt())
                .updatedAt(level.getUpdatedAt())
                .build();
    }
}
