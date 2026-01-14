package com.cesde.studentinfo.service;
import com.cesde.studentinfo.model.User;
import com.cesde.studentinfo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * Implementación de UserDetailsService para Spring Security
 * 
 * Este servicio es requerido por Spring Security para cargar usuarios durante la autenticación.
 * Actualmente la configuración permite acceso sin autenticación (permitAll),
 * pero este servicio elimina el warning de "generated security password".
 * 
 * Cuando se implemente autenticación completa, este servicio ya estará listo.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    /**
     * Carga un usuario por su username para Spring Security
     * 
     * @param username El username del usuario
     * @return UserDetails con la información del usuario y sus roles
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // Ya está hasheado con BCrypt en la BD
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(!user.getIsActive()) // Bloqueado si no está activo
                .credentialsExpired(false)
                .disabled(!user.getIsActive()) // Deshabilitado si no está activo
                .build();
    }
    /**
     * Convierte los roles del usuario en GrantedAuthority para Spring Security
     */
    private Set<GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()))
                .collect(Collectors.toSet());
    }
}
