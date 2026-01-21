package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.GradeComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para GradeComponent (Componentes de Calificación)
 */
@Repository
public interface GradeComponentRepository extends JpaRepository<GradeComponent, Long> {

    /**
     * Busca un componente de calificación por su código
     * @param code Código del componente
     * @return Optional con el componente si existe
     */
    Optional<GradeComponent> findByCode(String code);

    /**
     * Busca un componente de calificación por su nombre
     * @param name Nombre del componente
     * @return Optional con el componente si existe
     */
    Optional<GradeComponent> findByName(String name);
}
