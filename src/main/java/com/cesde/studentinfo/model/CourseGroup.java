package com.cesde.studentinfo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_period_id", nullable = false)
    private AcademicPeriod academicPeriod;

    @NotBlank
    @Size(max = 20)
    @Column(name = "group_code", nullable = false)
    private String groupCode;

    @NotBlank
    @Size(max = 100)
    @Column(name = "group_name", nullable = false)
    private String groupName;

    @Column(name = "max_students")
    private Integer maxStudents;

    @Builder.Default
    @Column(name = "current_students", nullable = false)
    private Integer currentStudents = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_shift", length = 10)
    private ScheduleShift scheduleShift;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum ScheduleShift {
        MANANA,
        TARDE,
        NOCHE,
        MIXTO
    }

    @Override
    public String toString() {
        return "CourseGroup{id=" + id + ", groupCode='" + groupCode + "', groupName='" + groupName + "'}";
    }
}

