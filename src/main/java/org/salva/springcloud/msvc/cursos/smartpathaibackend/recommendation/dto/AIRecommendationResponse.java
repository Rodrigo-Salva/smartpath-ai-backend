package org.salva.springcloud.msvc.cursos.smartpathaibackend.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIRecommendationResponse {

    private Long id;
    private Long userId;
    private Long resourceId;
    private String resourceTitle;
    private String resourceUrl;
    private Double recommendationScore;
    private String recommendationReason;
    private String aiModel;
    private Boolean isAccepted;
    private String userFeedback;
    private LocalDateTime createdAt;
}
