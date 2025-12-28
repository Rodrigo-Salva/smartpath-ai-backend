package org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewHistoryDTO {
    private Long id;
    private Long userId;  // ✅ AGREGAR SI NO EXISTE
    private String targetRole;
    private String interviewType;
    private String difficultyLevel;  // ✅ AGREGAR SI NO EXISTE
    private String status;
    private Double overallScore;
    private Integer totalQuestions;  // ✅ AGREGAR SI NO EXISTE
    private Integer answeredQuestions;  // ✅ AGREGAR SI NO EXISTE
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;  // ✅ AGREGAR SI NO EXISTE
}
