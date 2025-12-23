package org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.ai.service.GeminiService;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.entity.CV;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.repository.CVRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.listener.NotificationEventListener;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.dto.CVSkillDTO;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.entity.CVSkill;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.entity.Skill;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.repository.CVSkillRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillDetectionService {

    private final CVRepository cvRepository;
    private final CVSkillRepository cvSkillRepository;
    private final SkillRepository skillRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;
    private final NotificationEventListener notificationEventListener;

    @Transactional
    public List<CVSkillDTO> detectSkillsFromCV(Long cvId, Long userId) {
        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("CV no encontrado"));

        if (!cv.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para analizar este CV");
        }

        // Verificar si ya se detectaron skills
        List<CVSkill> existing = cvSkillRepository.findByCvId(cvId);
        if (!existing.isEmpty()) {
            log.info("Skills ya detectadas para CV {}, retornando existentes", cvId);
            return existing.stream().map(this::mapToDTO).collect(Collectors.toList());
        }

        // Llamar a IA para detectar skills
        String aiResponse = geminiService.detectSkillsFromText(cv.getExtractedText());
        log.info("Skills detectadas por IA: {}", aiResponse);

        // Parsear respuesta
        List<CVSkill> detectedSkills = parseSkillsResponse(cv, aiResponse);

        // Guardar
        List<CVSkill> saved = cvSkillRepository.saveAll(detectedSkills);

        notificationEventListener.onSkillsDetected(userId, saved.size());

        return saved.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CVSkillDTO> getCVSkills(Long cvId, Long userId) {
        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("CV no encontrado"));

        if (!cv.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para ver este CV");
        }

        return cvSkillRepository.findByCvIdOrderByConfidenceScoreDesc(cvId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private List<CVSkill> parseSkillsResponse(CV cv, String aiResponse) {
        List<CVSkill> cvSkills = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(aiResponse);
            JsonNode skillsArray = root.get("skills");

            if (skillsArray != null && skillsArray.isArray()) {
                for (JsonNode skillNode : skillsArray) {
                    String skillName = skillNode.get("name").asText();
                    String proficiency = skillNode.has("proficiency")
                            ? skillNode.get("proficiency").asText()
                            : "INTERMEDIATE";
                    Integer years = skillNode.has("years")
                            ? skillNode.get("years").asInt()
                            : 1;
                    Double confidence = skillNode.has("confidence")
                            ? skillNode.get("confidence").asDouble()
                            : 0.8;

                    // Buscar o crear skill
                    Skill skill = skillRepository.findByNameIgnoreCase(skillName)
                            .orElseGet(() -> createNewSkill(skillName));

                    CVSkill cvSkill = CVSkill.builder()
                            .cv(cv)
                            .skill(skill)
                            .proficiencyLevel(proficiency)
                            .yearsOfExperience(years)
                            .detectedByAI(true)
                            .confidenceScore(confidence)
                            .build();

                    cvSkills.add(cvSkill);
                }
            }
        } catch (Exception e) {
            log.error("Error parseando skills: {}", e.getMessage());
        }

        return cvSkills;
    }

    private Skill createNewSkill(String skillName) {
        Skill skill = Skill.builder()
                .name(skillName)
                .category("TECHNICAL")
                .isActive(true)
                .build();
        return skillRepository.save(skill);
    }

    private CVSkillDTO mapToDTO(CVSkill cvSkill) {
        return CVSkillDTO.builder()
                .id(cvSkill.getId())
                .cvId(cvSkill.getCv().getId())
                .skillId(cvSkill.getSkill().getId())
                .skillName(cvSkill.getSkill().getName())
                .proficiencyLevel(cvSkill.getProficiencyLevel())
                .yearsOfExperience(cvSkill.getYearsOfExperience())
                .confidenceScore(cvSkill.getConfidenceScore())
                .detectedByAI(cvSkill.getDetectedByAI())
                .build();
    }
}

