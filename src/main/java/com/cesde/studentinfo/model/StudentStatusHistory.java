package com.cesde.studentinfo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_enrollment_id", nullable = false)
    private CourseEnrollment courseEnrollment;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseEnrollment.EnrollmentStatus status;

    @NotNull
    @Column(name = "status_date", nullable = false)
    private LocalDate statusDate;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Size(max = 50)
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "StudentStatusHistory{id=" + id + ", status=" + status + ", date=" + statusDate + "}";
    }
}

