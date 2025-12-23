package org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewHistoryDTO {
    private Long id;
    private String targetRole;
    private String interviewType;
    private Double overallScore;
    private String status;
    private LocalDateTime createdAt;
}
