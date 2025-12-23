package org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private Boolean isActive;
}