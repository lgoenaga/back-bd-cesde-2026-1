package com.cesde.studentinfo.service;

import com.cesde.studentinfo.model.Role;
import com.cesde.studentinfo.model.User;
import com.cesde.studentinfo.model.UserRole;
import com.cesde.studentinfo.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserService userService;
    private final RoleService roleService;

    public UserRole assignRoleToUser(Long userId, Long roleId, Long assignedByUserId) {
        log.info("Assigning role {} to user {}", roleId, userId);

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));
        Role role = roleService.getRoleById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + roleId));

        // Verificar si ya existe la asignación
        Optional<UserRole> existing = userRoleRepository.findByUserIdAndRoleId(userId, roleId);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("El usuario ya tiene asignado este rol");
        }

        UserRole.UserRoleId id = new UserRole.UserRoleId(userId, roleId);
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        if (assignedByUserId != null) {
            User assignedBy = userService.getUserById(assignedByUserId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario asignador no encontrado con ID: " + assignedByUserId));
            userRole.setAssignedBy(assignedBy);
        }

        return userRoleRepository.save(userRole);
    }

    public void removeRoleFromUser(Long userId, Long roleId) {
        log.info("Removing role {} from user {}", roleId, userId);

        UserRole.UserRoleId id = new UserRole.UserRoleId(userId, roleId);
        UserRole userRole = userRoleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asignación de rol no encontrada"));

        userRoleRepository.delete(userRole);
    }

    @Transactional(readOnly = true)
    public List<UserRole> getAllUserRoles() {
        return userRoleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<UserRole> getUserRolesByUserId(Long userId) {
        return userRoleRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<UserRole> getUserRolesByRoleId(Long roleId) {
        return userRoleRepository.findByRoleId(roleId);
    }

    @Transactional(readOnly = true)
    public List<UserRole> getUserRolesByUsername(String username) {
        return userRoleRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<UserRole> getUserRolesByRoleName(String roleName) {
        return userRoleRepository.findByRoleName(roleName);
    }

    @Transactional(readOnly = true)
    public List<UserRole> getRecentAssignments(int days) {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(days);
        return userRoleRepository.findRecentAssignments(fromDate);
    }

    @Transactional(readOnly = true)
    public List<UserRole> getAssignmentsByAssignedBy(Long assignedByUserId) {
        return userRoleRepository.findByAssignedByUserId(assignedByUserId);
    }

    @Transactional(readOnly = true)
    public Optional<UserRole> getUserRole(Long userId, Long roleId) {
        return userRoleRepository.findByUserIdAndRoleId(userId, roleId);
    }

    @Transactional(readOnly = true)
    public long countUserRoles(Long userId) {
        return userRoleRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public long countRoleUsers(Long roleId) {
        return userRoleRepository.countByRoleId(roleId);
    }
}

