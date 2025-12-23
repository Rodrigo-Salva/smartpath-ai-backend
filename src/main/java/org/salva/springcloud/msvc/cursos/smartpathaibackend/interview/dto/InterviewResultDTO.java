package org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResultDTO {
    private Long interviewId;
    private String targetRole;
    private Double overallScore;
    private Integer totalQuestions;
    private Integer answeredQuestions;
    private List<QuestionResultDTO> questionResults;
    private String overallFeedback;
    private List<String> strengths;
    private List<String> improvements;
}
