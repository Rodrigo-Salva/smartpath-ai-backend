package org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSkillDTO {
    private Long id;
    private Long skillId;
    private String skillName;
    private String currentLevel;
    private String targetLevel;
    private String priority;
    private Boolean isLearning;
}
