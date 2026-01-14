package com.cesde.studentinfo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security
 *
 * ESTADO ACTUAL (Desarrollo):
 * - Acceso permitido sin autenticación (permitAll)
 * - CustomUserDetailsService implementado para eliminar warnings
 * - BCrypt configurado para passwords
 *
 * FUTURO (Producción):
 * - Se activará autenticación por endpoint según roles
 * - CustomUserDetailsService ya está listo para ser usado
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean para encriptación de passwords con BCrypt
     * Usado para crear y validar passwords de usuarios en la BD
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración de la cadena de filtros de seguridad
     *
     * IMPORTANTE: Actualmente permite todo el acceso sin autenticación (permitAll)
     * para facilitar el desarrollo y pruebas desde el frontend.
     *
     * El CustomUserDetailsService está implementado para:
     * 1. Eliminar el warning de "generated security password"
     * 2. Estar listo cuando se active la autenticación
     *
     * Para activar autenticación, cambiar permitAll() por configuración de roles.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitado para API REST
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // TODO: Implementar autenticación por roles
            );
        return http.build();
    }
}

