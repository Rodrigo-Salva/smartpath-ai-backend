package org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity;

// backend/src/main/java/com/smartpath/auth/entity/User.java

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "experience_level", nullable = false, length = 50)
    private String experienceLevel; // STUDENT, JUNIOR, MID, CAREER_CHANGE

    @Column(name = "target_role", length = 100)
    private String targetRole;

    // Agregar estos campos a tu User entity si no los tienes

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "location")
    private String location;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "portfolio_url")
    private String portfolioUrl;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "salary_expectation")
    private String salaryExpectation;

    @Column(name = "is_open_to_opportunities")
    private Boolean isOpenToOpportunities = false;

    @Column(name = "preferred_work_mode")
    private String preferredWorkMode; // REMOTE, HYBRID, ONSITE

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

