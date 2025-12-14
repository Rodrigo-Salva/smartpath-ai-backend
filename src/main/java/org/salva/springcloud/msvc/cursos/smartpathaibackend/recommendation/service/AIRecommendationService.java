package org.salva.springcloud.msvc.cursos.smartpathaibackend.recommendation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.ai.service.GeminiService;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.repository.UserRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.recommendation.dto.AIRecommendationDTO;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.recommendation.model.AIRecommendation;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.recommendation.repository.AIRecommendationRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.model.LearningResource;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.repository.LearningResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIRecommendationService {

    private final AIRecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final LearningResourceRepository resourceRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    @Transactional
    public List<AIRecommendationDTO> generateRecommendationsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // ✅ Validar y usar valores por defecto si son null
        String targetRole = user.getTargetRole() != null && !user.getTargetRole().isBlank()
                ? user.getTargetRole()
                : "Desarrollo profesional general";
        String experienceLevel = user.getExperienceLevel() != null
                ? user.getExperienceLevel()
                : "JUNIOR";

        // Obtener recomendaciones de Gemini
        String userProfile = String.format("Nombre: %s, Objetivo: %s",
                user.getFullName(), targetRole);

        String aiResponse = geminiService.generateRecommendation(
                userProfile,
                targetRole,
                experienceLevel
        );

        log.info("Respuesta de Gemini AI para usuario {}: {}", userId, aiResponse);

        // Buscar recursos relacionados en la base de datos
        List<LearningResource> matchingResources = findMatchingResources(user.getTargetRole());

        List<AIRecommendation> recommendations = new ArrayList<>();

        for (LearningResource resource : matchingResources) {
            double score = calculateRecommendationScore(user, resource);

            AIRecommendation recommendation = AIRecommendation.builder()
                    .user(user)
                    .resource(resource)
                    .recommendationScore(score)
                    .recommendationReason(generateReason(user, resource, aiResponse, targetRole))
                    .aiModel("gemini-pro")
                    .isAccepted(null)
                    .build();

            recommendations.add(recommendation);
        }

        List<AIRecommendation> saved = recommendationRepository.saveAll(recommendations);

        return saved.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AIRecommendationDTO> getUserRecommendations(Long userId) {
        return recommendationRepository.findTopRecommendationsByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AIRecommendationDTO acceptRecommendation(Long recommendationId, String feedback) {
        AIRecommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RuntimeException("Recomendación no encontrada"));

        recommendation.setIsAccepted(true);
        recommendation.setUserFeedback(feedback);

        AIRecommendation updated = recommendationRepository.save(recommendation);
        return mapToDTO(updated);
    }

    @Transactional
    public AIRecommendationDTO rejectRecommendation(Long recommendationId, String feedback) {
        AIRecommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RuntimeException("Recomendación no encontrada"));

        recommendation.setIsAccepted(false);
        recommendation.setUserFeedback(feedback);

        AIRecommendation updated = recommendationRepository.save(recommendation);
        return mapToDTO(updated);
    }

    // ✅ CORREGIDO: Manejar targetRole null
    private List<LearningResource> findMatchingResources(String targetRole) {
        List<LearningResource> resources = resourceRepository.findByIsActiveTrue();

        // Si targetRole es null o vacío, retornar recursos generales
        if (targetRole == null || targetRole.isBlank()) {
            log.info("targetRole es null/vacío, retornando recursos generales");
            return resources.stream()
                    .limit(5)
                    .collect(Collectors.toList());
        }

        // Si hay targetRole, filtrar por coincidencias
        String targetRoleLower = targetRole.toLowerCase();
        return resources.stream()
                .filter(r -> {
                    boolean titleMatch = r.getTitle() != null &&
                            r.getTitle().toLowerCase().contains(targetRoleLower);
                    boolean tagMatch = r.getTags() != null &&
                            r.getTags().stream()
                                    .anyMatch(tag -> tag != null && tag.toLowerCase().contains(targetRoleLower));
                    return titleMatch || tagMatch;
                })
                .limit(5)
                .collect(Collectors.toList());
    }

    private double calculateRecommendationScore(User user, LearningResource resource) {
        double score = 0.5;

        // ✅ Validar null en experienceLevel
        if (resource.getDifficultyLevel() != null && user.getExperienceLevel() != null &&
                resource.getDifficultyLevel().equalsIgnoreCase(user.getExperienceLevel())) {
            score += 0.3;
        }

        if (Boolean.TRUE.equals(resource.getIsFree())) {
            score += 0.1;
        }

        if (resource.getRating() != null) {
            try {
                double rating = Double.parseDouble(resource.getRating());
                if (rating >= 4.5) {
                    score += 0.1;
                }
            } catch (NumberFormatException e) {
                log.warn("No se pudo parsear rating: {}", resource.getRating());
            }
        }

        return Math.min(1.0, score);
    }

    // ✅ CORREGIDO: Agregar parámetro targetRole con valor por defecto
    private String generateReason(User user, LearningResource resource, String aiContext, String targetRole) {
        return String.format(
                "Recurso recomendado para %s porque coincide con tu objetivo de %s. " +
                        "Nivel: %s, Tipo: %s.",
                user.getFullName(),
                targetRole,
                resource.getDifficultyLevel() != null ? resource.getDifficultyLevel() : "No especificado",
                resource.getResourceType() != null ? resource.getResourceType() : "Recurso general"
        );
    }

    private AIRecommendationDTO mapToDTO(AIRecommendation recommendation) {
        return AIRecommendationDTO.builder()
                .id(recommendation.getId())
                .userId(recommendation.getUser().getId())
                .resourceId(recommendation.getResource().getId())
                .resourceTitle(recommendation.getResource().getTitle())
                .resourceUrl(recommendation.getResource().getUrl())
                .recommendationScore(recommendation.getRecommendationScore())
                .recommendationReason(recommendation.getRecommendationReason())
                .aiModel(recommendation.getAiModel())
                .isAccepted(recommendation.getIsAccepted())
                .userFeedback(recommendation.getUserFeedback())
                .createdAt(recommendation.getCreatedAt())
                .build();
    }
}
