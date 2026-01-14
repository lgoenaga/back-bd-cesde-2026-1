package com.cesde.studentinfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal - Spring Boot REST API Application
 * Sistema de Información Estudiantil - CESDE 2026
 *
 * Esta aplicación expone una API REST para gestionar:
 * - Estudiantes
 * - Profesores
 * - Cursos
 * - Asignaciones y más
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║   SISTEMA DE INFORMACIÓN ESTUDIANTIL - CESDE 2026         ║");
        System.out.println("║   REST API - Spring Boot + JPA + MySQL                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
}
