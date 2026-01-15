package com.cesde.studentinfo.service;

import com.cesde.studentinfo.model.Course;
import com.cesde.studentinfo.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de lógica de negocio para Course
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;

    /**
     * Crear un nuevo curso
     */
    public Course createCourse(Course course) {
        log.info("Creating course with code: {}", course.getCode());

        // Validar que no existe un curso con el mismo código
        if (courseRepository.existsByCode(course.getCode())) {
            throw new IllegalArgumentException("Ya existe un curso con el código: " + course.getCode());
        }
        return courseRepository.save(course);
    }

    /**
     * Actualizar un curso existente
     */
    public Course updateCourse(Course course) {
        log.info("Updating course with id: {}", course.getId());

        // Verificar que el curso existe
        if (!courseRepository.existsById(course.getId())) {
            throw new IllegalArgumentException("El curso con ID " + course.getId() + " no existe");
        }
        return courseRepository.save(course);
    }

    /**
     * Eliminar un curso
     */
    public void deleteCourse(Long id) {
        log.info("Deleting course with id: {}", id);
        courseRepository.deleteById(id);
    }

    /**
     * Desactivar un curso (soft delete)
     */
    public void deactivateCourse(Long id) {
        log.info("Deactivating course with id: {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado con ID: " + id));
        course.setIsActive(false);
        courseRepository.save(course);
    }

    /**
     * Obtener un curso por ID
     */
    @Transactional(readOnly = true)
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    /**
     * Obtener un curso por código
     */
    @Transactional(readOnly = true)
    public Optional<Course> getCourseByCode(String code) {
        return courseRepository.findByCode(code);
    }

    /**
     * Obtener todos los cursos
     */
    @Transactional(readOnly = true)
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    /**
     * Obtener todos los cursos activos
     */
    @Transactional(readOnly = true)
    public List<Course> getActiveCourses() {
        return courseRepository.findAllActive();
    }

    /**
     * Buscar cursos por nombre
     */
    @Transactional(readOnly = true)
    public List<Course> searchCoursesByName(String name) {
        return courseRepository.findByNameContaining(name);
    }

    /**
     * Contar total de cursos
     */
    @Transactional(readOnly = true)
    public long countCourses() {
        return courseRepository.count();
    }

    // ==================== PAGINATION METHODS ====================

    @Transactional(readOnly = true)
    public Page<Course> getAllCoursesPaginated(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Course> getActiveCoursesPaginated(Pageable pageable) {
        return courseRepository.findByIsActiveTrue(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Course> searchCoursesByNamePaginated(String name, Pageable pageable) {
        return courseRepository.findByNameContainingIgnoreCase(name, pageable);
    }
}
