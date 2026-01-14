package com.cesde.studentinfo.service;

import com.cesde.studentinfo.model.Student;
import com.cesde.studentinfo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de lógica de negocio para Student
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public Student createStudent(Student student) {
        log.info("Creating student with identification: {}", student.getIdentificationNumber());

        if (studentRepository.existsByIdentificationNumber(student.getIdentificationNumber())) {
            throw new IllegalArgumentException("Ya existe un estudiante con ID: " + student.getIdentificationNumber());
        }
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new IllegalArgumentException("Ya existe un estudiante con email: " + student.getEmail());
        }
        return studentRepository.save(student);
    }

    public Student updateStudent(Student student) {
        log.info("Updating student with id: {}", student.getId());

        if (!studentRepository.existsById(student.getId())) {
            throw new IllegalArgumentException("Estudiante no encontrado con ID: " + student.getId());
        }
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        log.info("Deleting student with id: {}", id);
        studentRepository.deleteById(id);
    }

    public void deactivateStudent(Long id) {
        log.info("Deactivating student with id: {}", id);

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado con ID: " + id));
        student.setIsActive(false);
        studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Student> getStudentByIdentification(String idNumber) {
        return studentRepository.findByIdentificationNumber(idNumber);
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Student> getActiveStudents() {
        return studentRepository.findAllActive();
    }

    @Transactional(readOnly = true)
    public List<Student> searchStudentsByName(String name) {
        return studentRepository.findByNameContaining(name);
    }

    @Transactional(readOnly = true)
    public long countStudents() {
        return studentRepository.count();
    }
}

