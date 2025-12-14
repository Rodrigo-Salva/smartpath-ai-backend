package org.salva.springcloud.msvc.cursos.smartpathaibackend.recommendation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.common.dto.ApiResponse;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.recommendation.dto.AIRecommendationDTO;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.recommendation.service.AIRecommendationService;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User; // ✅ AGREGAR
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.repository.UserRepository; // ✅ AGREGAR
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@Tag(name = "AI Recommendations", description = "Recomendaciones personalizadas con IA")
public class AIRecommendationController {

    private final AIRecommendationService recommendationService;
    private final UserRepository userRepository; // ✅ AGREGAR

    @PostMapping("/generate")
    @Operation(summary = "Generar recomendaciones personalizadas con IA")
    public ResponseEntity<ApiResponse<List<AIRecommendationDTO>>> generateRecommendations(
            Authentication authentication) {
        // ✅ CORRECCIÓN: Obtener el email del authentication y buscar el user
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Long userId = user.getId();

        List<AIRecommendationDTO> recommendations = recommendationService.generateRecommendationsForUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Recomendaciones generadas exitosamente", recommendations));
    }

    @GetMapping("/my-recommendations")
    @Operation(summary = "Obtener mis recomendaciones")
    public ResponseEntity<ApiResponse<List<AIRecommendationDTO>>> getMyRecommendations(
            Authentication authentication) {
        // ✅ CORRECCIÓN: Igual aquí
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Long userId = user.getId();

        List<AIRecommendationDTO> recommendations = recommendationService.getUserRecommendations(userId);
        return ResponseEntity.ok(ApiResponse.success("Recomendaciones obtenidas", recommendations));
    }

    @PutMapping("/{id}/accept")
    @Operation(summary = "Aceptar una recomendación")
    public ResponseEntity<ApiResponse<AIRecommendationDTO>> acceptRecommendation(
            @PathVariable Long id,
            @RequestParam(required = false) String feedback) {
        AIRecommendationDTO updated = recommendationService.acceptRecommendation(id, feedback);
        return ResponseEntity.ok(ApiResponse.success("Recomendación aceptada", updated));
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Rechazar una recomendación")
    public ResponseEntity<ApiResponse<AIRecommendationDTO>> rejectRecommendation(
            @PathVariable Long id,
            @RequestParam(required = false) String feedback) {
        AIRecommendationDTO updated = recommendationService.rejectRecommendation(id, feedback);
        return ResponseEntity.ok(ApiResponse.success("Recomendación rechazada", updated));
    }
}
