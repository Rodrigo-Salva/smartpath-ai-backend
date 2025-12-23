package org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardDTO {
    private UserProfileDTO profile;
    private Integer totalCVs;
    private Integer totalSkillGaps;
    private Integer completedInterviews;
    private Double avgInterviewScore;
    private Integer learningResourcesInProgress;
    private Integer unreadNotifications;
    private String nextRecommendedAction;
}
