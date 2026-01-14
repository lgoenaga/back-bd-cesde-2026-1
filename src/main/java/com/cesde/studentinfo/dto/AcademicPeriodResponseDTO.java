package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.AcademicPeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de períodos académicos (Response)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicPeriodResponseDTO {

    private Long id;
    private String name;
    private Integer year;
    private Integer periodNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private Boolean isCurrent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AcademicPeriodResponseDTO fromEntity(AcademicPeriod period) {
        LocalDate now = LocalDate.now();
        boolean isCurrent = !now.isBefore(period.getStartDate()) && !now.isAfter(period.getEndDate());

        return AcademicPeriodResponseDTO.builder()
                .id(period.getId())
                .name(period.getName())
                .year(period.getYear())
                .periodNumber(period.getPeriodNumber())
                .startDate(period.getStartDate())
                .endDate(period.getEndDate())
                .isActive(period.getIsActive())
                .isCurrent(isCurrent)
                .createdAt(period.getCreatedAt())
                .updatedAt(period.getUpdatedAt())
                .build();
    }
}
