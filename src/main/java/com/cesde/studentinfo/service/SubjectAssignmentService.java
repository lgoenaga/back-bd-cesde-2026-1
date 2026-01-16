package com.cesde.studentinfo.service;

import com.cesde.studentinfo.dto.SubjectAssignmentRequestDTO;
import com.cesde.studentinfo.dto.SubjectAssignmentResponseDTO;
import com.cesde.studentinfo.dto.SubjectAssignmentUpdateDTO;
import com.cesde.studentinfo.model.*;
import com.cesde.studentinfo.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing SubjectAssignment operations
 * Handles business logic and validations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubjectAssignmentService {

    private final SubjectAssignmentRepository subjectAssignmentRepository;
    private final SubjectRepository subjectRepository;
    private final ProfessorRepository professorRepository;
    private final AcademicPeriodRepository academicPeriodRepository;
    private final CourseGroupRepository courseGroupRepository;

    /**
     * Create a new subject assignment
     */
    public SubjectAssignmentResponseDTO createAssignment(SubjectAssignmentRequestDTO dto) {
        log.info("Creating subject assignment for subject {} and professor {}",
                dto.getSubjectId(), dto.getProfessorId());

        // Validate subject exists
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subject not found with ID: " + dto.getSubjectId()));

        // Validate professor exists
        Professor professor = professorRepository.findById(dto.getProfessorId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Professor not found with ID: " + dto.getProfessorId()));

        // Validate academic period exists and is active
        AcademicPeriod period = academicPeriodRepository.findById(dto.getAcademicPeriodId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Academic Period not found with ID: " + dto.getAcademicPeriodId()));

        if (!period.getIsActive()) {
            throw new IllegalArgumentException(
                    "Cannot assign to inactive Academic Period: " + period.getName());
        }

        // Validate group if provided
        CourseGroup group = null;
        if (dto.getGroupId() != null) {
            group = courseGroupRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Course Group not found with ID: " + dto.getGroupId()));
        }

        // Check for duplicate assignment
        boolean exists = subjectAssignmentRepository.existsBySubjectIdAndProfessorIdAndAcademicPeriodId(
                dto.getSubjectId(), dto.getProfessorId(), dto.getAcademicPeriodId());

        if (exists) {
            throw new IllegalArgumentException(
                    "Assignment already exists for this subject, professor and period combination");
        }

        // Create assignment
        SubjectAssignment assignment = SubjectAssignment.builder()
                .subject(subject)
                .professor(professor)
                .academicPeriod(period)
                .group(group)
                .schedule(dto.getSchedule())
                .classroom(dto.getClassroom())
                .maxStudents(dto.getMaxStudents())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();

        SubjectAssignment saved = subjectAssignmentRepository.save(assignment);
        log.info("Subject assignment created with ID: {}", saved.getId());

        return SubjectAssignmentResponseDTO.fromEntity(saved);
    }

    /**
     * Get all assignments (no pagination)
     */
    public List<SubjectAssignmentResponseDTO> getAllAssignments() {
        log.info("Fetching all subject assignments");
        List<SubjectAssignment> assignments = subjectAssignmentRepository.findAll();
        return assignments.stream()
                .map(SubjectAssignmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all assignments with pagination
     */
    public Page<SubjectAssignmentResponseDTO> getAllAssignmentsPaged(Pageable pageable) {
        log.info("Fetching subject assignments page: {}", pageable.getPageNumber());
        Page<SubjectAssignment> page = subjectAssignmentRepository.findAllWithDetails(pageable);
        return page.map(SubjectAssignmentResponseDTO::fromEntity);
    }

    /**
     * Get active assignments only
     */
    public List<SubjectAssignmentResponseDTO> getActiveAssignments() {
        log.info("Fetching active subject assignments");
        List<SubjectAssignment> assignments = subjectAssignmentRepository.findByIsActiveTrue();
        return assignments.stream()
                .map(SubjectAssignmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get active assignments with pagination
     */
    public Page<SubjectAssignmentResponseDTO> getActiveAssignmentsPaged(Pageable pageable) {
        log.info("Fetching active subject assignments page: {}", pageable.getPageNumber());
        Page<SubjectAssignment> page = subjectAssignmentRepository.findActiveWithDetails(pageable);
        return page.map(SubjectAssignmentResponseDTO::fromEntity);
    }

    /**
     * Get assignment by ID
     */
    public SubjectAssignmentResponseDTO getAssignmentById(Long id) {
        log.info("Fetching subject assignment with ID: {}", id);
        SubjectAssignment assignment = subjectAssignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subject Assignment not found with ID: " + id));
        return SubjectAssignmentResponseDTO.fromEntity(assignment);
    }

    /**
     * Get assignments by subject ID
     */
    public List<SubjectAssignmentResponseDTO> getAssignmentsBySubject(Long subjectId) {
        log.info("Fetching assignments for subject ID: {}", subjectId);
        List<SubjectAssignment> assignments = subjectAssignmentRepository.findBySubjectId(subjectId);
        return assignments.stream()
                .map(SubjectAssignmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get assignments by subject ID with pagination
     */
    public Page<SubjectAssignmentResponseDTO> getAssignmentsBySubjectPaged(Long subjectId, Pageable pageable) {
        log.info("Fetching assignments for subject ID {} page: {}", subjectId, pageable.getPageNumber());
        Page<SubjectAssignment> page = subjectAssignmentRepository.findBySubjectIdWithDetails(subjectId, pageable);
        return page.map(SubjectAssignmentResponseDTO::fromEntity);
    }

    /**
     * Get assignments by professor ID
     */
    public List<SubjectAssignmentResponseDTO> getAssignmentsByProfessor(Long professorId) {
        log.info("Fetching assignments for professor ID: {}", professorId);
        List<SubjectAssignment> assignments = subjectAssignmentRepository.findByProfessorId(professorId);
        return assignments.stream()
                .map(SubjectAssignmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get assignments by professor ID with pagination
     */
    public Page<SubjectAssignmentResponseDTO> getAssignmentsByProfessorPaged(Long professorId, Pageable pageable) {
        log.info("Fetching assignments for professor ID {} page: {}", professorId, pageable.getPageNumber());
        Page<SubjectAssignment> page = subjectAssignmentRepository.findByProfessorIdWithDetails(professorId, pageable);
        return page.map(SubjectAssignmentResponseDTO::fromEntity);
    }

    /**
     * Get assignments by academic period ID
     */
    public List<SubjectAssignmentResponseDTO> getAssignmentsByPeriod(Long periodId) {
        log.info("Fetching assignments for period ID: {}", periodId);
        List<SubjectAssignment> assignments = subjectAssignmentRepository.findByAcademicPeriodId(periodId);
        return assignments.stream()
                .map(SubjectAssignmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get assignments by academic period ID with pagination
     */
    public Page<SubjectAssignmentResponseDTO> getAssignmentsByPeriodPaged(Long periodId, Pageable pageable) {
        log.info("Fetching assignments for period ID {} page: {}", periodId, pageable.getPageNumber());
        Page<SubjectAssignment> page = subjectAssignmentRepository.findByAcademicPeriodIdWithDetails(periodId, pageable);
        return page.map(SubjectAssignmentResponseDTO::fromEntity);
    }

    /**
     * Get assignments by subject and period
     */
    public List<SubjectAssignmentResponseDTO> getAssignmentsBySubjectAndPeriod(Long subjectId, Long periodId) {
        log.info("Fetching assignments for subject {} and period {}", subjectId, periodId);
        List<SubjectAssignment> assignments = subjectAssignmentRepository
                .findBySubjectIdAndAcademicPeriodId(subjectId, periodId);
        return assignments.stream()
                .map(SubjectAssignmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing assignment
     */
    public SubjectAssignmentResponseDTO updateAssignment(Long id, SubjectAssignmentUpdateDTO dto) {
        log.info("Updating subject assignment with ID: {}", id);

        SubjectAssignment assignment = subjectAssignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subject Assignment not found with ID: " + id));

        // Update fields if provided
        if (dto.getSchedule() != null) {
            assignment.setSchedule(dto.getSchedule());
        }
        if (dto.getClassroom() != null) {
            assignment.setClassroom(dto.getClassroom());
        }
        if (dto.getMaxStudents() != null) {
            assignment.setMaxStudents(dto.getMaxStudents());
        }
        if (dto.getIsActive() != null) {
            assignment.setIsActive(dto.getIsActive());
        }

        SubjectAssignment updated = subjectAssignmentRepository.save(assignment);
        log.info("Subject assignment updated: {}", id);

        return SubjectAssignmentResponseDTO.fromEntity(updated);
    }

    /**
     * Delete assignment (soft delete - set isActive to false)
     */
    public void deleteAssignment(Long id) {
        log.info("Deleting subject assignment with ID: {}", id);

        SubjectAssignment assignment = subjectAssignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subject Assignment not found with ID: " + id));

        assignment.setIsActive(false);
        subjectAssignmentRepository.save(assignment);
        log.info("Subject assignment soft deleted: {}", id);
    }

    /**
     * Permanently delete assignment
     */
    public void permanentlyDeleteAssignment(Long id) {
        log.info("Permanently deleting subject assignment with ID: {}", id);

        if (!subjectAssignmentRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Subject Assignment not found with ID: " + id);
        }

        subjectAssignmentRepository.deleteById(id);
        log.info("Subject assignment permanently deleted: {}", id);
    }
}
