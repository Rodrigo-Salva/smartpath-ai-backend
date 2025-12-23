package org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResourceRequest {
    private String title;
    private String description;
    private String url;
    private String resourceType;
    private String difficultyLevel;
    private String provider;
    private Integer estimatedHours;
    private Boolean isFree;
    private String price;
    private String rating;
    private List<String> tags;
    private Boolean isActive;
}
