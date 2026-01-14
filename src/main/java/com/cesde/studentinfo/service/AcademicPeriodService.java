package com.cesde.studentinfo.service;

import com.cesde.studentinfo.exception.BusinessException;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.AcademicPeriod;
import com.cesde.studentinfo.repository.AcademicPeriodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service para gestión de períodos académicos
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AcademicPeriodService {

    private final AcademicPeriodRepository academicPeriodRepository;

    @Transactional(readOnly = true)
    public List<AcademicPeriod> getAllPeriods() {
        log.info("Fetching all academic periods");
        return academicPeriodRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<AcademicPeriod> getActivePeriods() {
        log.info("Fetching active academic periods");
        return academicPeriodRepository.findAllActive();
    }

    @Transactional(readOnly = true)
    public Optional<AcademicPeriod> getPeriodById(Long id) {
        log.info("Fetching academic period by id: {}", id);
        return academicPeriodRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<AcademicPeriod> getCurrentPeriod() {
        log.info("Fetching current academic period");
        return academicPeriodRepository.findCurrentPeriod(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<AcademicPeriod> getPeriodsByYear(int year) {
        log.info("Fetching academic periods for year: {}", year);
        return academicPeriodRepository.findByYear(year);
    }

    public AcademicPeriod createPeriod(AcademicPeriod period) {
        log.info("Creating academic period: {}", period.getName());

        // Validar que no exista el nombre
        if (academicPeriodRepository.existsByName(period.getName())) {
            throw new BusinessException("Academic period with name " + period.getName() + " already exists");
        }

        // Validar fechas
        if (period.getEndDate().isBefore(period.getStartDate())) {
            throw new BusinessException("End date must be after start date");
        }

        // Establecer valores por defecto
        if (period.getIsActive() == null) {
            period.setIsActive(true);
        }

        AcademicPeriod saved = academicPeriodRepository.save(period);
        log.info("Academic period created successfully with id: {}", saved.getId());
        return saved;
    }

    public AcademicPeriod updatePeriod(Long id, AcademicPeriod period) {
        log.info("Updating academic period: {}", id);

        AcademicPeriod existing = academicPeriodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicPeriod", id));

        if (period.getName() != null) {
            existing.setName(period.getName());
        }
        if (period.getStartDate() != null) {
            existing.setStartDate(period.getStartDate());
        }
        if (period.getEndDate() != null) {
            existing.setEndDate(period.getEndDate());
        }
        if (period.getIsActive() != null) {
            existing.setIsActive(period.getIsActive());
        }

        // Validar fechas
        if (existing.getEndDate().isBefore(existing.getStartDate())) {
            throw new BusinessException("End date must be after start date");
        }

        return academicPeriodRepository.save(existing);
    }

    public void deletePeriod(Long id) {
        log.info("Deleting academic period: {}", id);
        AcademicPeriod period = academicPeriodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicPeriod", id));

        academicPeriodRepository.deleteById(id);
        log.info("Academic period deleted successfully");
    }

    @Transactional(readOnly = true)
    public long countPeriods() {
        return academicPeriodRepository.count();
    }
}
