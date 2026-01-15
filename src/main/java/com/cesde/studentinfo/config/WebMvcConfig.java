package com.cesde.studentinfo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de Spring MVC
 *
 * Deshabilitamos el manejo de recursos estáticos para evitar conflictos
 * con las rutas de la API REST
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Deshabilitar el manejo automático de recursos estáticos
     * para evitar que Spring busque archivos estáticos en las rutas de la API
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // No agregar ningún handler de recursos estáticos
        // Todas las rutas deben ser manejadas por controllers
    }
}
