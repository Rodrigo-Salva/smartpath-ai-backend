package org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddSkillRequest {
    private String skillName;
    private String currentLevel;
    private String targetLevel;
    private String priority;
}
