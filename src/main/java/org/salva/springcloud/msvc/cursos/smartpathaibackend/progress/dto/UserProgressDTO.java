package org.salva.springcloud.msvc.cursos.smartpathaibackend.progress.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressDTO {
    private Long id;
    private Long userId;
    private Long careerPathId;
    private Long resourceId;
    private Integer progressPercentage;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;
    private String notes;
}