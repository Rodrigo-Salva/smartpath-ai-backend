package org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.entity;

import jakarta.persistence.*;
import lombok.*;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "current_level")
    private String currentLevel; // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT

    @Column(name = "target_level")
    private String targetLevel;

    @Column(name = "priority")
    private String priority; // HIGH, MEDIUM, LOW

    @Column(name = "is_learning")
    private Boolean isLearning = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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

