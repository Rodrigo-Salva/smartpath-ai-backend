package org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceFilterDTO {
    private String resourceType;
    private String difficultyLevel;
    private String provider;
    private Boolean isFree;
    private String tag;
    private Integer minHours;
    private Integer maxHours;
}
