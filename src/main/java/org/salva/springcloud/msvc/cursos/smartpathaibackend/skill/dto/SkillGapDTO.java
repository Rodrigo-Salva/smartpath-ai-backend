package org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillGapDTO {
    private Long id;
    private Long skillId;
    private String skillName;
    private String skillCategory;
    private String importanceLevel;
    private Integer estimatedLearningHours;
    private String gapDescription;
    private Boolean isClosed;
    private List<String> recommendedResources;
}
