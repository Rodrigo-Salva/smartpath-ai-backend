package org.salva.springcloud.msvc.cursos.smartpathaibackend.progress.dto;

import lombok.Data;

@Data
public class UpdateProgressRequest {
    private Integer progressPercentage;
    private Integer timeSpentMinutes;
    private String status; // NOT_STARTED, IN_PROGRESS, COMPLETED, PAUSED
    private String notes;
}
