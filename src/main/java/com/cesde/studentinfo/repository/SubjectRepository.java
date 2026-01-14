package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.Subject;
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
     * Busca una materia por código
     */
    Optional<Subject> findByCode(String code);

    /**
     * Busca materias por nivel
     */
    @Query("FROM Subject s WHERE s.level.id = :levelId ORDER BY s.name")
    List<Subject> findByLevelId(@Param("levelId") Long levelId);

    /**
     * Busca materias por nombre (búsqueda parcial)
     */
    @Query("FROM Subject s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY s.name")
    List<Subject> findByNameContaining(@Param("name") String name);

    /**
     * Verifica si existe una materia con el código dado
     */
    boolean existsByCode(String code);

    /**
     * Busca materias activas
     */
    @Query("FROM Subject s WHERE s.isActive = true ORDER BY s.name")
    List<Subject> findAllActive();
}

