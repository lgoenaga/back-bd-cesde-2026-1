package com.cesde.studentinfo.dto;

import com.cesde.studentinfo.model.Person;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para crear y actualizar profesores
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorDTO {

    @NotNull(message = "Identification type is required")
    private Person.IdentificationType identificationType;

    @NotBlank(message = "Identification number is required")
    @Size(max = 20, message = "Identification number must not exceed 20 characters")
    private String identificationNumber;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    @Size(max = 20, message = "Mobile must not exceed 20 characters")
    private String mobile;

    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;

    private Boolean isActive;
}

