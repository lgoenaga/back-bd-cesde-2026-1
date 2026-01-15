package com.cesde.studentinfo.service;

import com.cesde.studentinfo.model.Professor;
import com.cesde.studentinfo.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de lógica de negocio para Professor
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    public Professor createProfessor(Professor professor) {
        log.info("Creating professor with identification: {}", professor.getIdentificationNumber());

        if (professorRepository.existsByIdentificationNumber(professor.getIdentificationNumber())) {
            throw new IllegalArgumentException("Ya existe un profesor con ID: " + professor.getIdentificationNumber());
        }
        if (professorRepository.existsByEmail(professor.getEmail())) {
            throw new IllegalArgumentException("Ya existe un profesor con email: " + professor.getEmail());
        }
        return professorRepository.save(professor);
    }

    public Professor updateProfessor(Professor professor) {
        log.info("Updating professor with id: {}", professor.getId());

        if (!professorRepository.existsById(professor.getId())) {
            throw new IllegalArgumentException("Profesor no encontrado con ID: " + professor.getId());
        }
        return professorRepository.save(professor);
    }

    public void deleteProfessor(Long id) {
        log.info("Deleting professor with id: {}", id);
        professorRepository.deleteById(id);
    }

    public void deactivateProfessor(Long id) {
        log.info("Deactivating professor with id: {}", id);

        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profesor no encontrado con ID: " + id));
        professor.setIsActive(false);
        professorRepository.save(professor);
    }

    @Transactional(readOnly = true)
    public Optional<Professor> getProfessorById(Long id) {
        return professorRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Professor> getProfessorByIdentification(String idNumber) {
        return professorRepository.findByIdentificationNumber(idNumber);
    }

    @Transactional(readOnly = true)
    public List<Professor> getAllProfessors() {
        return professorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Professor> getActiveProfessors() {
        return professorRepository.findAllActive();
    }

    @Transactional(readOnly = true)
    public List<Professor> searchProfessorsByName(String name) {
        return professorRepository.findByNameContaining(name);
    }

    @Transactional(readOnly = true)
    public long countProfessors() {
        return professorRepository.count();
    }

    // ==================== PAGINATION METHODS ====================

    @Transactional(readOnly = true)
    public Page<Professor> getAllProfessorsPaginated(Pageable pageable) {
        return professorRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Professor> getActiveProfessorsPaginated(Pageable pageable) {
        return professorRepository.findByIsActiveTrue(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Professor> searchProfessorsByNamePaginated(String name, Pageable pageable) {
        return professorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                name, name, pageable);
    }
}
