package com.cesde.studentinfo.service;

import com.cesde.studentinfo.model.Role;
import com.cesde.studentinfo.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;

    public Role createRole(Role role) {
        log.info("Creating role: {}", role.getName());

        if (roleRepository.existsByName(role.getName())) {
            throw new IllegalArgumentException("Ya existe un rol con el nombre: " + role.getName());
        }
        return roleRepository.save(role);
    }

    public Role updateRole(Role role) {
        log.info("Updating role with id: {}", role.getId());

        if (!roleRepository.existsById(role.getId())) {
            throw new IllegalArgumentException("Rol no encontrado con ID: " + role.getId());
        }
        return roleRepository.save(role);
    }

    public void deleteRole(Long id) {
        log.info("Deleting role with id: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + id));

        // Usar query específica en lugar de acceder a la colección lazy
        Long userCount = roleRepository.countUsersByRoleId(id);
        if (userCount > 0) {
            throw new IllegalArgumentException("No se puede eliminar el rol porque tiene " + userCount + " usuario(s) asignado(s)");
        }

        roleRepository.deleteById(id);
    }

    public void toggleRoleStatus(Long id) {
        log.info("Toggling role status with id: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + id));
        role.setEnabled(!role.getEnabled());
        roleRepository.save(role);
    }

    @Transactional(readOnly = true)
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Role> getEnabledRoles() {
        return roleRepository.findAllEnabled();
    }

    @Transactional(readOnly = true)
    public List<Role> searchRolesByName(String name) {
        return roleRepository.findByNameContaining(name);
    }

    @Transactional(readOnly = true)
    public long countRoles() {
        return roleRepository.count();
    }

    /**
     * Obtiene el conteo de usuarios asignados a un rol específico.
     * Usa query directa para evitar LazyInitializationException.
     */
    @Transactional(readOnly = true)
    public Long countUsersByRole(Long roleId) {
        return roleRepository.countUsersByRoleId(roleId);
    }
}

