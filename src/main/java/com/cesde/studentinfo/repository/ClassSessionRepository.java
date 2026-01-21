package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para ClassSession (Sesiones de Clase)
 */
@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {

    /**
     * Busca sesiones de clase por asignación de materia
     * @param subjectAssignmentId ID de la asignación de materia
     * @return Lista de sesiones de clase
     */
    List<ClassSession> findBySubjectAssignmentId(Long subjectAssignmentId);

    /**
     * Busca sesiones de clase por fecha
     * @param sessionDate Fecha de la sesión
     * @return Lista de sesiones de clase
     */
    List<ClassSession> findBySessionDate(LocalDate sessionDate);

    /**
     * Busca sesiones de clase en un rango de fechas
     * @param startDate Fecha inicial
     * @param endDate Fecha final
     * @return Lista de sesiones de clase
     */
    @Query("SELECT cs FROM ClassSession cs WHERE cs.sessionDate BETWEEN :startDate AND :endDate")
    List<ClassSession> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Busca sesiones de clase por asignación de materia y fecha
     * @param subjectAssignmentId ID de la asignación de materia
     * @param sessionDate Fecha de la sesión
     * @return Optional con la sesión si existe
     */
    Optional<ClassSession> findBySubjectAssignmentIdAndSessionDate(Long subjectAssignmentId, LocalDate sessionDate);
}
