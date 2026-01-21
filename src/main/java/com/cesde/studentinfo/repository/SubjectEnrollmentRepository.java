package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.SubjectEnrollment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para SubjectEnrollment (Inscripciones a Materias)
 */
@Repository
public interface SubjectEnrollmentRepository extends JpaRepository<SubjectEnrollment, Long> {

    @EntityGraph(attributePaths = {"levelEnrollment.courseEnrollment.student", "levelEnrollment.level", "subjectAssignment.subject", "subjectAssignment.professor"})
    @Override
    List<SubjectEnrollment> findAll();

    @EntityGraph(attributePaths = {"levelEnrollment.courseEnrollment.student", "levelEnrollment.level", "subjectAssignment.subject", "subjectAssignment.professor"})
    @Override
    Optional<SubjectEnrollment> findById(Long id);

    @Query("SELECT se FROM SubjectEnrollment se " +
           "LEFT JOIN FETCH se.levelEnrollment le " +
           "LEFT JOIN FETCH le.courseEnrollment ce " +
           "LEFT JOIN FETCH ce.student " +
           "LEFT JOIN FETCH le.level " +
           "LEFT JOIN FETCH se.subjectAssignment sa " +
           "LEFT JOIN FETCH sa.subject " +
           "LEFT JOIN FETCH sa.professor " +
           "WHERE se.levelEnrollment.id = :levelEnrollmentId " +
           "ORDER BY se.enrollmentDate DESC")
    List<SubjectEnrollment> findByLevelEnrollmentId(@Param("levelEnrollmentId") Long levelEnrollmentId);

    @Query("SELECT se FROM SubjectEnrollment se " +
           "LEFT JOIN FETCH se.levelEnrollment le " +
           "LEFT JOIN FETCH le.courseEnrollment ce " +
           "LEFT JOIN FETCH ce.student s " +
           "LEFT JOIN FETCH le.level " +
           "LEFT JOIN FETCH se.subjectAssignment sa " +
           "LEFT JOIN FETCH sa.subject " +
           "LEFT JOIN FETCH sa.professor " +
           "WHERE se.subjectAssignment.id = :subjectAssignmentId " +
           "ORDER BY s.lastName, s.firstName")
    List<SubjectEnrollment> findBySubjectAssignmentId(@Param("subjectAssignmentId") Long subjectAssignmentId);

    @Query("SELECT se FROM SubjectEnrollment se " +
           "LEFT JOIN FETCH se.levelEnrollment le " +
           "LEFT JOIN FETCH le.courseEnrollment ce " +
           "LEFT JOIN FETCH ce.student " +
           "LEFT JOIN FETCH le.level " +
           "LEFT JOIN FETCH se.subjectAssignment sa " +
           "LEFT JOIN FETCH sa.subject " +
           "LEFT JOIN FETCH sa.professor " +
           "WHERE se.status = :status " +
           "ORDER BY se.enrollmentDate DESC")
    List<SubjectEnrollment> findByStatus(@Param("status") SubjectEnrollment.SubjectStatus status);
}
