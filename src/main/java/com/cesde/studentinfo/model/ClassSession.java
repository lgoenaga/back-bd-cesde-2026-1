package com.cesde.studentinfo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "class_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_assignment_id", nullable = false)
    private SubjectAssignment subjectAssignment;

    @NotNull
    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @NotNull
    @Column(name = "session_time", nullable = false)
    private LocalTime sessionTime;

    @NotNull
    @Builder.Default
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes = 120;

    @Size(max = 200)
    @Column(length = 200)
    private String topic;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.PROGRAMADA;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum SessionStatus {
        PROGRAMADA,
        REALIZADA,
        CANCELADA,
        REPROGRAMADA
    }

    @Override
    public String toString() {
        return "ClassSession{id=" + id + ", date=" + sessionDate + ", status=" + status + "}";
    }
}

