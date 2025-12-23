package org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.repository.UserRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.common.dto.ApiResponse;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.dto.*;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.service.GapAnalysisService;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.service.SkillDetectionService;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.service.SkillService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
@Tag(name = "Skills Management", description = "Detección y gestión de habilidades")
public class SkillController {

    private final SkillService skillService;
    private final SkillDetectionService detectionService;
    private final GapAnalysisService gapAnalysisService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Obtener catálogo de skills disponibles")
    public ResponseEntity<ApiResponse<List<SkillDTO>>> getAllSkills(
            @RequestParam(required = false) String category) {

        List<SkillDTO> skills = category != null
                ? skillService.getSkillsByCategory(category)
                : skillService.getAllActiveSkills();

        return ResponseEntity.ok(ApiResponse.success("Skills obtenidas", skills));
    }

    @PostMapping("/detect/{cvId}")
    @Operation(summary = "Detectar skills de un CV con IA")
    public ResponseEntity<ApiResponse<List<CVSkillDTO>>> detectSkills(
            @PathVariable Long cvId,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        List<CVSkillDTO> skills = detectionService.detectSkillsFromCV(cvId, userId);

        return ResponseEntity.ok(ApiResponse.success(
                "Skills detectadas exitosamente",
                skills
        ));
    }

    @GetMapping("/cv/{cvId}")
    @Operation(summary = "Obtener skills detectadas de un CV")
    public ResponseEntity<ApiResponse<List<CVSkillDTO>>> getCVSkills(
            @PathVariable Long cvId,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        List<CVSkillDTO> skills = detectionService.getCVSkills(cvId, userId);

        return ResponseEntity.ok(ApiResponse.success("Skills del CV", skills));
    }

    @PostMapping("/gap-analysis/{cvId}")
    @Operation(summary = "Realizar análisis de brechas de habilidades")
    public ResponseEntity<ApiResponse<GapAnalysisResponse>> analyzeGaps(
            @PathVariable Long cvId,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        GapAnalysisResponse analysis = gapAnalysisService.analyzeSkillGaps(userId, cvId);

        return ResponseEntity.ok(ApiResponse.success(
                "Análisis de brechas completado",
                analysis
        ));
    }

    @GetMapping("/my-gaps")
    @Operation(summary = "Obtener mis brechas de habilidades")
    public ResponseEntity<ApiResponse<GapAnalysisResponse>> getMyGaps(
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        GapAnalysisResponse gaps = gapAnalysisService.getMyGaps(userId);

        return ResponseEntity.ok(ApiResponse.success("Brechas obtenidas", gaps));
    }

    @PutMapping("/gaps/{gapId}/close")
    @Operation(summary = "Marcar una brecha como cerrada")
    public ResponseEntity<ApiResponse<Void>> closeGap(
            @PathVariable Long gapId,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        gapAnalysisService.closeGap(gapId, userId);

        return ResponseEntity.ok(ApiResponse.success("Brecha cerrada exitosamente", null));
    }

    @GetMapping("/my-skills")
    @Operation(summary = "Obtener mis habilidades objetivo")
    public ResponseEntity<ApiResponse<List<UserSkillDTO>>> getMySkills(
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        List<UserSkillDTO> skills = skillService.getUserSkills(userId);

        return ResponseEntity.ok(ApiResponse.success("Tus habilidades", skills));
    }

    @PostMapping("/my-skills")
    @Operation(summary = "Agregar una skill manualmente a mi perfil")
    public ResponseEntity<ApiResponse<UserSkillDTO>> addMySkill(
            @RequestBody AddSkillRequest request,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        UserSkillDTO skill = skillService.addUserSkill(userId, request);

        return ResponseEntity.ok(ApiResponse.success("Skill agregada", skill));
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }
}

