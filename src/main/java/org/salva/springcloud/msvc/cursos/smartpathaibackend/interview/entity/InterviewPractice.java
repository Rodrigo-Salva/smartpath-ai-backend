package org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.entity;

import jakarta.persistence.*;
import lombok.*;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "interview_practices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewPractice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "target_role", nullable = false)
    private String targetRole;

    @Column(name = "difficulty_level")
    private String difficultyLevel; // JUNIOR, INTERMEDIATE, SENIOR

    @Column(name = "interview_type")
    private String interviewType; // TECHNICAL, BEHAVIORAL, MIXED

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "answered_questions")
    private Integer answeredQuestions = 0;

    @Column(name = "overall_score")
    private Double overallScore;

    @Column(name = "status")
    private String status; // IN_PROGRESS, COMPLETED, ABANDONED

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "interviewPractice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewQuestion> questions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
