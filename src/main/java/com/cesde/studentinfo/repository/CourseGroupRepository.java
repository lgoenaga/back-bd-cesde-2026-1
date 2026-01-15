package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.CourseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para CourseGroup (Grupos de Curso)
 */
@Repository
public interface CourseGroupRepository extends JpaRepository<CourseGroup, Long> {

    @EntityGraph(attributePaths = {"course", "level", "academicPeriod"})
    Optional<CourseGroup> findByGroupCode(String groupCode);

    @EntityGraph(attributePaths = {"course", "level", "academicPeriod"})
    @Query("FROM CourseGroup cg WHERE cg.course.id = :courseId ORDER BY cg.groupCode")
    List<CourseGroup> findByCourseId(@Param("courseId") Long courseId);

    @EntityGraph(attributePaths = {"course", "level", "academicPeriod"})
    @Query("FROM CourseGroup cg WHERE cg.academicPeriod.id = :periodId ORDER BY cg.groupCode")
    List<CourseGroup> findByAcademicPeriodId(@Param("periodId") Long periodId);

    @EntityGraph(attributePaths = {"course", "level", "academicPeriod"})
    @Query("FROM CourseGroup cg WHERE cg.isActive = true AND cg.currentStudents < cg.maxStudents")
    List<CourseGroup> findActiveGroupsWithAvailableSeats();

    boolean existsByGroupCode(String groupCode);

    @EntityGraph(attributePaths = {"course", "level", "academicPeriod"})
    @Override
    List<CourseGroup> findAll();
}
