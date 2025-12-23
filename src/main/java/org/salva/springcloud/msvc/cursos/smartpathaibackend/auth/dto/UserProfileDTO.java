package org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String location;
    private String bio;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String avatarUrl;
    private String targetRole;
    private String experienceLevel;
    private Integer yearsOfExperience;
    private String salaryExpectation;
    private Boolean isOpenToOpportunities;
    private String preferredWorkMode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
