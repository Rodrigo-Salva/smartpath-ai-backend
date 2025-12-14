package org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "learning_resources")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "resource_type", nullable = false, length = 50)
    private String resourceType; // COURSE, BOOK, VIDEO, ARTICLE, TUTORIAL, PROJECT

    @Column(name = "provider", length = 100)
    private String provider; // Udemy, Coursera, YouTube, etc.

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "difficulty_level", length = 50)
    private String difficultyLevel;

    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Column(name = "is_free")
    private Boolean isFree = false;

    @Column(length = 10)
    private String price;

    @Column(length = 10)
    private String rating;

    @ElementCollection
    @CollectionTable(name = "resource_tags", joinColumns = @JoinColumn(name = "resource_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

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

