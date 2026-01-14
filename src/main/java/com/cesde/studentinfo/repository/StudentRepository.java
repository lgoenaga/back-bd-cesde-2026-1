package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository para Student
 * Reemplaza StudentDAO con métodos de Spring Data
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Busca un estudiante por número de identificación
     */
    Optional<Student> findByIdentificationNumber(String identificationNumber);

    /**
     * Busca un estudiante por email
     */
    Optional<Student> findByEmail(String email);

    /**
     * Obtiene todos los estudiantes activos
     */
    @Query("FROM Student s WHERE s.isActive = true ORDER BY s.lastName, s.firstName")
    List<Student> findAllActive();

    /**
     * Busca estudiantes por nombre o apellido (búsqueda parcial)
     */
    @Query("FROM Student s WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "ORDER BY s.lastName, s.firstName")
    List<Student> findByNameContaining(@Param("name") String name);

    /**
     * Verifica si existe un estudiante con el número de identificación dado
     */
    boolean existsByIdentificationNumber(String identificationNumber);

    /**
     * Verifica si existe un estudiante con el email dado
     */
    boolean existsByEmail(String email);
}

