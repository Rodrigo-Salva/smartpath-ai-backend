package org.salva.springcloud.msvc.cursos.smartpathaibackend.career.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerPathDTO {
    private Long id;
    private String title;
    private String description;
    private String targetRole;
    private String difficultyLevel;
    private Integer estimatedDurationWeeks;
    private List<String> requiredSkills;
    private List<String> prerequisites;
    private Boolean isActive;
}