package org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.common.dto.ApiResponse;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.dto.*;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.service.LearningResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
@Tag(name = "Learning Resources", description = "Gestión de recursos de aprendizaje")
public class LearningResourceController {

    private final LearningResourceService resourceService;

    @GetMapping
    @Operation(summary = "Obtener todos los recursos de aprendizaje (con filtros opcionales)")
    public ResponseEntity<ApiResponse<List<LearningResourceDTO>>> getAllResources(
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String difficultyLevel,
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) Boolean isFree,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Integer minHours,
            @RequestParam(required = false) Integer maxHours,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "50") Integer size) {

        ResourceFilterDTO filter = ResourceFilterDTO.builder()
                .resourceType(resourceType)
                .difficultyLevel(difficultyLevel)
                .provider(provider)
                .isFree(isFree)
                .tag(tag)
                .minHours(minHours)
                .maxHours(maxHours)
                .build();

        List<LearningResourceDTO> resources = resourceService.getAllResources(filter, page, size);

        return ResponseEntity.ok(ApiResponse.success("Recursos obtenidos", resources));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un recurso por ID")
    public ResponseEntity<ApiResponse<LearningResourceDTO>> getResourceById(
            @PathVariable Long id) {

        LearningResourceDTO resource = resourceService.getResourceById(id);
        return ResponseEntity.ok(ApiResponse.success("Recurso obtenido", resource));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Obtener recursos por tipo (COURSE, BOOK, VIDEO, etc.)")
    public ResponseEntity<ApiResponse<List<LearningResourceDTO>>> getResourcesByType(
            @PathVariable String type) {

        List<LearningResourceDTO> resources = resourceService.getResourcesByType(type);
        return ResponseEntity.ok(ApiResponse.success("Recursos filtrados por tipo", resources));
    }

    @GetMapping("/difficulty/{level}")
    @Operation(summary = "Obtener recursos por nivel de dificultad")
    public ResponseEntity<ApiResponse<List<LearningResourceDTO>>> getResourcesByDifficulty(
            @PathVariable String level) {

        List<LearningResourceDTO> resources = resourceService.getResourcesByDifficulty(level);
        return ResponseEntity.ok(ApiResponse.success("Recursos filtrados por dificultad", resources));
    }

    @GetMapping("/free")
    @Operation(summary = "Obtener solo recursos gratuitos")
    public ResponseEntity<ApiResponse<List<LearningResourceDTO>>> getFreeResources() {
        List<LearningResourceDTO> resources = resourceService.getFreeResources();
        return ResponseEntity.ok(ApiResponse.success("Recursos gratuitos", resources));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar recursos por título o descripción")
    public ResponseEntity<ApiResponse<List<LearningResourceDTO>>> searchResources(
            @RequestParam String query) {

        List<LearningResourceDTO> resources = resourceService.searchResources(query);
        return ResponseEntity.ok(ApiResponse.success("Resultados de búsqueda", resources));
    }

    @GetMapping("/stats")
    @Operation(summary = "Obtener estadísticas de recursos")
    public ResponseEntity<ApiResponse<ResourceStatsDTO>> getResourceStats() {
        ResourceStatsDTO stats = resourceService.getResourceStats();
        return ResponseEntity.ok(ApiResponse.success("Estadísticas obtenidas", stats));
    }

    // ============ ENDPOINTS DE ADMIN ============

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Crear nuevo recurso de aprendizaje")
    public ResponseEntity<ApiResponse<LearningResourceDTO>> createResource(
            @RequestBody CreateResourceRequest request) {

        LearningResourceDTO created = resourceService.createResource(request);
        return ResponseEntity.ok(ApiResponse.success("Recurso creado exitosamente", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Actualizar recurso de aprendizaje")
    public ResponseEntity<ApiResponse<LearningResourceDTO>> updateResource(
            @PathVariable Long id,
            @RequestBody UpdateResourceRequest request) {

        LearningResourceDTO updated = resourceService.updateResource(id, request);
        return ResponseEntity.ok(ApiResponse.success("Recurso actualizado", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Desactivar recurso (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok(ApiResponse.success("Recurso desactivado", null));
    }

    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Eliminar recurso permanentemente")
    public ResponseEntity<ApiResponse<Void>> permanentDeleteResource(@PathVariable Long id) {
        resourceService.permanentDeleteResource(id);
        return ResponseEntity.ok(ApiResponse.success("Recurso eliminado permanentemente", null));
    }
}