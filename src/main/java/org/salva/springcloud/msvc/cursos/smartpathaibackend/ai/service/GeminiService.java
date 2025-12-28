package org.salva.springcloud.msvc.cursos.smartpathaibackend.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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

    public String analyzeCVText(String cvText, String targetRole) {
        String prompt = String.format("""
        Analiza el siguiente CV y proporciona feedback detallado.
        
        Rol objetivo del candidato: %s
        
        CV:
        %s
        
        Proporciona el análisis en formato JSON con esta estructura:
        {
          "overall_score": 8.5,
          "strengths": ["punto fuerte 1", "punto fuerte 2", "punto fuerte 3"],
          "weaknesses": ["debilidad 1", "debilidad 2"],
          "improvements": ["mejora 1", "mejora 2", "mejora 3"]
        }
        
        El overall_score debe ser de 0 a 10.
        """,
                targetRole != null ? targetRole : "No especificado",
                cvText.length() > 8000 ? cvText.substring(0, 8000) : cvText
        );

        return callGemini(prompt);
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

    public String detectSkillsFromText(String cvText) {
        String prompt = String.format("""
        Analiza el siguiente CV y extrae TODAS las habilidades técnicas y blandas mencionadas.
        
        CV:
        %s
        
        Responde en formato JSON con esta estructura:
        {
          "skills": [
            {
              "name": "Java",
              "proficiency": "ADVANCED",
              "years": 5,
              "confidence": 0.95
            },
            {
              "name": "Spring Boot",
              "proficiency": "INTERMEDIATE",
              "years": 3,
              "confidence": 0.9
            }
          ]
        }
        
        Niveles de proficiency: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
        Confidence: 0.0 a 1.0 (qué tan seguro estás de que el candidato tiene esa skill)
        """,
                cvText.length() > 8000 ? cvText.substring(0, 8000) : cvText
        );

        return callGemini(prompt);
    }

    public String analyzeSkillGapsForRole(String targetRole, List<String> currentSkills) {
        String skillsList = String.join(", ", currentSkills);

        String prompt = String.format("""
        Analiza las brechas de habilidades para el rol: %s
        
        Habilidades actuales del candidato:
        %s
        
        Identifica las habilidades que le FALTAN para ese rol y responde en JSON:
        {
          "missing_skills": [
            {
              "skill": "Docker",
              "importance": "CRITICAL",
              "estimated_hours": 60,
              "description": "Esencial para deployment moderno"
            },
            {
              "skill": "Kubernetes",
              "importance": "HIGH",
              "estimated_hours": 80,
              "description": "Importante para orquestación de contenedores"
            }
          ]
        }
        
        Niveles de importance: CRITICAL, HIGH, MEDIUM, LOW
        estimated_hours: horas de estudio estimadas para dominar la skill
        """,
                targetRole,
                skillsList.isEmpty() ? "Ninguna identificada aún" : skillsList
        );

        return callGemini(prompt);
    }

    public String generateInterviewQuestions(String targetRole, String difficultyLevel,
                                             String interviewType, Integer numberOfQuestions) {
        String prompt = String.format("""
    Genera exactamente %d preguntas de entrevista para el puesto de "%s".
    
    Nivel de dificultad: %s
    Tipo de entrevista: %s
    
    IMPORTANTE: Responde ÚNICAMENTE con JSON válido en este formato EXACTO:
    {
      "questions": [
        {
          "question": "texto de la pregunta 1",
          "type": "TECHNICAL"
        },
        {
          "question": "texto de la pregunta 2",
          "type": "BEHAVIORAL"
        }
      ]
    }
    
    NO incluyas:
    - Explicaciones adicionales
    - Texto antes o después del JSON
    - Bloques de código markdown
    
    Solo devuelve el JSON puro.
    Tipos válidos: TECHNICAL, BEHAVIORAL, SITUATIONAL
    """,
                numberOfQuestions,
                targetRole,
                difficultyLevel != null ? difficultyLevel : "INTERMEDIATE",
                interviewType != null ? interviewType : "MIXED"
        );

        try {
            log.info("=== LLAMANDO A GEMINI ===");
            log.info("Prompt: {}", prompt);

            String response = callGemini(prompt);

            log.info("=== RESPUESTA CRUDA DE GEMINI ===");
            log.info(response);

            String cleanedResponse = response.replace("``````", "").trim();

            log.info("=== RESPUESTA LIMPIA ===");
            log.info(cleanedResponse);

            return cleanedResponse;

        } catch (Exception e) {
            log.error("❌ ERROR GENERANDO PREGUNTAS CON GEMINI: {}", e.getMessage(), e);

            return """
            {
              "questions": [
                {"question": "Error al generar pregunta con IA. Intenta de nuevo.", "type": "TECHNICAL"}
              ]
            }
            """;
        }
    }


    public String evaluateInterviewAnswer(String question, String answer, String targetRole) {

        // ✅ MOCK temporal para testing
        if (apiKey == null || apiKey.isEmpty()) {
            return """
        {
          "score": 8.5,
          "feedback": "Excelente respuesta que demuestra buen conocimiento del tema.",
          "strengths": ["Claridad en la explicación", "Buenos ejemplos prácticos"],
          "improvements": ["Podría profundizar en casos edge", "Considerar mencionar mejores prácticas"]
        }
        """;
        }

        String prompt = String.format("""
        Evalúa la siguiente respuesta de entrevista:
        
        Pregunta: %s
        Respuesta del candidato: %s
        Rol objetivo: %s
        
        Proporciona feedback constructivo en formato JSON:
        {
          "score": 8.5,
          "feedback": "Buena respuesta que demuestra comprensión del concepto...",
          "strengths": ["Claridad en la explicación", "Buenos ejemplos"],
          "improvements": ["Podría profundizar en...", "Considerar mencionar..."]
        }
        
        Score: 0-10 (donde 10 es excelente)
        """,
                question,
                answer.length() > 2000 ? answer.substring(0, 2000) : answer,
                targetRole
        );

        return callGemini(prompt);
    }

    public String generateOverallInterviewFeedback(String targetRole, Double avgScore, Integer totalQuestions) {
        String prompt = String.format("""
        Genera feedback general para una práctica de entrevista:
        
        Rol objetivo: %s
        Score promedio: %.2f/10
        Total de preguntas: %d
        
        Proporciona un resumen ejecutivo del desempeño, fortalezas generales 
        y áreas de mejora para el candidato.
        
        Responde en formato de texto natural (no JSON), máximo 300 palabras.
        """,
                targetRole,
                avgScore,
                totalQuestions
        );

        return callGemini(prompt);
    }


    private String callGemini(String prompt) {
        String url = String.format("%s/%s:generateContent?key=%s", apiUrl, model, apiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("maxOutputTokens", 1000);
        requestBody.put("generationConfig", generationConfig);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        log.info("Llamando a Gemini API: {}", url.replace(apiKey, "***"));

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                log.debug("Respuesta de Gemini: {}", body);

                List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> responseContent = (Map<String, Object>) candidate.get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) responseContent.get("parts");

                    if (parts != null && !parts.isEmpty()) {
                        String text = (String) parts.get(0).get("text");
                        log.info("Respuesta de Gemini obtenida exitosamente");
                        return text;
                    }
                }
            }
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("❌ CUOTA DE GEMINI EXCEDIDA: {}", e.getMessage());
            throw new RuntimeException("Has alcanzado el límite de solicitudes de Gemini API. Por favor espera unos minutos o cambia al modelo gemini-pro.");
        } catch (Exception e) {
            log.error("❌ ERROR LLAMANDO A GEMINI: {}", e.getMessage(), e);
            throw new RuntimeException("Error comunicándose con Gemini API: " + e.getMessage());
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
