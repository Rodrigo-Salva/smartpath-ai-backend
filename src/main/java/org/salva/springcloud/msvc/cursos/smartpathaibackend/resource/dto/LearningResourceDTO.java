package org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningResourceDTO {
    private Long id;
    private String title;
    private String description;
    private String resourceType;
    private String provider;
    private String url;
    private String difficultyLevel;
    private Integer estimatedHours;
    private Boolean isFree;
    private String price;
    private String rating;
    private List<String> tags;
}