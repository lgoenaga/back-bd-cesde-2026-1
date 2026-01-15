package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.Level;
import com.cesde.studentinfo.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository para Level
 */
@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {

    /**
     * Busca niveles por curso
     */
    List<Level> findByCourse(Course course);

    /**
     * Obtiene todos los niveles con Course cargado (JOIN FETCH)
     */
    @Query("SELECT l FROM Level l JOIN FETCH l.course ORDER BY l.course.id, l.levelNumber")
    List<Level> findAllWithCourse();

    /**
     * Busca niveles por curso ID
     */
    @Query("SELECT l FROM Level l JOIN FETCH l.course WHERE l.course.id = :courseId ORDER BY l.levelNumber")
    List<Level> findByCourseId(@Param("courseId") Long courseId);

    /**
     * Busca un nivel específico de un curso
     */
    @Query("SELECT l FROM Level l JOIN FETCH l.course WHERE l.course.id = :courseId AND l.levelNumber = :levelNumber")
    Optional<Level> findByCourseIdAndLevelNumber(@Param("courseId") Long courseId,
                                                   @Param("levelNumber") Integer levelNumber);

    /**
     * Verifica si existe un nivel para un curso
     */
    boolean existsByCourseIdAndLevelNumber(Long courseId, Integer levelNumber);

    // ==================== PAGINATION METHODS ====================

    /**
     * Obtiene todos los niveles paginados con Course cargado (JOIN FETCH)
     */
    @Query(value = "SELECT l FROM Level l JOIN FETCH l.course",
           countQuery = "SELECT COUNT(l) FROM Level l")
    Page<Level> findAllWithCourse(Pageable pageable);

    /**
     * Busca niveles por curso ID paginados con FETCH
     */
    @Query(value = "SELECT l FROM Level l JOIN FETCH l.course WHERE l.course.id = :courseId ORDER BY l.levelNumber",
           countQuery = "SELECT COUNT(l) FROM Level l WHERE l.course.id = :courseId")
    Page<Level> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);
}
