package org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewPracticeDTO {
    private Long id;
    private Long userId;
    private String targetRole;
    private String interviewType;
    private String difficultyLevel;
    private String status;
    private Double overallScore;
    private String aiFeedback;
    private Integer totalQuestions;
    private Integer answeredQuestions;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}