package org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GapAnalysisResponse {
    private String targetRole;
    private Integer totalGaps;
    private Integer criticalGaps;
    private Integer totalLearningHours;
    private List<SkillGapDTO> gaps;
    private String aiRecommendation;
}
