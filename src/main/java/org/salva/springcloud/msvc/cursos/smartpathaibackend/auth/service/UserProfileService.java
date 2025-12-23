package org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.dto.*;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.repository.UserRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.repository.CVRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.repository.InterviewPracticeRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.repository.NotificationRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.progress.entity.UserProgress;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.progress.repository.UserProgressRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.repository.SkillGapRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

    private final UserRepository userRepository;
    private final CVRepository cvRepository;
    private final SkillGapRepository skillGapRepository;
    private final InterviewPracticeRepository interviewRepository;
    private final UserProgressRepository progressRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return mapToProfileDTO(user);
    }

    @Transactional
    public UserProfileDTO updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getLocation() != null) user.setLocation(request.getLocation());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getLinkedinUrl() != null) user.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getGithubUrl() != null) user.setGithubUrl(request.getGithubUrl());
        if (request.getPortfolioUrl() != null) user.setPortfolioUrl(request.getPortfolioUrl());
        if (request.getTargetRole() != null) user.setTargetRole(request.getTargetRole());
        if (request.getExperienceLevel() != null) user.setExperienceLevel(request.getExperienceLevel());
        if (request.getYearsOfExperience() != null) user.setYearsOfExperience(request.getYearsOfExperience());
        if (request.getSalaryExpectation() != null) user.setSalaryExpectation(request.getSalaryExpectation());
        if (request.getIsOpenToOpportunities() != null) user.setIsOpenToOpportunities(request.getIsOpenToOpportunities());
        if (request.getPreferredWorkMode() != null) user.setPreferredWorkMode(request.getPreferredWorkMode());

        User updated = userRepository.save(user);
        log.info("Perfil actualizado para usuario {}", userId);

        return mapToProfileDTO(updated);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar contraseña actual
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }

        // Validar que las nuevas contraseñas coincidan
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Las nuevas contraseñas no coinciden");
        }

        // Validar longitud mínima
        if (request.getNewPassword().length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }

        // Actualizar contraseña
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Contraseña cambiada para usuario {}", userId);
    }

    @Transactional(readOnly = true)
    public UserDashboardDTO getDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Estadísticas
        Integer totalCVs = cvRepository.findByUserIdOrderByCreatedAtDesc(userId).size();
        Integer totalGaps = skillGapRepository.findByUserIdAndIsClosedFalseOrderByImportanceLevelAsc(userId).size();

        var completedInterviews = interviewRepository.findByUserIdAndStatus(userId, "COMPLETED");
        Integer completedCount = completedInterviews.size();

        Double avgScore = completedInterviews.stream()
                .filter(i -> i.getOverallScore() != null)
                .mapToDouble(i -> i.getOverallScore())
                .average()
                .orElse(0.0);

        Integer inProgress = progressRepository.findByUserIdAndStatus(
                userId,
                UserProgress.ProgressStatus.IN_PROGRESS
        ).size();

        Integer unread = notificationRepository.countByUserIdAndIsReadFalse(userId);

        String nextAction = determineNextAction(totalCVs, totalGaps, completedCount);

        return UserDashboardDTO.builder()
                .profile(mapToProfileDTO(user))
                .totalCVs(totalCVs)
                .totalSkillGaps(totalGaps)
                .completedInterviews(completedCount)
                .avgInterviewScore(avgScore)
                .learningResourcesInProgress(inProgress)
                .unreadNotifications(unread)
                .nextRecommendedAction(nextAction)
                .build();
    }

    private String determineNextAction(Integer cvs, Integer gaps, Integer interviews) {
        if (cvs == 0) {
            return "Sube tu primer CV para comenzar tu análisis";
        }
        if (gaps > 0) {
            return String.format("Tienes %d brecha(s) de habilidades por cerrar", gaps);
        }
        if (interviews == 0) {
            return "Practica tu primera entrevista con IA";
        }
        return "¡Excelente progreso! Continúa aprendiendo";
    }

    private UserProfileDTO mapToProfileDTO(User user) {
        return UserProfileDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .location(user.getLocation())
                .bio(user.getBio())
                .linkedinUrl(user.getLinkedinUrl())
                .githubUrl(user.getGithubUrl())
                .portfolioUrl(user.getPortfolioUrl())
                .avatarUrl(user.getAvatarUrl())
                .targetRole(user.getTargetRole())
                .experienceLevel(user.getExperienceLevel())
                .yearsOfExperience(user.getYearsOfExperience())
                .salaryExpectation(user.getSalaryExpectation())
                .isOpenToOpportunities(user.getIsOpenToOpportunities())
                .preferredWorkMode(user.getPreferredWorkMode())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
