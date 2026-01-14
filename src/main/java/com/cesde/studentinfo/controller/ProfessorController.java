package com.cesde.studentinfo.controller;

import com.cesde.studentinfo.dto.ApiResponse;
import com.cesde.studentinfo.dto.ProfessorDTO;
import com.cesde.studentinfo.dto.ProfessorResponseDTO;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.Professor;
import com.cesde.studentinfo.service.ProfessorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller para gestión de Profesores
 * Expone endpoints REST para operaciones CRUD de profesores
 */
@RestController
@RequestMapping("/professors")
@RequiredArgsConstructor
@Slf4j
public class ProfessorController {

    private final ProfessorService professorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProfessorResponseDTO>>> getAllProfessors() {
        log.info("GET /professors - Fetching all professors");
        List<Professor> professors = professorService.getAllProfessors();
        List<ProfessorResponseDTO> response = professors.stream()
                .map(ProfessorResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Professors retrieved successfully"));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ProfessorResponseDTO>>> getActiveProfessors() {
        log.info("GET /professors/active - Fetching active professors");
        List<Professor> professors = professorService.getActiveProfessors();
        List<ProfessorResponseDTO> response = professors.stream()
                .map(ProfessorResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Active professors retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfessorResponseDTO>> getProfessorById(@PathVariable Long id) {
        log.info("GET /professors/{} - Fetching professor by ID", id);
        Professor professor = professorService.getProfessorById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor", id));
        return ResponseEntity.ok(ApiResponse.success(ProfessorResponseDTO.fromEntity(professor)));
    }

    @GetMapping("/identification/{idNumber}")
    public ResponseEntity<ApiResponse<ProfessorResponseDTO>> getProfessorByIdentification(@PathVariable String idNumber) {
        log.info("GET /professors/identification/{} - Fetching professor by identification", idNumber);
        Professor professor = professorService.getProfessorByIdentification(idNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Professor", "identification", idNumber));
        return ResponseEntity.ok(ApiResponse.success(ProfessorResponseDTO.fromEntity(professor)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProfessorResponseDTO>>> searchProfessors(@RequestParam String name) {
        log.info("GET /professors/search?name={} - Searching professors by name", name);
        List<Professor> professors = professorService.searchProfessorsByName(name);
        List<ProfessorResponseDTO> response = professors.stream()
                .map(ProfessorResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Search completed successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProfessorResponseDTO>> createProfessor(@Valid @RequestBody ProfessorDTO professorDTO) {
        log.info("POST /professors - Creating new professor: {}", professorDTO.getIdentificationNumber());

        Professor professor = Professor.builder()
                .identificationType(professorDTO.getIdentificationType())
                .identificationNumber(professorDTO.getIdentificationNumber())
                .firstName(professorDTO.getFirstName())
                .lastName(professorDTO.getLastName())
                .email(professorDTO.getEmail())
                .phone(professorDTO.getPhone())
                .mobile(professorDTO.getMobile())
                .address(professorDTO.getAddress())
                .dateOfBirth(professorDTO.getDateOfBirth())
                .hireDate(professorDTO.getHireDate() != null ? professorDTO.getHireDate() : LocalDate.now())
                .isActive(professorDTO.getIsActive() != null ? professorDTO.getIsActive() : true)
                .build();

        Professor savedProfessor = professorService.createProfessor(professor);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ProfessorResponseDTO.fromEntity(savedProfessor), "Professor created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfessorResponseDTO>> updateProfessor(
            @PathVariable Long id,
            @Valid @RequestBody ProfessorDTO professorDTO) {
        log.info("PUT /professors/{} - Updating professor", id);

        Professor professor = professorService.getProfessorById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor", id));

        professor.setIdentificationType(professorDTO.getIdentificationType());
        professor.setIdentificationNumber(professorDTO.getIdentificationNumber());
        professor.setFirstName(professorDTO.getFirstName());
        professor.setLastName(professorDTO.getLastName());
        professor.setEmail(professorDTO.getEmail());
        professor.setPhone(professorDTO.getPhone());
        professor.setMobile(professorDTO.getMobile());
        professor.setAddress(professorDTO.getAddress());
        professor.setDateOfBirth(professorDTO.getDateOfBirth());
        if (professorDTO.getIsActive() != null) {
            professor.setIsActive(professorDTO.getIsActive());
        }

        Professor updatedProfessor = professorService.updateProfessor(professor);
        return ResponseEntity.ok(ApiResponse.success(ProfessorResponseDTO.fromEntity(updatedProfessor), "Professor updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProfessor(@PathVariable Long id) {
        log.info("DELETE /professors/{} - Deleting professor", id);
        professorService.deleteProfessor(id);
        return ResponseEntity.ok(ApiResponse.success("Professor deleted successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<ProfessorResponseDTO>> deactivateProfessor(@PathVariable Long id) {
        log.info("PATCH /professors/{}/deactivate - Deactivating professor", id);
        professorService.deactivateProfessor(id);
        Professor professor = professorService.getProfessorById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor", id));
        return ResponseEntity.ok(ApiResponse.success(ProfessorResponseDTO.fromEntity(professor), "Professor deactivated successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countProfessors() {
        log.info("GET /professors/count - Counting professors");
        long count = professorService.countProfessors();
        return ResponseEntity.ok(ApiResponse.success(count, "Count retrieved successfully"));
    }
}

