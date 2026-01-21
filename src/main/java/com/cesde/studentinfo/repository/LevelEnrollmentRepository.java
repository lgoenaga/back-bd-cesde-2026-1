package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.LevelEnrollment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para LevelEnrollment (Inscripciones a Niveles)
 */
@Repository
public interface LevelEnrollmentRepository extends JpaRepository<LevelEnrollment, Long> {

    @EntityGraph(attributePaths = {"courseEnrollment.student", "courseEnrollment.course", "level", "academicPeriod", "group"})
    @Override
    List<LevelEnrollment> findAll();

    @EntityGraph(attributePaths = {"courseEnrollment.student", "courseEnrollment.course", "level", "academicPeriod", "group"})
    @Override
    Optional<LevelEnrollment> findById(Long id);

    @Query("SELECT le FROM LevelEnrollment le " +
           "LEFT JOIN FETCH le.courseEnrollment ce " +
           "LEFT JOIN FETCH ce.student " +
           "LEFT JOIN FETCH ce.course " +
           "LEFT JOIN FETCH le.level " +
           "LEFT JOIN FETCH le.academicPeriod " +
           "LEFT JOIN FETCH le.group " +
           "WHERE le.courseEnrollment.id = :courseEnrollmentId " +
           "ORDER BY le.enrollmentDate DESC")
    List<LevelEnrollment> findByCourseEnrollmentId(@Param("courseEnrollmentId") Long courseEnrollmentId);

    @Query("SELECT le FROM LevelEnrollment le " +
           "LEFT JOIN FETCH le.courseEnrollment ce " +
           "LEFT JOIN FETCH ce.student s " +
           "LEFT JOIN FETCH ce.course " +
           "LEFT JOIN FETCH le.level " +
           "LEFT JOIN FETCH le.academicPeriod " +
           "LEFT JOIN FETCH le.group " +
           "WHERE le.level.id = :levelId " +
           "ORDER BY s.lastName, s.firstName")
    List<LevelEnrollment> findByLevelId(@Param("levelId") Long levelId);

    @Query("SELECT le FROM LevelEnrollment le " +
           "LEFT JOIN FETCH le.courseEnrollment ce " +
           "LEFT JOIN FETCH ce.student s " +
           "LEFT JOIN FETCH ce.course " +
           "LEFT JOIN FETCH le.level " +
           "LEFT JOIN FETCH le.academicPeriod " +
           "LEFT JOIN FETCH le.group " +
           "WHERE le.academicPeriod.id = :periodId " +
           "ORDER BY le.enrollmentDate DESC")
    List<LevelEnrollment> findByAcademicPeriodId(@Param("periodId") Long periodId);

    @Query("SELECT le FROM LevelEnrollment le " +
           "LEFT JOIN FETCH le.courseEnrollment ce " +
           "LEFT JOIN FETCH ce.student s " +
           "LEFT JOIN FETCH ce.course " +
           "LEFT JOIN FETCH le.level " +
           "LEFT JOIN FETCH le.academicPeriod " +
           "LEFT JOIN FETCH le.group " +
           "WHERE le.group.id = :groupId " +
           "ORDER BY s.lastName, s.firstName")
    List<LevelEnrollment> findByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT le FROM LevelEnrollment le " +
           "LEFT JOIN FETCH le.courseEnrollment ce " +
           "LEFT JOIN FETCH ce.student s " +
           "LEFT JOIN FETCH ce.course " +
           "LEFT JOIN FETCH le.level " +
           "LEFT JOIN FETCH le.academicPeriod " +
           "LEFT JOIN FETCH le.group " +
           "WHERE le.status = :status " +
           "ORDER BY le.enrollmentDate DESC")
    List<LevelEnrollment> findByStatus(@Param("status") LevelEnrollment.LevelStatus status);
}
