package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de materias (Response)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResponseDTO {

    private Long id;
    private Long levelId;
    private String levelName;
    private String code;
    private String name;
    private String description;
    private BigDecimal credits;
    private Integer hoursPerWeek;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SubjectResponseDTO fromEntity(Subject subject) {
        return SubjectResponseDTO.builder()
                .id(subject.getId())
                .levelId(subject.getLevel().getId())
                .levelName(subject.getLevel().getName())
                .code(subject.getCode())
                .name(subject.getName())
                .description(subject.getDescription())
                .credits(subject.getCredits())
                .hoursPerWeek(subject.getHoursPerWeek())
                .isActive(subject.getIsActive())
                .createdAt(subject.getCreatedAt())
                .updatedAt(subject.getUpdatedAt())
                .build();
    }
}
