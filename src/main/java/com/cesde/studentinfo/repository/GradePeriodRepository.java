package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.GradePeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para GradePeriod (Períodos de Calificación)
 */
@Repository
public interface GradePeriodRepository extends JpaRepository<GradePeriod, Long> {

    /**
     * Busca un período de calificación por su número
     * @param periodNumber Número del período (1, 2, 3)
     * @return Optional con el período si existe
     */
    Optional<GradePeriod> findByPeriodNumber(Integer periodNumber);

    /**
     * Busca un período de calificación por su nombre
     * @param name Nombre del período
     * @return Optional con el período si existe
     */
    Optional<GradePeriod> findByName(String name);
}
