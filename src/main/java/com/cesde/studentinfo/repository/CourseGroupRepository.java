package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.CourseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
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

    Optional<CourseGroup> findByGroupCode(String groupCode);

    @Query("FROM CourseGroup cg WHERE cg.course.id = :courseId ORDER BY cg.groupCode")
    List<CourseGroup> findByCourseId(@Param("courseId") Long courseId);

    @Query("FROM CourseGroup cg WHERE cg.academicPeriod.id = :periodId ORDER BY cg.groupCode")
    List<CourseGroup> findByAcademicPeriodId(@Param("periodId") Long periodId);

    @Query("FROM CourseGroup cg WHERE cg.isActive = true AND cg.currentStudents < cg.maxStudents")
    List<CourseGroup> findActiveGroupsWithAvailableSeats();

    boolean existsByGroupCode(String groupCode);
}

