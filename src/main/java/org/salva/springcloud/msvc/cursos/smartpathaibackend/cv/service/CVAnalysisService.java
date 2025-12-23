package org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.ai.service.GeminiService;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.dto.CVAnalysisDTO;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.entity.CV;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.entity.CVAnalysis;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.repository.CVAnalysisRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.repository.CVRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.listener.NotificationEventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CVAnalysisService {

    private final CVRepository cvRepository;
    private final CVAnalysisRepository analysisRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;
    private final NotificationEventListener  notificationEventListener;

    @Transactional
    public CVAnalysisDTO analyzeCV(Long cvId, Long userId) {
        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("CV no encontrado"));

        if (!cv.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para analizar este CV");
        }

        if (cv.getExtractedText() == null || cv.getExtractedText().isEmpty()) {
            throw new RuntimeException("No se pudo extraer texto del CV");
        }

        // Verificar si ya fue analizado
        if (cv.getIsAnalyzed()) {
            return analysisRepository.findByCvId(cvId)
                    .map(this::mapToDTO)
                    .orElseThrow(() -> new RuntimeException("Análisis no encontrado"));
        }

        // Generar análisis con IA
        String aiResponse = geminiService.analyzeCVText(
                cv.getExtractedText(),
                cv.getUser().getTargetRole()
        );

        log.info("Respuesta de Gemini para CV {}: {}", cvId, aiResponse);

        // Parsear respuesta (asumiendo JSON)
        CVAnalysis analysis = parseAIResponse(cv, aiResponse);

        // Guardar análisis
        CVAnalysis saved = analysisRepository.save(analysis);

        // Marcar CV como analizado
        cv.setIsAnalyzed(true);
        cvRepository.save(cv);

        notificationEventListener.onCVAnalyzed(userId, cvId, saved.getOverallScore());

        return mapToDTO(saved);
    }


    @Transactional(readOnly = true)
    public CVAnalysisDTO getAnalysisByCVId(Long cvId, Long userId) {
        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("CV no encontrado"));

        if (!cv.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para ver este análisis");
        }

        return analysisRepository.findByCvId(cvId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Este CV aún no ha sido analizado"));
    }

    private CVAnalysis parseAIResponse(CV cv, String aiResponse) {
        try {
            // Intentar parsear como JSON
            JsonNode root = objectMapper.readTree(aiResponse);

            List<String> strengths = parseJsonArray(root.get("strengths"));
            List<String> weaknesses = parseJsonArray(root.get("weaknesses"));
            List<String> improvements = parseJsonArray(root.get("improvements"));
            Double score = root.has("overall_score") ? root.get("overall_score").asDouble() : 7.0;

            return CVAnalysis.builder()
                    .cv(cv)
                    .overallScore(score)
                    .strengths(strengths)
                    .weaknesses(weaknesses)
                    .improvements(improvements)
                    .aiFeedback(aiResponse)
                    .aiModel("gemini-pro")
                    .build();

        } catch (Exception e) {
            log.warn("No se pudo parsear respuesta como JSON, usando análisis genérico");

            // Fallback: análisis genérico
            return CVAnalysis.builder()
                    .cv(cv)
                    .overallScore(7.0)
                    .strengths(Arrays.asList("CV bien estructurado"))
                    .weaknesses(Arrays.asList("Requiere más detalles"))
                    .improvements(Arrays.asList("Agregar logros cuantificables"))
                    .aiFeedback(aiResponse)
                    .aiModel("gemini-pro")
                    .build();
        }
    }

    private List<String> parseJsonArray(JsonNode arrayNode) {
        List<String> result = new ArrayList<>();
        if (arrayNode != null && arrayNode.isArray()) {
            arrayNode.forEach(node -> result.add(node.asText()));
        }
        return result;
    }

    private CVAnalysisDTO mapToDTO(CVAnalysis analysis) {
        return CVAnalysisDTO.builder()
                .id(analysis.getId())
                .cvId(analysis.getCv().getId())
                .overallScore(analysis.getOverallScore())
                .strengths(analysis.getStrengths())
                .weaknesses(analysis.getWeaknesses())
                .improvements(analysis.getImprovements())
                .aiFeedback(analysis.getAiFeedback())
                .aiModel(analysis.getAiModel())
                .createdAt(analysis.getCreatedAt())
                .build();
    }


}

