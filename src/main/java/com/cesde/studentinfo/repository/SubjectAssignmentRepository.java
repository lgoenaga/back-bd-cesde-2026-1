package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.SubjectAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectAssignmentRepository extends JpaRepository<SubjectAssignment, Long> {

    @Query("SELECT sa FROM SubjectAssignment sa " +
           "LEFT JOIN FETCH sa.subject s " +
           "LEFT JOIN FETCH s.level " +
           "LEFT JOIN FETCH sa.professor " +
           "LEFT JOIN FETCH sa.academicPeriod " +
           "WHERE sa.subject.id = :subjectId")
    List<SubjectAssignment> findBySubjectId(@Param("subjectId") Long subjectId);

    @Query("SELECT sa FROM SubjectAssignment sa " +
           "LEFT JOIN FETCH sa.subject s " +
           "LEFT JOIN FETCH s.level " +
           "LEFT JOIN FETCH sa.professor " +
           "LEFT JOIN FETCH sa.academicPeriod " +
           "WHERE sa.professor.id = :professorId")
    List<SubjectAssignment> findByProfessorId(@Param("professorId") Long professorId);

    @Query("SELECT sa FROM SubjectAssignment sa " +
           "LEFT JOIN FETCH sa.subject s " +
           "LEFT JOIN FETCH s.level " +
           "LEFT JOIN FETCH sa.professor " +
           "LEFT JOIN FETCH sa.academicPeriod " +
           "WHERE sa.academicPeriod.id = :periodId")
    List<SubjectAssignment> findByAcademicPeriodId(@Param("periodId") Long periodId);

    @Query("SELECT sa FROM SubjectAssignment sa " +
           "LEFT JOIN FETCH sa.subject s " +
           "LEFT JOIN FETCH s.level " +
           "LEFT JOIN FETCH sa.professor " +
           "LEFT JOIN FETCH sa.academicPeriod " +
           "WHERE sa.subject.id = :subjectId AND sa.academicPeriod.id = :periodId")
    List<SubjectAssignment> findBySubjectIdAndAcademicPeriodId(@Param("subjectId") Long subjectId,
                                                                 @Param("periodId") Long periodId);

    @Query("SELECT sa FROM SubjectAssignment sa " +
           "LEFT JOIN FETCH sa.subject s " +
           "LEFT JOIN FETCH s.level " +
           "LEFT JOIN FETCH sa.professor " +
           "LEFT JOIN FETCH sa.academicPeriod " +
           "WHERE sa.isActive = true")
    List<SubjectAssignment> findByIsActiveTrue();

    boolean existsBySubjectIdAndProfessorIdAndAcademicPeriodId(Long subjectId, Long professorId, Long academicPeriodId);

    @Query("SELECT sa FROM SubjectAssignment sa " +
           "LEFT JOIN FETCH sa.subject s " +
           "LEFT JOIN FETCH s.level " +
           "LEFT JOIN FETCH sa.professor " +
           "LEFT JOIN FETCH sa.academicPeriod")
    Page<SubjectAssignment> findAllWithDetails(Pageable pageable);

    @Query("SELECT sa FROM SubjectAssignment sa " +
           "LEFT JOIN FETCH sa.subject s " +
           "LEFT JOIN FETCH s.level " +
           "LEFT JOIN FETCH sa.professor " +
           "LEFT JOIN FETCH sa.academicPeriod " +
           "WHERE sa.isActive = true")
    Page<SubjectAssignment> findActiveWithDetails(Pageable pageable);

    @Query("SELECT sa FROM SubjectAssignment sa " +
           "LEFT JOIN FETCH sa.subject s " +
           "LEFT JOIN FETCH s.level " +
           "LEFT JOIN FETCH sa.professor " +
           "LEFT JOIN FETCH sa.academicPeriod " +
           "WHERE sa.professor.id = :professorId")
    Page<SubjectAssignment> findByProfessorIdWithDetails(@Param("professorId") Long professorId, Pageable pageable);

    @Query("SELECT sa FROM SubjectAssignment sa " +
           "LEFT JOIN FETCH sa.subject s " +
           "LEFT JOIN FETCH s.level " +
           "LEFT JOIN FETCH sa.professor " +
           "LEFT JOIN FETCH sa.academicPeriod " +
           "WHERE sa.subject.id = :subjectId")
    Page<SubjectAssignment> findBySubjectIdWithDetails(@Param("subjectId") Long subjectId, Pageable pageable);

    @Query("SELECT sa FROM SubjectAssignment sa " +
           "LEFT JOIN FETCH sa.subject s " +
           "LEFT JOIN FETCH s.level " +
           "LEFT JOIN FETCH sa.professor " +
           "LEFT JOIN FETCH sa.academicPeriod " +
           "WHERE sa.academicPeriod.id = :periodId")
    Page<SubjectAssignment> findByAcademicPeriodIdWithDetails(@Param("periodId") Long periodId, Pageable pageable);
}
