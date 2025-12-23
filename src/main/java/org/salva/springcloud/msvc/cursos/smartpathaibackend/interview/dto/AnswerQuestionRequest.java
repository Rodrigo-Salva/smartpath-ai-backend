package org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerQuestionRequest {
    private String answer;
    private Integer timeSpentSeconds;
}
