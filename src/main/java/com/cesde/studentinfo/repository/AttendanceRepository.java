package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.Attendance;
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

    @Query("FROM Attendance a WHERE a.subjectEnrollment.levelEnrollment.courseEnrollment.student.id = :studentId ORDER BY a.assignmentDate DESC")
    List<Attendance> findByStudentId(@Param("studentId") Long studentId);

    @Query("FROM Attendance a WHERE a.classSession.id = :sessionId")
    List<Attendance> findByClassSessionId(@Param("sessionId") Long sessionId);

    @Query("FROM Attendance a WHERE a.subjectEnrollment.id = :enrollmentId ORDER BY a.assignmentDate")
    List<Attendance> findByEnrollmentId(@Param("enrollmentId") Long enrollmentId);

    @Query("FROM Attendance a WHERE a.subjectEnrollment.id = :enrollmentId AND a.classSession.id = :sessionId")
    Optional<Attendance> findByEnrollmentIdAndSessionId(@Param("enrollmentId") Long enrollmentId, @Param("sessionId") Long sessionId);

    @Query("FROM Attendance a WHERE a.assignmentDate BETWEEN :startDate AND :endDate")
    List<Attendance> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    boolean existsBySubjectEnrollmentIdAndClassSessionId(Long subjectEnrollmentId, Long classSessionId);
}

