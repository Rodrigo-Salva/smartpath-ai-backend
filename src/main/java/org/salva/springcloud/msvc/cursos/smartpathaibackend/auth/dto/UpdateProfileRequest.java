package org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String fullName;
    private String phoneNumber;
    private String location;
    private String bio;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String targetRole;
    private String experienceLevel;
    private Integer yearsOfExperience;
    private String salaryExpectation;
    private Boolean isOpenToOpportunities;
    private String preferredWorkMode;
}
