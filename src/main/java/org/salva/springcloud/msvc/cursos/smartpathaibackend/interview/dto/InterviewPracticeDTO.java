package org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.dto;
import lombok.*;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewPracticeDTO {
    private Long id;
    private String targetRole;
    private String difficultyLevel;
    private String interviewType;
    private Integer totalQuestions;
    private Integer answeredQuestions;
    private Double overallScore;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
