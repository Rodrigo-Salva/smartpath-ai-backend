package org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResultDTO {
    private Integer questionNumber;
    private String questionText;
    private String userAnswer;
    private Double score;
    private String feedback;
}