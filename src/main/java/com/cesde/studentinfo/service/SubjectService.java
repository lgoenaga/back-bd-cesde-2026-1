package com.cesde.studentinfo.service;

import com.cesde.studentinfo.exception.BusinessException;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.Level;
import com.cesde.studentinfo.model.Subject;
import com.cesde.studentinfo.repository.LevelRepository;
import com.cesde.studentinfo.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service para gestión de materias
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final LevelRepository levelRepository;

    @Transactional(readOnly = true)
    public List<Subject> getAllSubjects() {
        log.info("Fetching all subjects");
        return subjectRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Subject> getAllActiveSubjects() {
        log.info("Fetching all active subjects");
        return subjectRepository.findAllActive();
    }

    @Transactional(readOnly = true)
    public Optional<Subject> getSubjectById(Long id) {
        log.info("Fetching subject by id: {}", id);
        return subjectRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Subject> getSubjectByCode(String code) {
        log.info("Fetching subject by code: {}", code);
        return subjectRepository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public List<Subject> getSubjectsByLevelId(Long levelId) {
        log.info("Fetching subjects for level: {}", levelId);
        return subjectRepository.findByLevelId(levelId);
    }

    @Transactional(readOnly = true)
    public List<Subject> searchSubjectsByName(String name) {
        log.info("Searching subjects by name: {}", name);
        return subjectRepository.findByNameContaining(name);
    }

    public Subject createSubject(Subject subject) {
        log.info("Creating subject: {}", subject.getName());

        // Validar nivel
        Level level = levelRepository.findById(subject.getLevel().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Level", subject.getLevel().getId()));

        // Validar código único
        if (subjectRepository.existsByCode(subject.getCode())) {
            throw new BusinessException("Subject with code " + subject.getCode() + " already exists");
        }

        // Establecer valores por defecto
        if (subject.getIsActive() == null) {
            subject.setIsActive(true);
        }

        subject.setLevel(level);
        Subject saved = subjectRepository.save(subject);
        log.info("Subject created successfully with id: {}", saved.getId());
        return saved;
    }

    public Subject updateSubject(Long id, Subject subject) {
        log.info("Updating subject: {}", id);

        Subject existing = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));

        if (subject.getName() != null) {
            existing.setName(subject.getName());
        }
        if (subject.getDescription() != null) {
            existing.setDescription(subject.getDescription());
        }
        if (subject.getCredits() != null) {
            existing.setCredits(subject.getCredits());
        }
        if (subject.getHoursPerWeek() != null) {
            existing.setHoursPerWeek(subject.getHoursPerWeek());
        }
        if (subject.getIsActive() != null) {
            existing.setIsActive(subject.getIsActive());
        }

        return subjectRepository.save(existing);
    }

    public void deleteSubject(Long id) {
        log.info("Deleting subject: {}", id);
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));

        subjectRepository.deleteById(id);
        log.info("Subject deleted successfully");
    }

    @Transactional(readOnly = true)
    public long countSubjects() {
        return subjectRepository.count();
    }

    // ==================== PAGINATION METHODS ====================

    @Transactional(readOnly = true)
    public Page<Subject> getAllSubjectsPaginated(Pageable pageable) {
        return subjectRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Subject> getAllActiveSubjectsPaginated(Pageable pageable) {
        return subjectRepository.findByIsActiveTrue(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Subject> getSubjectsByLevelPaginated(Long levelId, Pageable pageable) {
        return subjectRepository.findByLevelId(levelId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Subject> searchSubjectsByNamePaginated(String name, Pageable pageable) {
        return subjectRepository.findByNameContainingIgnoreCase(name, pageable);
    }
}
