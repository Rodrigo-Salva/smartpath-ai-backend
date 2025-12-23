package org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.dto;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVSkillDTO {
    private Long id;
    private Long cvId;
    private Long skillId;
    private String skillName;
    private String proficiencyLevel;
    private Integer yearsOfExperience;
    private Double confidenceScore;
    private Boolean detectedByAI;
}
