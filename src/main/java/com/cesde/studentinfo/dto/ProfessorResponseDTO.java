package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.Professor;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para respuestas de Professor
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorResponseDTO {
    private Long id;
    private String identificationType;
    private String identificationNumber;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private String mobile;
    private String address;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;

    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * Convierte una entidad Professor a ProfessorResponseDTO
     */
    public static ProfessorResponseDTO fromEntity(Professor professor) {
        return ProfessorResponseDTO.builder()
                .id(professor.getId())
                .identificationType(professor.getIdentificationType().name())
                .identificationNumber(professor.getIdentificationNumber())
                .firstName(professor.getFirstName())
                .lastName(professor.getLastName())
                .fullName(professor.getFullName())
                .email(professor.getEmail())
                .phone(professor.getPhone())
                .mobile(professor.getMobile())
                .address(professor.getAddress())
                .dateOfBirth(professor.getDateOfBirth())
                .hireDate(professor.getHireDate())
                .isActive(professor.getIsActive())
                .createdAt(professor.getCreatedAt())
                .updatedAt(professor.getUpdatedAt())
                .build();
    }
}

