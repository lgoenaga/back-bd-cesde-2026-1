package com.cesde.studentinfo.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Course name is required")
    @Size(max = 100)
    @Column(nullable = false, unique = true)
    private String name;
    @NotBlank(message = "Course code is required")
    @Size(max = 20)
    @Column(nullable = false, unique = true)
    private String code;
    @Column(columnDefinition = "TEXT")
    private String description;
    @NotNull
    @Min(1)
    @Max(10)
    @Builder.Default
    @Column(name = "total_levels", nullable = false)
    private Integer totalLevels = 3;
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    @Override
    public String toString() {
        return "Course{id=" + id + ", name='" + name + "', code='" + code + "', totalLevels=" + totalLevels + ", isActive=" + isActive + "}";
    }
}
