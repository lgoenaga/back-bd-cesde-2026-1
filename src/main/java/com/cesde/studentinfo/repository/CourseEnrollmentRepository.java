package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para CourseEnrollment (Inscripciones)
 */
@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    @Query("FROM CourseEnrollment ce WHERE ce.student.id = :studentId ORDER BY ce.enrollmentDate DESC")
    List<CourseEnrollment> findByStudentId(@Param("studentId") Long studentId);

    @Query("FROM CourseEnrollment ce WHERE ce.course.id = :courseId ORDER BY ce.student.lastName")
    List<CourseEnrollment> findByCourseId(@Param("courseId") Long courseId);

    @Query("FROM CourseEnrollment ce WHERE ce.student.id = :studentId AND ce.course.id = :courseId AND ce.academicPeriod.id = :periodId")
    Optional<CourseEnrollment> findByStudentCourseAndPeriod(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("periodId") Long periodId);

    @Query("FROM CourseEnrollment ce WHERE ce.enrollmentStatus = :status")
    List<CourseEnrollment> findByStatus(@Param("status") CourseEnrollment.EnrollmentStatus status);

    @Query("FROM CourseEnrollment ce WHERE ce.academicPeriod.id = :periodId")
    List<CourseEnrollment> findByAcademicPeriodId(@Param("periodId") Long periodId);

    boolean existsByStudentIdAndCourseIdAndAcademicPeriodId(Long studentId, Long courseId, Long periodId);
}
