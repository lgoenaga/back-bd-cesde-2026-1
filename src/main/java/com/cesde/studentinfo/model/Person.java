package com.cesde.studentinfo.model;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * Superclase para entidades que representan personas (estudiantes, profesores, etc.).
 * No tiene tabla propia; sus campos se mapean en las tablas hijas.
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "identification_type", nullable = false, length = 3)
    private IdentificationType identificationType;

    @NotBlank
    @Size(max = 20)
    @Column(name = "identification_number", nullable = false, unique = true)
    private String identificationNumber;

    @NotBlank
    @Size(max = 50)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(nullable = false, unique = true)
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 20)
    private String mobile;

    @Size(max = 200)
    private String address;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public enum IdentificationType {
        CC, CE, TI, PAS
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}

