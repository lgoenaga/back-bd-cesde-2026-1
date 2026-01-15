package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository para Subject
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    /**
     * Sobrescribir findAll para cargar Level con JOIN FETCH
     */
    @Query("SELECT s FROM Subject s JOIN FETCH s.level ORDER BY s.name")
    @Override
    List<Subject> findAll();

    /**
     * Sobrescribir findById para cargar Level con JOIN FETCH
     */
    @Query("SELECT s FROM Subject s JOIN FETCH s.level WHERE s.id = :id")
    Optional<Subject> findById(@Param("id") Long id);

    /**
     * Busca una materia por código
     */
    @Query("SELECT s FROM Subject s JOIN FETCH s.level WHERE s.code = :code")
    Optional<Subject> findByCode(@Param("code") String code);

    /**
     * Busca materias por nivel
     */
    @Query("SELECT s FROM Subject s JOIN FETCH s.level WHERE s.level.id = :levelId ORDER BY s.name")
    List<Subject> findByLevelId(@Param("levelId") Long levelId);

    /**
     * Busca materias por nombre (búsqueda parcial)
     */
    @Query("SELECT s FROM Subject s JOIN FETCH s.level WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY s.name")
    List<Subject> findByNameContaining(@Param("name") String name);

    /**
     * Verifica si existe una materia con el código dado
     */
    boolean existsByCode(String code);

    /**
     * Busca materias activas
     */
    @Query("SELECT s FROM Subject s JOIN FETCH s.level WHERE s.isActive = true ORDER BY s.name")
    List<Subject> findAllActive();

    // ==================== PAGINATION METHODS ====================

    @Query(value = "SELECT s FROM Subject s JOIN FETCH s.level",
           countQuery = "SELECT COUNT(s) FROM Subject s")
    Page<Subject> findAllWithLevel(Pageable pageable);

    @Query(value = "SELECT s FROM Subject s JOIN FETCH s.level WHERE s.isActive = true",
           countQuery = "SELECT COUNT(s) FROM Subject s WHERE s.isActive = true")
    Page<Subject> findByIsActiveTrue(Pageable pageable);

    @Query(value = "SELECT s FROM Subject s JOIN FETCH s.level WHERE s.level.id = :levelId ORDER BY s.name",
           countQuery = "SELECT COUNT(s) FROM Subject s WHERE s.level.id = :levelId")
    Page<Subject> findByLevelId(@Param("levelId") Long levelId, Pageable pageable);

    @Query(value = "SELECT s FROM Subject s JOIN FETCH s.level WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))",
           countQuery = "SELECT COUNT(s) FROM Subject s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Subject> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
}
