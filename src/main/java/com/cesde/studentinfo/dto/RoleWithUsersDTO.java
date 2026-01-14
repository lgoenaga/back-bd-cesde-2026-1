package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO extendido de Role que incluye el conteo de usuarios.
 * Solo debe usarse cuando se carga explícitamente la colección de usuarios
 * o cuando se obtiene el conteo mediante una query específica.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleWithUsersDTO {

    private Long id;
    private String name;
    private String description;
    private Boolean enabled;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private Long totalUsers;

    /**
     * Convierte una entidad Role a DTO incluyendo el conteo de usuarios.
     * ADVERTENCIA: Solo usar cuando la colección users está inicializada (EAGER o fetch join),
     * de lo contrario causará LazyInitializationException.
     */
    public static RoleWithUsersDTO fromEntityWithUsers(Role role) {
        return RoleWithUsersDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .enabled(role.getEnabled())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .totalUsers(role.getUsers() != null ? (long) role.getUsers().size() : 0L)
                .build();
    }

    /**
     * Convierte Role a DTO usando un conteo explícito (obtenido de query).
     * Método preferido para evitar problemas de lazy loading.
     */
    public static RoleWithUsersDTO fromEntityWithCount(Role role, Long userCount) {
        return RoleWithUsersDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .enabled(role.getEnabled())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .totalUsers(userCount)
                .build();
    }
}

