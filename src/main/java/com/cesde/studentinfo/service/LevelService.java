package com.cesde.studentinfo.service;

import com.cesde.studentinfo.exception.BusinessException;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.Course;
import com.cesde.studentinfo.model.Level;
import com.cesde.studentinfo.repository.CourseRepository;
import com.cesde.studentinfo.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service para gestión de niveles
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LevelService {

    private final LevelRepository levelRepository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<Level> getAllLevels() {
        log.info("Fetching all levels");
        return levelRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Level> getLevelById(Long id) {
        log.info("Fetching level by id: {}", id);
        return levelRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Level> getLevelsByCourseId(Long courseId) {
        log.info("Fetching levels for course: {}", courseId);
        return levelRepository.findByCourseId(courseId);
    }

    public Level createLevel(Level level) {
        log.info("Creating level: {} for course: {}", level.getName(), level.getCourse().getId());

        // Validar curso
        Course course = courseRepository.findById(level.getCourse().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", level.getCourse().getId()));

        // Validar que no exista el nivel
        if (levelRepository.existsByCourseIdAndLevelNumber(course.getId(), level.getLevelNumber())) {
            throw new BusinessException("Level " + level.getLevelNumber() + " already exists for this course");
        }

        level.setCourse(course);
        Level saved = levelRepository.save(level);
        log.info("Level created successfully with id: {}", saved.getId());
        return saved;
    }

    public Level updateLevel(Long id, Level level) {
        log.info("Updating level: {}", id);

        Level existing = levelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Level", id));

        if (level.getName() != null) {
            existing.setName(level.getName());
        }
        if (level.getDescription() != null) {
            existing.setDescription(level.getDescription());
        }

        return levelRepository.save(existing);
    }

    public void deleteLevel(Long id) {
        log.info("Deleting level: {}", id);
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Level", id));

        levelRepository.deleteById(id);
        log.info("Level deleted successfully");
    }

    @Transactional(readOnly = true)
    public long countLevels() {
        return levelRepository.count();
    }
}
