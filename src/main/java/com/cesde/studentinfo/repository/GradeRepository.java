package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para Grade (Calificaciones)
 */
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    // Navegación: Grade -> subjectEnrollment -> levelEnrollment -> courseEnrollment -> student
    @Query("FROM Grade g WHERE g.subjectEnrollment.levelEnrollment.courseEnrollment.student.id = :studentId ORDER BY g.assignmentDate DESC")
    List<Grade> findByStudentId(@Param("studentId") Long studentId);

    // Por inscripción de materia
    @Query("FROM Grade g WHERE g.subjectEnrollment.id = :enrollmentId ORDER BY g.gradePeriod.id")
    List<Grade> findByEnrollmentId(@Param("enrollmentId") Long enrollmentId);

    // Por grupo: Grade -> subjectEnrollment -> levelEnrollment -> group
    @Query("FROM Grade g WHERE g.subjectEnrollment.levelEnrollment.group.id = :groupId")
    List<Grade> findByCourseGroupId(@Param("groupId") Long groupId);

    // Por período de calificación
    @Query("FROM Grade g WHERE g.gradePeriod.id = :periodId")
    List<Grade> findByGradePeriodId(@Param("periodId") Long periodId);
}
