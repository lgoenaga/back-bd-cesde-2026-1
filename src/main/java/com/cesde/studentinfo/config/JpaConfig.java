package com.cesde.studentinfo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuración de JPA y repositorios
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.cesde.studentinfo.repository")
@EnableTransactionManagement
public class JpaConfig {
    // Configuración básica de JPA
    // Spring Boot autoconfigura la mayoría de las cosas,
    // pero esta clase asegura el escaneo de repositorios
}

