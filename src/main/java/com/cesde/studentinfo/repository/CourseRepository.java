package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository para Course
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Busca un curso por código
     */
    Optional<Course> findByCode(String code);

    /**
     * Obtiene todos los cursos activos
     */
    @Query("FROM Course c WHERE c.isActive = true ORDER BY c.name")
    List<Course> findAllActive();

    /**
     * Busca cursos por nombre (búsqueda parcial)
     */
    @Query("FROM Course c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY c.name")
    List<Course> findByNameContaining(@Param("name") String name);

    /**
     * Verifica si existe un curso con el código dado
     */
    boolean existsByCode(String code);
}

