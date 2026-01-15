
package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.Attendance;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para Attendance (Asistencia)
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @EntityGraph(attributePaths = {"subjectEnrollment", "classSession", "recordedBy"})
    @Override
    List<Attendance> findAll();

    @EntityGraph(attributePaths = {"subjectEnrollment", "classSession", "recordedBy"})
    @Override
    Optional<Attendance> findById(Long id);

    @Query("SELECT a FROM Attendance a " +
           "LEFT JOIN FETCH a.subjectEnrollment se " +
           "LEFT JOIN FETCH a.classSession " +
           "LEFT JOIN FETCH a.recordedBy " +
           "LEFT JOIN FETCH se.levelEnrollment le " +
           "LEFT JOIN FETCH le.courseEnrollment ce " +
           "LEFT JOIN FETCH ce.student " +
           "WHERE ce.student.id = :studentId " +
           "ORDER BY a.assignmentDate DESC")
    List<Attendance> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT a FROM Attendance a " +
           "LEFT JOIN FETCH a.subjectEnrollment " +
           "LEFT JOIN FETCH a.classSession " +
           "LEFT JOIN FETCH a.recordedBy " +
           "WHERE a.classSession.id = :sessionId")
    List<Attendance> findByClassSessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT a FROM Attendance a " +
           "LEFT JOIN FETCH a.subjectEnrollment " +
           "LEFT JOIN FETCH a.classSession " +
           "LEFT JOIN FETCH a.recordedBy " +
           "WHERE a.subjectEnrollment.id = :enrollmentId " +
           "ORDER BY a.assignmentDate")
    List<Attendance> findByEnrollmentId(@Param("enrollmentId") Long enrollmentId);

    @Query("SELECT a FROM Attendance a " +
           "LEFT JOIN FETCH a.subjectEnrollment " +
           "LEFT JOIN FETCH a.classSession " +
           "LEFT JOIN FETCH a.recordedBy " +
           "WHERE a.subjectEnrollment.id = :enrollmentId AND a.classSession.id = :sessionId")
    Optional<Attendance> findByEnrollmentIdAndSessionId(@Param("enrollmentId") Long enrollmentId, @Param("sessionId") Long sessionId);

    @Query("SELECT a FROM Attendance a " +
           "LEFT JOIN FETCH a.subjectEnrollment " +
           "LEFT JOIN FETCH a.classSession " +
           "LEFT JOIN FETCH a.recordedBy " +
           "WHERE a.assignmentDate BETWEEN :startDate AND :endDate")
    List<Attendance> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    boolean existsBySubjectEnrollmentIdAndClassSessionId(Long subjectEnrollmentId, Long classSessionId);
}

