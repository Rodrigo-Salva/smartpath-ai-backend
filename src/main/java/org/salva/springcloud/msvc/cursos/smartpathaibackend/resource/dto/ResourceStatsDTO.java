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
public class ResourceStatsDTO {
    private Long totalResources;
    private Long freeResources;
    private Long paidResources;
    private Long courseCount;
    private Long bookCount;
    private Long videoCount;
    private Double avgEstimatedHours;
    private List<String> topProviders;
}
