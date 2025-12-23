package org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVAnalysisDTO {
    private Long id;
    private Long cvId;
    private Double overallScore;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> improvements;
    private String aiFeedback;
    private String aiModel;
    private LocalDateTime createdAt;
}

