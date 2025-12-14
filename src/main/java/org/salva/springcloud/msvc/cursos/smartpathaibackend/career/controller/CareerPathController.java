package org.salva.springcloud.msvc.cursos.smartpathaibackend.career.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.career.dto.CareerPathDTO;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.career.dto.CreateCareerPathRequest;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.career.service.CareerPathService;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/career-paths")
@RequiredArgsConstructor
@Tag(name = "Career Paths", description = "Gestión de rutas de carrera")
public class CareerPathController {

    private final CareerPathService careerPathService;

    @PostMapping
    @Operation(summary = "Crear una nueva ruta de carrera")
    public ResponseEntity<ApiResponse<CareerPathDTO>> createCareerPath(
            @Valid @RequestBody CreateCareerPathRequest request) {
        CareerPathDTO careerPath = careerPathService.createCareerPath(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ruta de carrera creada exitosamente", careerPath));
    }

    @GetMapping
    @Operation(summary = "Obtener todas las rutas de carrera activas")
    public ResponseEntity<ApiResponse<List<CareerPathDTO>>> getAllCareerPaths() {
        List<CareerPathDTO> careerPaths = careerPathService.getAllCareerPaths();
        return ResponseEntity.ok(ApiResponse.success("Rutas de carrera obtenidas exitosamente", careerPaths));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una ruta de carrera por ID")
    public ResponseEntity<ApiResponse<CareerPathDTO>> getCareerPathById(@PathVariable Long id) {
        CareerPathDTO careerPath = careerPathService.getCareerPathById(id);
        return ResponseEntity.ok(ApiResponse.success("Ruta de carrera obtenida exitosamente", careerPath));
    }

    @GetMapping("/role/{targetRole}")
    @Operation(summary = "Obtener rutas de carrera por rol objetivo")
    public ResponseEntity<ApiResponse<List<CareerPathDTO>>> getCareerPathsByRole(@PathVariable String targetRole) {
        List<CareerPathDTO> careerPaths = careerPathService.getCareerPathsByRole(targetRole);
        return ResponseEntity.ok(ApiResponse.success("Rutas de carrera filtradas por rol", careerPaths));
    }

    @GetMapping("/difficulty/{difficultyLevel}")
    @Operation(summary = "Obtener rutas de carrera por nivel de dificultad")
    public ResponseEntity<ApiResponse<List<CareerPathDTO>>> getCareerPathsByDifficulty(
            @PathVariable String difficultyLevel) {
        List<CareerPathDTO> careerPaths = careerPathService.getCareerPathsByDifficulty(difficultyLevel);
        return ResponseEntity.ok(ApiResponse.success("Rutas de carrera filtradas por dificultad", careerPaths));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una ruta de carrera")
    public ResponseEntity<ApiResponse<CareerPathDTO>> updateCareerPath(
            @PathVariable Long id,
            @Valid @RequestBody CreateCareerPathRequest request) {
        CareerPathDTO updated = careerPathService.updateCareerPath(id, request);
        return ResponseEntity.ok(ApiResponse.success("Ruta de carrera actualizada exitosamente", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una ruta de carrera (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteCareerPath(@PathVariable Long id) {
        careerPathService.deleteCareerPath(id);
        return ResponseEntity.ok(ApiResponse.success("Ruta de carrera eliminada exitosamente", null));
    }
}
