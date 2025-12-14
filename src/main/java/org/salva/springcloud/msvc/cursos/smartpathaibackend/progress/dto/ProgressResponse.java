package org.salva.springcloud.msvc.cursos.smartpathaibackend.progress.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressResponse {
    private Long id;
    private Long userId;
    private Long resourceId;
    private String resourceTitle;
    private String resourceType;
    private Integer progressPercentage;
    private String status;
    private Integer timeSpentMinutes;
    private LocalDateTime lastAccessedAt;
    private LocalDateTime completedAt;
    private String notes;
    private LocalDateTime createdAt;
}
