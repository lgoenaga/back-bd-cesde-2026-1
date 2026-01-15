package com.cesde.studentinfo.controller;

import com.cesde.studentinfo.dto.ApiResponse;
import com.cesde.studentinfo.dto.PagedResponse;
import com.cesde.studentinfo.dto.StudentDTO;
import com.cesde.studentinfo.dto.StudentResponseDTO;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.Student;
import com.cesde.studentinfo.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller para gestión de Estudiantes
 * Expone endpoints REST para operaciones CRUD de estudiantes
 */
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;

    /**
     * GET /api/students - Obtiene todos los estudiantes
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> getAllStudents() {
        log.info("GET /students - Fetching all students");
        List<Student> students = studentService.getAllStudents();
        List<StudentResponseDTO> response = students.stream()
                .map(StudentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Students retrieved successfully"));
    }

    /**
     * GET /api/students/active - Obtiene estudiantes activos
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> getActiveStudents() {
        log.info("GET /students/active - Fetching active students");
        List<Student> students = studentService.getActiveStudents();
        List<StudentResponseDTO> response = students.stream()
                .map(StudentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Active students retrieved successfully"));
    }

    /**
     * GET /api/students/{id} - Obtiene un estudiante por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentById(@PathVariable Long id) {
        log.info("GET /students/{} - Fetching student by ID", id);
        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
        return ResponseEntity.ok(ApiResponse.success(StudentResponseDTO.fromEntity(student)));
    }

    /**
     * GET /api/students/identification/{idNumber} - Obtiene un estudiante por número de identificación
     */
    @GetMapping("/identification/{idNumber}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentByIdentification(@PathVariable String idNumber) {
        log.info("GET /students/identification/{} - Fetching student by identification", idNumber);
        Student student = studentService.getStudentByIdentification(idNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "identification", idNumber));
        return ResponseEntity.ok(ApiResponse.success(StudentResponseDTO.fromEntity(student)));
    }

    /**
     * GET /api/students/search?name=xxx - Busca estudiantes por nombre
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> searchStudents(@RequestParam String name) {
        log.info("GET /students/search?name={} - Searching students by name", name);
        List<Student> students = studentService.searchStudentsByName(name);
        List<StudentResponseDTO> response = students.stream()
                .map(StudentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Search completed successfully"));
    }

    /**
     * POST /api/students - Crea un nuevo estudiante
     */
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponseDTO>> createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        log.info("POST /students - Creating new student: {}", studentDTO.getIdentificationNumber());

        Student student = Student.builder()
                .identificationType(studentDTO.getIdentificationType())
                .identificationNumber(studentDTO.getIdentificationNumber())
                .firstName(studentDTO.getFirstName())
                .lastName(studentDTO.getLastName())
                .email(studentDTO.getEmail())
                .phone(studentDTO.getPhone())
                .mobile(studentDTO.getMobile())
                .address(studentDTO.getAddress())
                .dateOfBirth(studentDTO.getDateOfBirth())
                .enrollmentDate(studentDTO.getEnrollmentDate() != null ? studentDTO.getEnrollmentDate() : LocalDate.now())
                .isActive(studentDTO.getIsActive() != null ? studentDTO.getIsActive() : true)
                .build();

        Student savedStudent = studentService.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(StudentResponseDTO.fromEntity(savedStudent), "Student created successfully"));
    }

    /**
     * PUT /api/students/{id} - Actualiza un estudiante existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentDTO studentDTO) {
        log.info("PUT /students/{} - Updating student", id);

        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));

        student.setIdentificationType(studentDTO.getIdentificationType());
        student.setIdentificationNumber(studentDTO.getIdentificationNumber());
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setEmail(studentDTO.getEmail());
        student.setPhone(studentDTO.getPhone());
        student.setMobile(studentDTO.getMobile());
        student.setAddress(studentDTO.getAddress());
        student.setDateOfBirth(studentDTO.getDateOfBirth());
        if (studentDTO.getIsActive() != null) {
            student.setIsActive(studentDTO.getIsActive());
        }

        Student updatedStudent = studentService.updateStudent(student);
        return ResponseEntity.ok(ApiResponse.success(StudentResponseDTO.fromEntity(updatedStudent), "Student updated successfully"));
    }

    /**
     * DELETE /api/students/{id} - Elimina un estudiante
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        log.info("DELETE /students/{} - Deleting student", id);
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully"));
    }

    /**
     * PATCH /api/students/{id}/deactivate - Desactiva un estudiante
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> deactivateStudent(@PathVariable Long id) {
        log.info("PATCH /students/{}/deactivate - Deactivating student", id);
        studentService.deactivateStudent(id);
        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
        return ResponseEntity.ok(ApiResponse.success(StudentResponseDTO.fromEntity(student), "Student deactivated successfully"));
    }

    /**
     * GET /api/students/count - Cuenta el total de estudiantes
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countStudents() {
        log.info("GET /students/count - Counting students");
        long count = studentService.countStudents();
        return ResponseEntity.ok(ApiResponse.success(count, "Count retrieved successfully"));
    }

    // ==================== PAGINATED ENDPOINTS ====================

    /**
     * GET /api/students/paged - Obtiene todos los estudiantes con paginación
     * @param page número de página (default: 0)
     * @param size tamaño de página (default: 20)
     * @param sort criterio de ordenamiento (default: id,desc)
     */
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<StudentResponseDTO>>> getAllStudentsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        log.info("GET /students/paged - Fetching students page={}, size={}, sort={}", page, size, sort);

        Pageable pageable = createPageable(page, size, sort);
        Page<Student> studentPage = studentService.getAllStudentsPaginated(pageable);

        PagedResponse<StudentResponseDTO> response = PagedResponse.from(
                studentPage.map(StudentResponseDTO::fromEntity));

        return ResponseEntity.ok(ApiResponse.success(response, "Students retrieved successfully"));
    }

    /**
     * GET /api/students/active/paged - Obtiene estudiantes activos con paginación
     */
    @GetMapping("/active/paged")
    public ResponseEntity<ApiResponse<PagedResponse<StudentResponseDTO>>> getActiveStudentsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        log.info("GET /students/active/paged - Fetching active students page={}, size={}", page, size);

        Pageable pageable = createPageable(page, size, sort);
        Page<Student> studentPage = studentService.getActiveStudentsPaginated(pageable);

        PagedResponse<StudentResponseDTO> response = PagedResponse.from(
                studentPage.map(StudentResponseDTO::fromEntity));

        return ResponseEntity.ok(ApiResponse.success(response, "Active students retrieved successfully"));
    }

    /**
     * GET /api/students/search/paged - Busca estudiantes por nombre con paginación
     */
    @GetMapping("/search/paged")
    public ResponseEntity<ApiResponse<PagedResponse<StudentResponseDTO>>> searchStudentsPaginated(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "lastName,asc") String[] sort) {

        log.info("GET /students/search/paged - Searching students name={}, page={}, size={}", name, page, size);

        Pageable pageable = createPageable(page, size, sort);
        Page<Student> studentPage = studentService.searchStudentsByNamePaginated(name, pageable);

        PagedResponse<StudentResponseDTO> response = PagedResponse.from(
                studentPage.map(StudentResponseDTO::fromEntity));

        return ResponseEntity.ok(ApiResponse.success(response, "Search completed successfully"));
    }

    /**
     * Helper method para crear Pageable desde parámetros
     */
    private Pageable createPageable(int page, int size, String[] sort) {
        String sortBy = sort.length > 0 ? sort[0] : "id";
        String direction = sort.length > 1 ? sort[1] : "desc";

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    }
}

