package com.cesde.studentinfo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración de CORS para Spring Security
 *
 * Esta implementación usa CorsConfigurationSource que se integra
 * directamente con Spring Security, asegurando que CORS funcione
 * correctamente con autenticación JWT.
 *
 * NOTA: allowedOriginPatterns("*") permite cualquier origen con credentials
 * En producción, reemplazar "*" con dominios específicos del frontend
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Permitir cualquier origen usando patrones (compatible con credentials)
        config.setAllowedOriginPatterns(List.of("*"));

        // Permitir credenciales (necesario para JWT en headers)
        config.setAllowCredentials(true);

        // Métodos HTTP permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Headers permitidos
        config.setAllowedHeaders(List.of("*"));

        // Tiempo de caché para preflight requests
        config.setMaxAge(3600L);

        // Aplicar configuración a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}


