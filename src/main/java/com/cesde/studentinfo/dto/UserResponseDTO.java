package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private Boolean isActive;

    private Long studentId;
    private String studentName;

    private Long professorId;
    private String professorName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private Set<RoleResponseDTO> roles;

    public static UserResponseDTO fromEntity(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .isActive(user.getIsActive())
                .studentId(user.getStudent() != null ? user.getStudent().getId() : null)
                .studentName(user.getStudent() != null ? user.getStudent().getFullName() : null)
                .professorId(user.getProfessor() != null ? user.getProfessor().getId() : null)
                .professorName(user.getProfessor() != null ? user.getProfessor().getFullName() : null)
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roles(user.getRoles() != null ?
                    user.getRoles().stream()
                        .map(RoleResponseDTO::fromEntity)
                        .collect(Collectors.toSet()) : null)
                .build();
    }
}

