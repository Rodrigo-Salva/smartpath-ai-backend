package org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.entity;

import jakarta.persistence.*;
import lombok.*;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "skill_gaps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "target_role")
    private String targetRole;

    @Column(name = "importance_level")
    private String importanceLevel; // CRITICAL, HIGH, MEDIUM, LOW

    @Column(name = "estimated_learning_hours")
    private Integer estimatedLearningHours;

    @Column(name = "gap_description", columnDefinition = "TEXT")
    private String gapDescription;

    @Column(name = "is_closed")
    private Boolean isClosed = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
