package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.AcademicPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository para AcademicPeriod
 */
@Repository
public interface AcademicPeriodRepository extends JpaRepository<AcademicPeriod, Long> {

    /**
     * Busca un período académico por nombre
     */
    Optional<AcademicPeriod> findByName(String name);

    /**
     * Busca períodos académicos activos
     */
    @Query("FROM AcademicPeriod a WHERE a.isActive = true ORDER BY a.startDate DESC")
    List<AcademicPeriod> findAllActive();

    /**
     * Busca períodos académicos por año
     */
    @Query("FROM AcademicPeriod a WHERE YEAR(a.startDate) = :year ORDER BY a.startDate")
    List<AcademicPeriod> findByYear(int year);

    /**
     * Busca el período académico actual (fecha actual entre inicio y fin)
     */
    @Query("FROM AcademicPeriod a WHERE :currentDate BETWEEN a.startDate AND a.endDate")
    Optional<AcademicPeriod> findCurrentPeriod(LocalDate currentDate);

    /**
     * Verifica si existe un período con el nombre dado
     */
    boolean existsByName(String name);
}

