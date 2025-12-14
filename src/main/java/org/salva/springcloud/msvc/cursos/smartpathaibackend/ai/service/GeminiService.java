package org.salva.springcloud.msvc.cursos.smartpathaibackend.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String apiUrl;

    @Value("${gemini.model:gemini-pro}")
    private String model;

    private final RestTemplate restTemplate;

    public GeminiService() {
        this.restTemplate = new RestTemplate();
    }

    public String generateRecommendation(String userProfile, String targetRole, String experienceLevel) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("Gemini API key no configurada, retornando recomendación por defecto");
            return generateDefaultRecommendation(targetRole, experienceLevel);
        }

        try {
            String prompt = buildPrompt(userProfile, targetRole, experienceLevel);
            return callGemini(prompt);
        } catch (Exception e) {
            log.error("Error al llamar a Gemini: {}", e.getMessage(), e);
            return generateDefaultRecommendation(targetRole, experienceLevel);
        }
    }

    private String buildPrompt(String userProfile, String targetRole, String experienceLevel) {
        return String.format("""
            Eres un mentor experto en desarrollo de carrera tecnológica.
            
            Usuario:
            - Perfil: %s
            - Rol objetivo: %s
            - Nivel de experiencia: %s
            
            Por favor, recomienda 3 recursos de aprendizaje específicos (cursos, libros, o proyectos)
            que ayuden a este usuario a alcanzar su objetivo. Para cada recurso indica:
            1. Nombre del recurso
            2. Por qué es relevante para este usuario
            3. Habilidades que desarrollará
            
            Responde en formato JSON válido con esta estructura exacta:
            {
              "recommendations": [
                {
                  "title": "Nombre del recurso 1",
                  "reason": "Razón de la recomendación",
                  "skills": ["habilidad 1", "habilidad 2", "habilidad 3"]
                },
                {
                  "title": "Nombre del recurso 2",
                  "reason": "Razón de la recomendación",
                  "skills": ["habilidad 1", "habilidad 2", "habilidad 3"]
                },
                {
                  "title": "Nombre del recurso 3",
                  "reason": "Razón de la recomendación",
                  "skills": ["habilidad 1", "habilidad 2", "habilidad 3"]
                }
              ]
            }
            """, userProfile, targetRole, experienceLevel);
    }

    private String callGemini(String prompt) {
        // URL completa: https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=API_KEY
        String url = String.format("%s/%s:generateContent?key=%s", apiUrl, model, apiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Estructura del request body de Gemini
        Map<String, Object> requestBody = new HashMap<>();

        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));

        requestBody.put("contents", List.of(content));

        // Configuración adicional
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("maxOutputTokens", 1000);
        requestBody.put("generationConfig", generationConfig);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        log.info("Llamando a Gemini API: {}", url.replace(apiKey, "***"));

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            log.debug("Respuesta de Gemini: {}", body);

            // Estructura de respuesta de Gemini:
            // {
            //   "candidates": [
            //     {
            //       "content": {
            //         "parts": [
            //           {
            //             "text": "..."
            //           }
            //         ]
            //       }
            //     }
            //   ]
            // }

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> candidate = candidates.get(0);
                Map<String, Object> cont = (Map<String, Object>) candidate.get("cont");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

                if (parts != null && !parts.isEmpty()) {
                    String text = (String) parts.get(0).get("text");
                    log.info("Respuesta de Gemini obtenida exitosamente");
                    return text;
                }
            }
        }

        throw new RuntimeException("No se pudo obtener respuesta de Gemini API");
    }

    private String generateDefaultRecommendation(String targetRole, String experienceLevel) {
        log.info("Generando recomendación por defecto para rol: {} y nivel: {}", targetRole, experienceLevel);

        return String.format("""
            {
              "recommendations": [
                {
                  "title": "Fundamentos de %s",
                  "reason": "Curso introductorio esencial para nivel %s que cubre los conceptos fundamentales y mejores prácticas de la industria",
                  "skills": ["Conceptos básicos", "Mejores prácticas", "Herramientas fundamentales"]
                },
                {
                  "title": "Proyecto práctico en %s",
                  "reason": "Aplicación práctica de conocimientos mediante un proyecto real que fortalecerá tu portfolio profesional",
                  "skills": ["Experiencia real", "Construcción de portfolio", "Resolución de problemas"]
                },
                {
                  "title": "Certificación profesional en %s",
                  "reason": "Validación oficial de tus habilidades que incrementará tu empleabilidad en el mercado laboral",
                  "skills": ["Credencial profesional", "Conocimiento validado", "Ventaja competitiva"]
                }
              ]
            }
            """, targetRole, experienceLevel, targetRole, targetRole);
    }
}
