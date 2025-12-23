package org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVUploadResponse {
    private Long id;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private Boolean isAnalyzed;
    private LocalDateTime createdAt;
}
