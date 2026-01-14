package com.cesde.studentinfo.controller;

import com.cesde.studentinfo.dto.ApiResponse;
import com.cesde.studentinfo.dto.UserRoleDTO;
import com.cesde.studentinfo.dto.UserRoleResponseDTO;
import com.cesde.studentinfo.model.UserRole;
import com.cesde.studentinfo.service.UserRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user-roles")
@RequiredArgsConstructor
@Slf4j
public class UserRoleController {

    private final UserRoleService userRoleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserRoleResponseDTO>>> getAllUserRoles() {
        log.info("GET /user-roles - Fetching all user-role assignments");
        List<UserRole> userRoles = userRoleService.getAllUserRoles();
        List<UserRoleResponseDTO> response = userRoles.stream()
                .map(UserRoleResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "User-role assignments retrieved successfully"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<UserRoleResponseDTO>>> getUserRolesByUserId(@PathVariable Long userId) {
        log.info("GET /user-roles/user/{} - Fetching roles for user", userId);
        List<UserRole> userRoles = userRoleService.getUserRolesByUserId(userId);
        List<UserRoleResponseDTO> response = userRoles.stream()
                .map(UserRoleResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "User roles retrieved successfully"));
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<ApiResponse<List<UserRoleResponseDTO>>> getUserRolesByRoleId(@PathVariable Long roleId) {
        log.info("GET /user-roles/role/{} - Fetching users with role", roleId);
        List<UserRole> userRoles = userRoleService.getUserRolesByRoleId(roleId);
        List<UserRoleResponseDTO> response = userRoles.stream()
                .map(UserRoleResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Role users retrieved successfully"));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<List<UserRoleResponseDTO>>> getUserRolesByUsername(@PathVariable String username) {
        log.info("GET /user-roles/username/{} - Fetching roles for username", username);
        List<UserRole> userRoles = userRoleService.getUserRolesByUsername(username);
        List<UserRoleResponseDTO> response = userRoles.stream()
                .map(UserRoleResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "User roles retrieved successfully"));
    }

    @GetMapping("/role-name/{roleName}")
    public ResponseEntity<ApiResponse<List<UserRoleResponseDTO>>> getUserRolesByRoleName(@PathVariable String roleName) {
        log.info("GET /user-roles/role-name/{} - Fetching users with role name", roleName);
        List<UserRole> userRoles = userRoleService.getUserRolesByRoleName(roleName);
        List<UserRoleResponseDTO> response = userRoles.stream()
                .map(UserRoleResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Users with role retrieved successfully"));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<UserRoleResponseDTO>>> getRecentAssignments(
            @RequestParam(defaultValue = "7") int days) {
        log.info("GET /user-roles/recent?days={} - Fetching recent assignments", days);
        List<UserRole> userRoles = userRoleService.getRecentAssignments(days);
        List<UserRoleResponseDTO> response = userRoles.stream()
                .map(UserRoleResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Recent assignments retrieved successfully"));
    }

    @GetMapping("/assigned-by/{userId}")
    public ResponseEntity<ApiResponse<List<UserRoleResponseDTO>>> getAssignmentsByAssignedBy(@PathVariable Long userId) {
        log.info("GET /user-roles/assigned-by/{} - Fetching assignments made by user", userId);
        List<UserRole> userRoles = userRoleService.getAssignmentsByAssignedBy(userId);
        List<UserRoleResponseDTO> response = userRoles.stream()
                .map(UserRoleResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Assignments by user retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserRoleResponseDTO>> assignRoleToUser(@Valid @RequestBody UserRoleDTO userRoleDTO) {
        log.info("POST /user-roles - Assigning role {} to user {}", userRoleDTO.getRoleId(), userRoleDTO.getUserId());

        UserRole userRole = userRoleService.assignRoleToUser(
                userRoleDTO.getUserId(),
                userRoleDTO.getRoleId(),
                userRoleDTO.getAssignedByUserId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(UserRoleResponseDTO.fromEntity(userRole), "Role assigned successfully"));
    }

    @DeleteMapping("/user/{userId}/role/{roleId}")
    public ResponseEntity<ApiResponse<Void>> removeRoleFromUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        log.info("DELETE /user-roles/user/{}/role/{} - Removing role from user", userId, roleId);
        userRoleService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.ok(ApiResponse.success(null, "Role removed from user successfully"));
    }
}

