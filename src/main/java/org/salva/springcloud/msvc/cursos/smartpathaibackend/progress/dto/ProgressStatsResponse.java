package org.salva.springcloud.msvc.cursos.smartpathaibackend.progress.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressStatsResponse {
    private Long totalResources;
    private Long completedResources;
    private Long inProgressResources;
    private Double averageProgress;
    private Integer totalTimeSpentMinutes;
    private Double completionRate;
}
