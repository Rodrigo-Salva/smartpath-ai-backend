package org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVListDTO {
    private Long id;
    private String fileName;
    private Long fileSize;
    private Boolean isAnalyzed;
    private LocalDateTime createdAt;
}

