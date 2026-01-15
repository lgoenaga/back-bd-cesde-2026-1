package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.Professor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository para Professor
 */
@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    /**
     * Busca un profesor por número de identificación
     */
    Optional<Professor> findByIdentificationNumber(String identificationNumber);

    /**
     * Busca un profesor por email
     */
    Optional<Professor> findByEmail(String email);

    /**
     * Obtiene todos los profesores activos
     */
    @Query("FROM Professor p WHERE p.isActive = true ORDER BY p.lastName, p.firstName")
    List<Professor> findAllActive();

    /**
     * Busca profesores por nombre o apellido (búsqueda parcial)
     */
    @Query("FROM Professor p WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "ORDER BY p.lastName, p.firstName")
    List<Professor> findByNameContaining(@Param("name") String name);

    /**
     * Verifica si existe un profesor con el número de identificación dado
     */
    boolean existsByIdentificationNumber(String identificationNumber);

    /**
     * Verifica si existe un profesor con el email dado
     */
    boolean existsByEmail(String email);

    // ==================== PAGINATION METHODS ====================

    /**
     * Obtiene todos los profesores activos con paginación
     */
    Page<Professor> findByIsActiveTrue(Pageable pageable);

    /**
     * Busca profesores por nombre o apellido con paginación
     */
    Page<Professor> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName, Pageable pageable);
}
