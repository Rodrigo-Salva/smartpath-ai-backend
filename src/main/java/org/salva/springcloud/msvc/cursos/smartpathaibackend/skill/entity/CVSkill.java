package org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.entity;

import jakarta.persistence.*;
import lombok.*;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.entity.CV;

import java.time.LocalDateTime;

@Entity
@Table(name = "cv_skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CVSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_id", nullable = false)
    private CV cv;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "proficiency_level")
    private String proficiencyLevel; // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "detected_by_ai")
    private Boolean detectedByAI = true;

    @Column(name = "confidence_score")
    private Double confidenceScore; // 0.0 - 1.0

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

