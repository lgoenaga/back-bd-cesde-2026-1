package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.Grade;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Grade (Calificaciones)
 */
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    // Sobrescribir findAll con EntityGraph
    @EntityGraph(attributePaths = {"subjectEnrollment", "gradePeriod", "gradeComponent", "assignedBy"})
    @Override
    List<Grade> findAll();

    // Sobrescribir findById con EntityGraph
    @EntityGraph(attributePaths = {"subjectEnrollment", "gradePeriod", "gradeComponent", "assignedBy"})
    @Override
    Optional<Grade> findById(Long id);

    // Navegación: Grade -> subjectEnrollment -> levelEnrollment -> courseEnrollment -> student
    @Query("SELECT g FROM Grade g " +
           "LEFT JOIN FETCH g.subjectEnrollment se " +
           "LEFT JOIN FETCH g.gradePeriod gp " +
           "LEFT JOIN FETCH g.gradeComponent gc " +
           "LEFT JOIN FETCH g.assignedBy ab " +
           "LEFT JOIN FETCH se.levelEnrollment le " +
           "LEFT JOIN FETCH le.courseEnrollment ce " +
           "LEFT JOIN FETCH ce.student s " +
           "WHERE s.id = :studentId " +
           "ORDER BY g.assignmentDate DESC")
    List<Grade> findByStudentId(@Param("studentId") Long studentId);

    // Por inscripción de materia
    @Query("SELECT g FROM Grade g " +
           "LEFT JOIN FETCH g.subjectEnrollment " +
           "LEFT JOIN FETCH g.gradePeriod " +
           "LEFT JOIN FETCH g.gradeComponent " +
           "LEFT JOIN FETCH g.assignedBy " +
           "WHERE g.subjectEnrollment.id = :enrollmentId " +
           "ORDER BY g.gradePeriod.id")
    List<Grade> findByEnrollmentId(@Param("enrollmentId") Long enrollmentId);

    // Por grupo: Grade -> subjectEnrollment -> levelEnrollment -> group
    @Query("SELECT g FROM Grade g " +
           "LEFT JOIN FETCH g.subjectEnrollment se " +
           "LEFT JOIN FETCH g.gradePeriod " +
           "LEFT JOIN FETCH g.gradeComponent " +
           "LEFT JOIN FETCH g.assignedBy " +
           "LEFT JOIN FETCH se.levelEnrollment le " +
           "LEFT JOIN FETCH le.group grp " +
           "WHERE grp.id = :groupId")
    List<Grade> findByCourseGroupId(@Param("groupId") Long groupId);

    // Por período de calificación
    @Query("SELECT g FROM Grade g " +
           "LEFT JOIN FETCH g.subjectEnrollment " +
           "LEFT JOIN FETCH g.gradePeriod " +
           "LEFT JOIN FETCH g.gradeComponent " +
           "LEFT JOIN FETCH g.assignedBy " +
           "WHERE g.gradePeriod.id = :periodId")
    List<Grade> findByGradePeriodId(@Param("periodId") Long periodId);
}
