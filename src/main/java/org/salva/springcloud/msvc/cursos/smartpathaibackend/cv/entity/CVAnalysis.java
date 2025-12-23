package org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cv_analyses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CVAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_id", nullable = false)
    private CV cv;

    @Column(name = "overall_score")
    private Double overallScore;

    @ElementCollection
    @CollectionTable(name = "cv_strengths", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "strength")
    private List<String> strengths;

    @ElementCollection
    @CollectionTable(name = "cv_weaknesses", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "weakness")
    private List<String> weaknesses;

    @ElementCollection
    @CollectionTable(name = "cv_improvements", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "improvement")
    private List<String> improvements;

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    @Column(name = "ai_model")
    private String aiModel;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

