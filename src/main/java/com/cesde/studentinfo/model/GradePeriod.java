package com.cesde.studentinfo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "grade_periods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradePeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Min(1)
    @Max(3)
    @Column(name = "period_number", nullable = false, unique = true)
    private Integer periodNumber;

    @NotNull
    @Builder.Default
    @Column(name = "weight_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal weightPercentage = new BigDecimal("33.33");

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "GradePeriod{id=" + id + ", name='" + name + "', number=" + periodNumber + "}";
    }
}

