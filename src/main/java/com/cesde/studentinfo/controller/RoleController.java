package com.cesde.studentinfo.controller;

import com.cesde.studentinfo.dto.ApiResponse;
import com.cesde.studentinfo.dto.PagedResponse;
import com.cesde.studentinfo.dto.RoleDTO;
import com.cesde.studentinfo.dto.RoleResponseDTO;
import com.cesde.studentinfo.dto.RoleWithUsersDTO;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.Role;
import com.cesde.studentinfo.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponseDTO>>> getAllRoles() {
        log.info("GET /roles - Fetching all roles");
        List<Role> roles = roleService.getAllRoles();
        List<RoleResponseDTO> response = roles.stream()
                .map(RoleResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Roles retrieved successfully"));
    }

    @GetMapping("/enabled")
    public ResponseEntity<ApiResponse<List<RoleResponseDTO>>> getEnabledRoles() {
        log.info("GET /roles/enabled - Fetching enabled roles");
        List<Role> roles = roleService.getEnabledRoles();
        List<RoleResponseDTO> response = roles.stream()
                .map(RoleResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Enabled roles retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponseDTO>> getRoleById(@PathVariable Long id) {
        log.info("GET /roles/{} - Fetching role by ID", id);
        Role role = roleService.getRoleById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        return ResponseEntity.ok(ApiResponse.success(RoleResponseDTO.fromEntity(role)));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<RoleResponseDTO>> getRoleByName(@PathVariable String name) {
        log.info("GET /roles/name/{} - Fetching role by name", name);
        Role role = roleService.getRoleByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", name));
        return ResponseEntity.ok(ApiResponse.success(RoleResponseDTO.fromEntity(role)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<RoleResponseDTO>>> searchRoles(@RequestParam String name) {
        log.info("GET /roles/search?name={} - Searching roles by name", name);
        List<Role> roles = roleService.searchRolesByName(name);
        List<RoleResponseDTO> response = roles.stream()
                .map(RoleResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Search completed successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countRoles() {
        log.info("GET /roles/count - Counting total roles");
        long count = roleService.countRoles();
        return ResponseEntity.ok(ApiResponse.success(count, "Total roles counted successfully"));
    }

    @GetMapping("/with-user-count")
    public ResponseEntity<ApiResponse<List<RoleWithUsersDTO>>> getAllRolesWithUserCount() {
        log.info("GET /roles/with-user-count - Fetching all roles with user count");
        List<Role> roles = roleService.getAllRoles();
        List<RoleWithUsersDTO> response = roles.stream()
                .map(role -> {
                    Long userCount = roleService.countUsersByRole(role.getId());
                    return RoleWithUsersDTO.fromEntityWithCount(role, userCount);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Roles with user count retrieved successfully"));
    }

    @GetMapping("/{id}/user-count")
    public ResponseEntity<ApiResponse<Long>> countUsersByRole(@PathVariable Long id) {
        log.info("GET /roles/{}/user-count - Counting users for role", id);
        // Verificar que el rol existe
        roleService.getRoleById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        Long count = roleService.countUsersByRole(id);
        return ResponseEntity.ok(ApiResponse.success(count, "Users counted successfully for role"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponseDTO>> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        log.info("POST /roles - Creating new role: {}", roleDTO.getName());

        Role role = Role.builder()
                .name(roleDTO.getName())
                .description(roleDTO.getDescription())
                .enabled(roleDTO.getEnabled() != null ? roleDTO.getEnabled() : true)
                .build();

        Role savedRole = roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(RoleResponseDTO.fromEntity(savedRole), "Role created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponseDTO>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleDTO roleDTO) {
        log.info("PUT /roles/{} - Updating role", id);

        Role role = roleService.getRoleById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));

        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        if (roleDTO.getEnabled() != null) {
            role.setEnabled(roleDTO.getEnabled());
        }

        Role updatedRole = roleService.updateRole(role);
        return ResponseEntity.ok(ApiResponse.success(RoleResponseDTO.fromEntity(updatedRole), "Role updated successfully"));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<RoleResponseDTO>> toggleRoleStatus(@PathVariable Long id) {
        log.info("PATCH /roles/{}/toggle-status - Toggling role status", id);
        roleService.toggleRoleStatus(id);
        Role role = roleService.getRoleById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        return ResponseEntity.ok(ApiResponse.success(RoleResponseDTO.fromEntity(role), "Role status toggled successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        log.info("DELETE /roles/{} - Deleting role", id);
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Role deleted successfully"));
    }

    // ==================== PAGINATED ENDPOINTS ====================

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<RoleResponseDTO>>> getAllRolesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        log.info("GET /roles/paged - Fetching roles page={}, size={}", page, size);
        Pageable pageable = createPageable(page, size, sort);
        Page<Role> rolePage = roleService.getAllRolesPaginated(pageable);
        PagedResponse<RoleResponseDTO> response = PagedResponse.from(
                rolePage.map(RoleResponseDTO::fromEntity));
        return ResponseEntity.ok(ApiResponse.success(response, "Roles retrieved successfully"));
    }

    @GetMapping("/enabled/paged")
    public ResponseEntity<ApiResponse<PagedResponse<RoleResponseDTO>>> getEnabledRolesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        log.info("GET /roles/enabled/paged - Fetching enabled roles page={}, size={}", page, size);
        Pageable pageable = createPageable(page, size, sort);
        Page<Role> rolePage = roleService.getEnabledRolesPaginated(pageable);
        PagedResponse<RoleResponseDTO> response = PagedResponse.from(
                rolePage.map(RoleResponseDTO::fromEntity));
        return ResponseEntity.ok(ApiResponse.success(response, "Enabled roles retrieved successfully"));
    }

    @GetMapping("/search/paged")
    public ResponseEntity<ApiResponse<PagedResponse<RoleResponseDTO>>> searchRolesPaginated(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        log.info("GET /roles/search/paged - Searching roles name={}, page={}, size={}", name, page, size);
        Pageable pageable = createPageable(page, size, sort);
        Page<Role> rolePage = roleService.searchRolesByNamePaginated(name, pageable);
        PagedResponse<RoleResponseDTO> response = PagedResponse.from(
                rolePage.map(RoleResponseDTO::fromEntity));
        return ResponseEntity.ok(ApiResponse.success(response, "Search completed successfully"));
    }

    private Pageable createPageable(int page, int size, String[] sort) {
        String sortBy = sort.length > 0 ? sort[0] : "id";
        String direction = sort.length > 1 ? sort[1] : "asc";
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    }
}
