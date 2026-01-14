package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.Level;
import com.cesde.studentinfo.model.Course;
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
     * Busca niveles por curso ID
     */
    @Query("FROM Level l WHERE l.course.id = :courseId ORDER BY l.levelNumber")
    List<Level> findByCourseId(@Param("courseId") Long courseId);

    /**
     * Busca un nivel específico de un curso
     */
    @Query("FROM Level l WHERE l.course.id = :courseId AND l.levelNumber = :levelNumber")
    Optional<Level> findByCourseIdAndLevelNumber(@Param("courseId") Long courseId,
                                                   @Param("levelNumber") Integer levelNumber);

    /**
     * Verifica si existe un nivel para un curso
     */
    boolean existsByCourseIdAndLevelNumber(Long courseId, Integer levelNumber);
}

