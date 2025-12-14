package org.salva.springcloud.msvc.cursos.smartpathaibackend.career.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "career_paths")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "target_role", nullable = false, length = 100)
    private String targetRole;

    @Column(name = "difficulty_level", nullable = false, length = 50)
    private String difficultyLevel; // BEGINNER, INTERMEDIATE, ADVANCED

    @Column(name = "estimated_duration_weeks")
    private Integer estimatedDurationWeeks;

    @ElementCollection
    @CollectionTable(name = "career_path_skills", joinColumns = @JoinColumn(name = "career_path_id"))
    @Column(name = "skill")
    private List<String> requiredSkills = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "career_path_prerequisites", joinColumns = @JoinColumn(name = "career_path_id"))
    @Column(name = "prerequisite")
    private List<String> prerequisites = new ArrayList<>();

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

