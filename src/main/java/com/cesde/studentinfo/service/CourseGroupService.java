package com.cesde.studentinfo.service;

import com.cesde.studentinfo.exception.BusinessException;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.CourseGroup;
import com.cesde.studentinfo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service para gestión de grupos de curso
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseGroupService {

    private final CourseGroupRepository courseGroupRepository;
    private final CourseRepository courseRepository;
    private final LevelRepository levelRepository;
    private final AcademicPeriodRepository academicPeriodRepository;

    @Transactional(readOnly = true)
    public List<CourseGroup> getAllCourseGroups() {
        log.info("Fetching all course groups");
        return courseGroupRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<CourseGroup> getCourseGroupById(Long id) {
        log.info("Fetching course group by id: {}", id);
        return courseGroupRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<CourseGroup> getCourseGroupsByCourseId(Long courseId) {
        log.info("Fetching course groups for course: {}", courseId);
        return courseGroupRepository.findByCourseId(courseId);
    }

    @Transactional(readOnly = true)
    public List<CourseGroup> getCourseGroupsByPeriodId(Long periodId) {
        log.info("Fetching course groups for period: {}", periodId);
        return courseGroupRepository.findByAcademicPeriodId(periodId);
    }

    @Transactional(readOnly = true)
    public List<CourseGroup> getAvailableCourseGroups() {
        log.info("Fetching available course groups");
        return courseGroupRepository.findActiveGroupsWithAvailableSeats();
    }

    public CourseGroup createCourseGroup(CourseGroup courseGroup) {
        log.info("Creating course group: {}", courseGroup.getGroupCode());

        // Validar que no exista el código
        if (courseGroupRepository.existsByGroupCode(courseGroup.getGroupCode())) {
            throw new BusinessException("Course group with code " + courseGroup.getGroupCode() + " already exists");
        }

        // Validar relaciones
        if (courseGroup.getCourse() != null && courseGroup.getCourse().getId() != null) {
            courseRepository.findById(courseGroup.getCourse().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course", courseGroup.getCourse().getId()));
        }

        if (courseGroup.getLevel() != null && courseGroup.getLevel().getId() != null) {
            levelRepository.findById(courseGroup.getLevel().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Level", courseGroup.getLevel().getId()));
        }

        if (courseGroup.getAcademicPeriod() != null && courseGroup.getAcademicPeriod().getId() != null) {
            academicPeriodRepository.findById(courseGroup.getAcademicPeriod().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("AcademicPeriod", courseGroup.getAcademicPeriod().getId()));
        }

        // Establecer valores por defecto
        if (courseGroup.getCurrentStudents() == null) {
            courseGroup.setCurrentStudents(0);
        }
        if (courseGroup.getIsActive() == null) {
            courseGroup.setIsActive(true);
        }

        CourseGroup saved = courseGroupRepository.save(courseGroup);
        log.info("Course group created successfully with id: {}", saved.getId());
        return saved;
    }

    public CourseGroup updateCourseGroup(Long id, CourseGroup courseGroup) {
        log.info("Updating course group: {}", id);

        CourseGroup existing = courseGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseGroup", id));

        // Actualizar campos
        if (courseGroup.getGroupName() != null) {
            existing.setGroupName(courseGroup.getGroupName());
        }
        if (courseGroup.getMaxStudents() != null) {
            existing.setMaxStudents(courseGroup.getMaxStudents());
        }
        if (courseGroup.getScheduleShift() != null) {
            existing.setScheduleShift(courseGroup.getScheduleShift());
        }
        if (courseGroup.getDescription() != null) {
            existing.setDescription(courseGroup.getDescription());
        }
        if (courseGroup.getIsActive() != null) {
            existing.setIsActive(courseGroup.getIsActive());
        }

        return courseGroupRepository.save(existing);
    }

    public void deleteCourseGroup(Long id) {
        log.info("Deleting course group: {}", id);
        CourseGroup courseGroup = courseGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseGroup", id));

        if (courseGroup.getCurrentStudents() > 0) {
            throw new BusinessException("Cannot delete course group with enrolled students");
        }

        courseGroupRepository.deleteById(id);
        log.info("Course group deleted successfully");
    }

    @Transactional(readOnly = true)
    public long countCourseGroups() {
        return courseGroupRepository.count();
    }
}
