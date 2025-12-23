package org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "interview_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_practice_id", nullable = false)
    private InterviewPractice interviewPractice;

    @Column(name = "question_number")
    private Integer questionNumber;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "question_type")
    private String questionType; // TECHNICAL, BEHAVIORAL, SITUATIONAL

    @Column(name = "user_answer", columnDefinition = "TEXT")
    private String userAnswer;

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    @Column(name = "score")
    private Double score; // 0-10

    @Column(name = "is_answered")
    private Boolean isAnswered = false;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

