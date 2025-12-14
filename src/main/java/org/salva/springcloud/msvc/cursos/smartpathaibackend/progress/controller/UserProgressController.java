package org.salva.springcloud.msvc.cursos.smartpathaibackend.progress.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.common.dto.ApiResponse;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.progress.dto.*;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.progress.service.UserProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
@Tag(name = "User Progress", description = "Gestión del progreso de aprendizaje")
public class UserProgressController {

    private final UserProgressService progressService;

    @PostMapping
    @Operation(summary = "Iniciar tracking de un recurso")
    public ResponseEntity<ApiResponse<ProgressResponse>> createProgress(
            @RequestBody CreateProgressRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        ProgressResponse response = progressService.createProgress(email, request);

        return ResponseEntity.ok(ApiResponse.success(
                "Progreso iniciado exitosamente", response));
    }

    @PutMapping("/{progressId}")
    @Operation(summary = "Actualizar progreso de un recurso")
    public ResponseEntity<ApiResponse<ProgressResponse>> updateProgress(
            @PathVariable Long progressId,
            @RequestBody UpdateProgressRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        ProgressResponse response = progressService.updateProgress(email, progressId, request);

        return ResponseEntity.ok(ApiResponse.success(
                "Progreso actualizado exitosamente", response));
    }

    @PutMapping("/{progressId}/complete")
    @Operation(summary = "Marcar recurso como completado")
    public ResponseEntity<ApiResponse<ProgressResponse>> completeProgress(
            @PathVariable Long progressId,
            Authentication authentication) {

        String email = authentication.getName();
        ProgressResponse response = progressService.completeProgress(email, progressId);

        return ResponseEntity.ok(ApiResponse.success(
                "¡Felicidades! Recurso completado", response));
    }

    @GetMapping("/my-progress")
    @Operation(summary = "Obtener mi progreso de aprendizaje")
    public ResponseEntity<ApiResponse<List<ProgressResponse>>> getMyProgress(
            Authentication authentication) {

        String email = authentication.getName();
        List<ProgressResponse> progress = progressService.getMyProgress(email);

        return ResponseEntity.ok(ApiResponse.success(
                "Progreso obtenido exitosamente", progress));
    }

    @GetMapping("/stats")
    @Operation(summary = "Obtener estadísticas de mi progreso")
    public ResponseEntity<ApiResponse<ProgressStatsResponse>> getMyStats(
            Authentication authentication) {

        String email = authentication.getName();
        ProgressStatsResponse stats = progressService.getMyStats(email);

        return ResponseEntity.ok(ApiResponse.success(
                "Estadísticas obtenidas exitosamente", stats));
    }
}
