package org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartInterviewRequest {
    private String targetRole;
    private String difficultyLevel; // JUNIOR, INTERMEDIATE, SENIOR
    private String interviewType; // TECHNICAL, BEHAVIORAL, MIXED
    private Integer numberOfQuestions; // Default: 10
}
