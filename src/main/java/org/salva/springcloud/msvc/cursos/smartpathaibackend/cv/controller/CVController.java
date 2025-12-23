package org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.repository.UserRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.common.dto.ApiResponse;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.dto.*;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.service.CVService;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.service.CVAnalysisService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cvs")
@RequiredArgsConstructor
@Tag(name = "CV Management", description = "Gestión y análisis de CVs con IA")
public class CVController {

    private final CVService cvService;
    private final CVAnalysisService analysisService;
    private final UserRepository userRepository;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir un CV en formato PDF")
    public ResponseEntity<ApiResponse<CVUploadResponse>> uploadCV(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {

        Long userId = getUserIdFromAuth(authentication);
        CVUploadResponse response = cvService.uploadCV(userId, file);

        return ResponseEntity.ok(ApiResponse.success(
                "CV subido exitosamente. Puedes analizarlo ahora.",
                response
        ));
    }

    @GetMapping
    @Operation(summary = "Obtener todos mis CVs")
    public ResponseEntity<ApiResponse<List<CVListDTO>>> getMyCVs(
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        List<CVListDTO> cvs = cvService.getUserCVs(userId);

        return ResponseEntity.ok(ApiResponse.success("CVs obtenidos", cvs));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalles de un CV específico")
    public ResponseEntity<ApiResponse<CVUploadResponse>> getCVById(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        CVUploadResponse cv = cvService.getCVById(id, userId);

        return ResponseEntity.ok(ApiResponse.success("CV obtenido", cv));
    }

    @PostMapping("/{id}/analyze")
    @Operation(summary = "Analizar CV con IA (Gemini)")
    public ResponseEntity<ApiResponse<CVAnalysisDTO>> analyzeCV(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        CVAnalysisDTO analysis = analysisService.analyzeCV(id, userId);

        return ResponseEntity.ok(ApiResponse.success(
                "CV analizado exitosamente",
                analysis
        ));
    }

    @GetMapping("/{id}/analysis")
    @Operation(summary = "Obtener análisis de un CV")
    public ResponseEntity<ApiResponse<CVAnalysisDTO>> getCVAnalysis(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        CVAnalysisDTO analysis = analysisService.getAnalysisByCVId(id, userId);

        return ResponseEntity.ok(ApiResponse.success("Análisis obtenido", analysis));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un CV")
    public ResponseEntity<ApiResponse<Void>> deleteCV(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        cvService.deleteCV(id, userId);

        return ResponseEntity.ok(ApiResponse.success("CV eliminado", null));
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }
}

