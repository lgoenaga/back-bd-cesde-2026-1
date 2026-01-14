package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleResponseDTO {

    private Long userId;
    private String username;
    private String userEmail;

    private Long roleId;
    private String roleName;
    private String roleDescription;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime assignedAt;

    private Long assignedByUserId;
    private String assignedByUsername;

    public static UserRoleResponseDTO fromEntity(UserRole userRole) {
        return UserRoleResponseDTO.builder()
                .userId(userRole.getUser() != null ? userRole.getUser().getId() : null)
                .username(userRole.getUser() != null ? userRole.getUser().getUsername() : null)
                .userEmail(userRole.getUser() != null ? userRole.getUser().getEmail() : null)
                .roleId(userRole.getRole() != null ? userRole.getRole().getId() : null)
                .roleName(userRole.getRole() != null ? userRole.getRole().getName() : null)
                .roleDescription(userRole.getRole() != null ? userRole.getRole().getDescription() : null)
                .assignedAt(userRole.getAssignedAt())
                .assignedByUserId(userRole.getAssignedBy() != null ? userRole.getAssignedBy().getId() : null)
                .assignedByUsername(userRole.getAssignedBy() != null ? userRole.getAssignedBy().getUsername() : null)
                .build();
    }
}

