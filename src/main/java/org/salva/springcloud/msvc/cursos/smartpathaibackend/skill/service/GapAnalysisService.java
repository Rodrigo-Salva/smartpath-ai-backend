package org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.ai.service.GeminiService;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.repository.UserRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.repository.LearningResourceRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.dto.GapAnalysisResponse;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.dto.SkillGapDTO;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.entity.CVSkill;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.entity.Skill;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.entity.SkillGap;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.repository.CVSkillRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.repository.SkillGapRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GapAnalysisService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final CVSkillRepository cvSkillRepository;
    private final SkillGapRepository skillGapRepository;
    private final LearningResourceRepository resourceRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    @Transactional
    public GapAnalysisResponse analyzeSkillGaps(Long userId, Long cvId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String targetRole = user.getTargetRole() != null ? user.getTargetRole() : "Desarrollador general";

        // Obtener skills actuales del CV
        List<CVSkill> currentSkills = cvSkillRepository.findByCvId(cvId);
        Set<String> currentSkillNames = currentSkills.stream()
                .map(cs -> cs.getSkill().getName())
                .collect(Collectors.toSet());

        // Llamar a IA para obtener skills requeridas para el rol
        String aiResponse = geminiService.analyzeSkillGapsForRole(
                targetRole,
                new ArrayList<>(currentSkillNames)
        );

        log.info("Gap analysis de IA: {}", aiResponse);

        // Parsear gaps
        List<SkillGap> gaps = parseGapsResponse(user, targetRole, aiResponse, currentSkillNames);

        // Guardar gaps
        List<SkillGap> savedGaps = skillGapRepository.saveAll(gaps);

        // Construir respuesta
        return buildGapAnalysisResponse(targetRole, savedGaps);
    }

    @Transactional(readOnly = true)
    public GapAnalysisResponse getMyGaps(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<SkillGap> gaps = skillGapRepository.findByUserIdAndIsClosedFalseOrderByImportanceLevelAsc(userId);

        if (gaps.isEmpty()) {
            return GapAnalysisResponse.builder()
                    .targetRole(user.getTargetRole())
                    .totalGaps(0)
                    .criticalGaps(0)
                    .totalLearningHours(0)
                    .gaps(new ArrayList<>())
                    .aiRecommendation("No hay análisis de brechas disponible. Sube un CV y analízalo primero.")
                    .build();
        }

        return buildGapAnalysisResponse(user.getTargetRole(), gaps);
    }

    @Transactional
    public void closeGap(Long gapId, Long userId) {
        SkillGap gap = skillGapRepository.findById(gapId)
                .orElseThrow(() -> new RuntimeException("Gap no encontrado"));

        if (!gap.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para modificar este gap");
        }

        gap.setIsClosed(true);
        skillGapRepository.save(gap);
        log.info("Gap {} cerrado para usuario {}", gapId, userId);
    }

    private List<SkillGap> parseGapsResponse(User user, String targetRole, String aiResponse, Set<String> currentSkills) {
        List<SkillGap> gaps = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(aiResponse);
            JsonNode gapsArray = root.get("missing_skills");

            if (gapsArray != null && gapsArray.isArray()) {
                for (JsonNode gapNode : gapsArray) {
                    String skillName = gapNode.get("skill").asText();
                    String importance = gapNode.has("importance")
                            ? gapNode.get("importance").asText()
                            : "MEDIUM";
                    Integer hours = gapNode.has("estimated_hours")
                            ? gapNode.get("estimated_hours").asInt()
                            : 40;
                    String description = gapNode.has("description")
                            ? gapNode.get("description").asText()
                            : "Habilidad necesaria para " + targetRole;

                    // Buscar o crear skill
                    Skill skill = skillRepository.findByNameIgnoreCase(skillName)
                            .orElseGet(() -> createNewSkill(skillName));

                    SkillGap gap = SkillGap.builder()
                            .user(user)
                            .skill(skill)
                            .targetRole(targetRole)
                            .importanceLevel(importance)
                            .estimatedLearningHours(hours)
                            .gapDescription(description)
                            .isClosed(false)
                            .build();

                    gaps.add(gap);
                }
            }
        } catch (Exception e) {
            log.error("Error parseando gaps: {}", e.getMessage());
            // Fallback: crear gaps genéricos
            gaps.add(createGenericGap(user, targetRole, "Skill adicional requerida", "HIGH", 50));
        }

        return gaps;
    }

    private SkillGap createGenericGap(User user, String targetRole, String skillName, String importance, Integer hours) {
        Skill skill = skillRepository.findByNameIgnoreCase(skillName)
                .orElseGet(() -> createNewSkill(skillName));

        return SkillGap.builder()
                .user(user)
                .skill(skill)
                .targetRole(targetRole)
                .importanceLevel(importance)
                .estimatedLearningHours(hours)
                .gapDescription("Habilidad recomendada para " + targetRole)
                .isClosed(false)
                .build();
    }

    private Skill createNewSkill(String skillName) {
        Skill skill = Skill.builder()
                .name(skillName)
                .category("TECHNICAL")
                .isActive(true)
                .build();
        return skillRepository.save(skill);
    }

    private GapAnalysisResponse buildGapAnalysisResponse(String targetRole, List<SkillGap> gaps) {
        List<SkillGapDTO> gapDTOs = gaps.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        int criticalCount = (int) gaps.stream()
                .filter(g -> "CRITICAL".equals(g.getImportanceLevel()))
                .count();

        int totalHours = gaps.stream()
                .mapToInt(g -> g.getEstimatedLearningHours() != null ? g.getEstimatedLearningHours() : 0)
                .sum();

        String recommendation = generateRecommendation(gaps);

        return GapAnalysisResponse.builder()
                .targetRole(targetRole)
                .totalGaps(gaps.size())
                .criticalGaps(criticalCount)
                .totalLearningHours(totalHours)
                .gaps(gapDTOs)
                .aiRecommendation(recommendation)
                .build();
    }

    private String generateRecommendation(List<SkillGap> gaps) {
        if (gaps.isEmpty()) {
            return "¡Excelente! No se detectaron brechas significativas.";
        }

        long criticalGaps = gaps.stream()
                .filter(g -> "CRITICAL".equals(g.getImportanceLevel()))
                .count();

        if (criticalGaps > 0) {
            return String.format("Tienes %d habilidad(es) crítica(s) que debes priorizar. " +
                    "Te recomendamos enfocarte primero en estas antes de aplicar a posiciones.", criticalGaps);
        }

        return String.format("Has identificado %d área(s) de mejora. " +
                        "Con dedicación constante, podrás cerrar estas brechas en aproximadamente %d horas de estudio.",
                gaps.size(),
                gaps.stream().mapToInt(g -> g.getEstimatedLearningHours() != null ? g.getEstimatedLearningHours() : 0).sum());
    }

    private SkillGapDTO mapToDTO(SkillGap gap) {
        // Buscar recursos recomendados para esta skill
        List<String> resources = resourceRepository.findByIsActiveTrue().stream()
                .filter(r -> r.getTitle().toLowerCase().contains(gap.getSkill().getName().toLowerCase()))
                .limit(3)
                .map(r -> r.getTitle())
                .collect(Collectors.toList());

        return SkillGapDTO.builder()
                .id(gap.getId())
                .skillId(gap.getSkill().getId())
                .skillName(gap.getSkill().getName())
                .skillCategory(gap.getSkill().getCategory())
                .importanceLevel(gap.getImportanceLevel())
                .estimatedLearningHours(gap.getEstimatedLearningHours())
                .gapDescription(gap.getGapDescription())
                .isClosed(gap.getIsClosed())
                .recommendedResources(resources)
                .build();
    }
}

