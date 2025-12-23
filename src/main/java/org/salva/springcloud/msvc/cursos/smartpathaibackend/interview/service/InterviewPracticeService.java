package org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.ai.service.GeminiService;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.repository.UserRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.dto.*;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.entity.InterviewPractice;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.entity.InterviewQuestion;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.repository.InterviewPracticeRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.repository.InterviewQuestionRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.listener.NotificationEventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewPracticeService {

    private final InterviewPracticeRepository practiceRepository;
    private final InterviewQuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;
    private final NotificationEventListener notificationEventListener;

    @Transactional
    public InterviewPracticeDTO startInterview(Long userId, StartInterviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar request
        if (request.getNumberOfQuestions() == null || request.getNumberOfQuestions() < 5) {
            request.setNumberOfQuestions(10);
        }

        String targetRole = request.getTargetRole() != null
                ? request.getTargetRole()
                : user.getTargetRole() != null ? user.getTargetRole() : "Desarrollador";

        // Crear práctica de entrevista
        InterviewPractice practice = InterviewPractice.builder()
                .user(user)
                .targetRole(targetRole)
                .difficultyLevel(request.getDifficultyLevel() != null ? request.getDifficultyLevel() : "INTERMEDIATE")
                .interviewType(request.getInterviewType() != null ? request.getInterviewType() : "MIXED")
                .totalQuestions(request.getNumberOfQuestions())
                .answeredQuestions(0)
                .status("IN_PROGRESS")
                .build();

        InterviewPractice savedPractice = practiceRepository.save(practice);

        // Generar preguntas con IA
        String aiResponse = geminiService.generateInterviewQuestions(
                targetRole,
                request.getDifficultyLevel(),
                request.getInterviewType(),
                request.getNumberOfQuestions()
        );

        log.info("Preguntas generadas por IA: {}", aiResponse);

        // Parsear y guardar preguntas
        List<InterviewQuestion> questions = parseQuestionsResponse(savedPractice, aiResponse);
        questionRepository.saveAll(questions);

        return mapToDTO(savedPractice);
    }

    @Transactional(readOnly = true)
    public List<InterviewQuestionDTO> getInterviewQuestions(Long interviewId, Long userId) {
        InterviewPractice practice = practiceRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Entrevista no encontrada"));

        if (!practice.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para ver esta entrevista");
        }

        return questionRepository.findByInterviewPracticeIdOrderByQuestionNumberAsc(interviewId)
                .stream()
                .map(this::mapQuestionToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InterviewQuestionDTO getNextQuestion(Long interviewId, Long userId) {
        InterviewPractice practice = practiceRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Entrevista no encontrada"));

        if (!practice.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para ver esta entrevista");
        }

        // Buscar primera pregunta no contestada
        List<InterviewQuestion> questions = questionRepository
                .findByInterviewPracticeIdOrderByQuestionNumberAsc(interviewId);

        return questions.stream()
                .filter(q -> !q.getIsAnswered())
                .findFirst()
                .map(this::mapQuestionToDTO)
                .orElse(null);
    }

    @Transactional
    public InterviewQuestionDTO answerQuestion(Long interviewId, Long questionId,
                                               AnswerQuestionRequest request, Long userId) {
        InterviewPractice practice = practiceRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Entrevista no encontrada"));

        if (!practice.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para responder esta entrevista");
        }

        InterviewQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Pregunta no encontrada"));

        if (question.getIsAnswered()) {
            throw new RuntimeException("Esta pregunta ya fue respondida");
        }

        // Evaluar respuesta con IA
        String aiFeedback = geminiService.evaluateInterviewAnswer(
                question.getQuestionText(),
                request.getAnswer(),
                practice.getTargetRole()
        );

        // Parsear feedback y score
        Double score = extractScoreFromFeedback(aiFeedback);

        // Actualizar pregunta
        question.setUserAnswer(request.getAnswer());
        question.setAiFeedback(aiFeedback);
        question.setScore(score);
        question.setIsAnswered(true);
        question.setTimeSpentSeconds(request.getTimeSpentSeconds());
        question.setAnsweredAt(LocalDateTime.now());

        InterviewQuestion saved = questionRepository.save(question);

        // Actualizar práctica
        practice.setAnsweredQuestions(practice.getAnsweredQuestions() + 1);

        // Si es la última pregunta, completar entrevista
        if (practice.getAnsweredQuestions().equals(practice.getTotalQuestions())) {
            completeInterview(practice);
        }

        practiceRepository.save(practice);

        return mapQuestionToDTO(saved);
    }

    @Transactional
    public InterviewResultDTO completeInterview(Long interviewId, Long userId) {
        InterviewPractice practice = practiceRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Entrevista no encontrada"));

        if (!practice.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso");
        }

        completeInterview(practice);
        practiceRepository.save(practice);

        return buildInterviewResult(practice);
    }

    private void completeInterview(InterviewPractice practice) {
        List<InterviewQuestion> questions = questionRepository
                .findByInterviewPracticeIdOrderByQuestionNumberAsc(practice.getId());

        // Calcular score promedio
        double avgScore = questions.stream()
                .filter(q -> q.getScore() != null)
                .mapToDouble(InterviewQuestion::getScore)
                .average()
                .orElse(0.0);

        practice.setOverallScore(avgScore);
        practice.setStatus("COMPLETED");
        practice.setCompletedAt(LocalDateTime.now());

        // Generar feedback general
        String overallFeedback = geminiService.generateOverallInterviewFeedback(
                practice.getTargetRole(),
                avgScore,
                questions.size()
        );

        practice.setAiFeedback(overallFeedback);

        notificationEventListener.onInterviewCompleted(practice.getUser().getId(), avgScore);
    }


    @Transactional(readOnly = true)
    public InterviewResultDTO getInterviewResult(Long interviewId, Long userId) {
        InterviewPractice practice = practiceRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Entrevista no encontrada"));

        if (!practice.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso");
        }

        if (!"COMPLETED".equals(practice.getStatus())) {
            throw new RuntimeException("Esta entrevista aún no ha sido completada");
        }

        return buildInterviewResult(practice);
    }

    @Transactional(readOnly = true)
    public List<InterviewHistoryDTO> getMyInterviewHistory(Long userId) {
        return practiceRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToHistoryDTO)
                .collect(Collectors.toList());
    }

    private List<InterviewQuestion> parseQuestionsResponse(InterviewPractice practice, String aiResponse) {
        List<InterviewQuestion> questions = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(aiResponse);
            JsonNode questionsArray = root.get("questions");

            if (questionsArray != null && questionsArray.isArray()) {
                int questionNumber = 1;
                for (JsonNode questionNode : questionsArray) {
                    String text = questionNode.get("question").asText();
                    String type = questionNode.has("type")
                            ? questionNode.get("type").asText()
                            : "TECHNICAL";

                    InterviewQuestion question = InterviewQuestion.builder()
                            .interviewPractice(practice)
                            .questionNumber(questionNumber++)
                            .questionText(text)
                            .questionType(type)
                            .isAnswered(false)
                            .build();

                    questions.add(question);
                }
            }
        } catch (Exception e) {
            log.error("Error parseando preguntas: {}", e.getMessage());
            // Fallback: crear preguntas genéricas
            for (int i = 1; i <= practice.getTotalQuestions(); i++) {
                questions.add(InterviewQuestion.builder()
                        .interviewPractice(practice)
                        .questionNumber(i)
                        .questionText("Pregunta genérica " + i)
                        .questionType("TECHNICAL")
                        .isAnswered(false)
                        .build());
            }
        }

        return questions;
    }

    private Double extractScoreFromFeedback(String feedback) {
        try {
            JsonNode root = objectMapper.readTree(feedback);
            if (root.has("score")) {
                return root.get("score").asDouble();
            }
        } catch (Exception e) {
            log.warn("No se pudo extraer score del feedback, usando default");
        }
        return 7.0; // Default score
    }

    private InterviewResultDTO buildInterviewResult(InterviewPractice practice) {
        List<InterviewQuestion> questions = questionRepository
                .findByInterviewPracticeIdOrderByQuestionNumberAsc(practice.getId());

        List<QuestionResultDTO> questionResults = questions.stream()
                .map(q -> QuestionResultDTO.builder()
                        .questionNumber(q.getQuestionNumber())
                        .questionText(q.getQuestionText())
                        .userAnswer(q.getUserAnswer())
                        .score(q.getScore())
                        .feedback(q.getAiFeedback())
                        .build())
                .collect(Collectors.toList());

        return InterviewResultDTO.builder()
                .interviewId(practice.getId())
                .targetRole(practice.getTargetRole())
                .overallScore(practice.getOverallScore())
                .totalQuestions(practice.getTotalQuestions())
                .answeredQuestions(practice.getAnsweredQuestions())
                .questionResults(questionResults)
                .overallFeedback(practice.getAiFeedback())
                .strengths(List.of("Comunicación clara", "Buenos ejemplos"))
                .improvements(List.of("Profundizar en aspectos técnicos"))
                .build();
    }

    private InterviewPracticeDTO mapToDTO(InterviewPractice practice) {
        return InterviewPracticeDTO.builder()
                .id(practice.getId())
                .targetRole(practice.getTargetRole())
                .difficultyLevel(practice.getDifficultyLevel())
                .interviewType(practice.getInterviewType())
                .totalQuestions(practice.getTotalQuestions())
                .answeredQuestions(practice.getAnsweredQuestions())
                .overallScore(practice.getOverallScore())
                .status(practice.getStatus())
                .createdAt(practice.getCreatedAt())
                .completedAt(practice.getCompletedAt())
                .build();
    }

    private InterviewQuestionDTO mapQuestionToDTO(InterviewQuestion question) {
        return InterviewQuestionDTO.builder()
                .id(question.getId())
                .interviewPracticeId(question.getInterviewPractice().getId())
                .questionNumber(question.getQuestionNumber())
                .questionText(question.getQuestionText())
                .questionType(question.getQuestionType())
                .userAnswer(question.getUserAnswer())
                .aiFeedback(question.getAiFeedback())
                .score(question.getScore())
                .isAnswered(question.getIsAnswered())
                .timeSpentSeconds(question.getTimeSpentSeconds())
                .build();
    }

    private InterviewHistoryDTO mapToHistoryDTO(InterviewPractice practice) {
        return InterviewHistoryDTO.builder()
                .id(practice.getId())
                .targetRole(practice.getTargetRole())
                .interviewType(practice.getInterviewType())
                .overallScore(practice.getOverallScore())
                .status(practice.getStatus())
                .createdAt(practice.getCreatedAt())
                .build();
    }
}

