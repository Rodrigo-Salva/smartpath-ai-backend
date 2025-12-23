package org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewQuestionDTO {
    private Long id;
    private Long interviewPracticeId;
    private Integer questionNumber;
    private String questionText;
    private String questionType;
    private String userAnswer;
    private String aiFeedback;
    private Double score;
    private Boolean isAnswered;
    private Integer timeSpentSeconds;
}
