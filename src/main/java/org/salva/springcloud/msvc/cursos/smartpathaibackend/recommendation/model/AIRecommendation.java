package org.salva.springcloud.msvc.cursos.smartpathaibackend.recommendation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.model.LearningResource;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_recommendations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private LearningResource resource;

    @Column(name = "recommendation_score", nullable = false)
    private Double recommendationScore; // 0.0 - 1.0

    @Column(name = "recommendation_reason", columnDefinition = "TEXT")
    private String recommendationReason;

    @Column(name = "ai_model", length = 50)
    private String aiModel; // GPT-4, GPT-3.5, etc.

    @Column(name = "is_accepted")
    private Boolean isAccepted;

    @Column(name = "user_feedback", columnDefinition = "TEXT")
    private String userFeedback;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
