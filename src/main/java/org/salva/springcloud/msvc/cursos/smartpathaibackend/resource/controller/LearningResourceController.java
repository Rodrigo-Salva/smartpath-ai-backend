package org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.common.dto.ApiResponse;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.dto.CreateResourceRequest;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.dto.LearningResourceDTO;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.service.LearningResourceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
@Tag(name = "Learning Resources", description = "Gestión de recursos de aprendizaje")
public class LearningResourceController {

    private final LearningResourceService resourceService;

    @PostMapping
    @Operation(summary = "Crear un nuevo recurso de aprendizaje")
    public ResponseEntity<ApiResponse<LearningResourceDTO>> createResource(
            @Valid @RequestBody CreateResourceRequest request) {
        LearningResourceDTO resource = resourceService.createResource(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Recurso creado exitosamente", resource));
    }

    @GetMapping
    @Operation(summary = "Obtener todos los recursos activos")
    public ResponseEntity<ApiResponse<List<LearningResourceDTO>>> getAllResources() {
        List<LearningResourceDTO> resources = resourceService.getAllResources();
        return ResponseEntity.ok(ApiResponse.success("Recursos obtenidos exitosamente", resources));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un recurso por ID")
    public ResponseEntity<ApiResponse<LearningResourceDTO>> getResourceById(@PathVariable Long id) {
        LearningResourceDTO resource = resourceService.getResourceById(id);
        return ResponseEntity.ok(ApiResponse.success("Recurso obtenido exitosamente", resource));
    }

    @GetMapping("/type/{resourceType}")
    @Operation(summary = "Obtener recursos por tipo")
    public ResponseEntity<ApiResponse<List<LearningResourceDTO>>> getResourcesByType(
            @PathVariable String resourceType) {
        List<LearningResourceDTO> resources = resourceService.getResourcesByType(resourceType);
        return ResponseEntity.ok(ApiResponse.success("Recursos filtrados por tipo", resources));
    }

    @GetMapping("/free")
    @Operation(summary = "Obtener recursos gratuitos")
    public ResponseEntity<ApiResponse<List<LearningResourceDTO>>> getFreeResources() {
        List<LearningResourceDTO> resources = resourceService.getFreeResources();
        return ResponseEntity.ok(ApiResponse.success("Recursos gratuitos obtenidos", resources));
    }

    @GetMapping("/tag/{tag}")
    @Operation(summary = "Obtener recursos por tag")
    public ResponseEntity<ApiResponse<List<LearningResourceDTO>>> getResourcesByTag(@PathVariable String tag) {
        List<LearningResourceDTO> resources = resourceService.getResourcesByTag(tag);
        return ResponseEntity.ok(ApiResponse.success("Recursos filtrados por tag", resources));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un recurso")
    public ResponseEntity<ApiResponse<LearningResourceDTO>> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody CreateResourceRequest request) {
        LearningResourceDTO updated = resourceService.updateResource(id, request);
        return ResponseEntity.ok(ApiResponse.success("Recurso actualizado exitosamente", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un recurso (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok(ApiResponse.success("Recurso eliminado exitosamente", null));
    }
}
