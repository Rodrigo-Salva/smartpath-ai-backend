package org.salva.springcloud.msvc.cursos.smartpathaibackend.progress.dto;

import lombok.Data;

@Data
public class CreateProgressRequest {
    private Long resourceId;
    private String notes;
}

