package org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.repository.UserRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.common.dto.ApiResponse;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.dto.*;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.service.InterviewPracticeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interviews")
@RequiredArgsConstructor
@Tag(name = "Interview Practice", description = "Práctica de entrevistas con IA")
public class InterviewController {

    private final InterviewPracticeService interviewService;
    private final UserRepository userRepository;

    @PostMapping("/start")
    @Operation(summary = "Iniciar una nueva práctica de entrevista")
    public ResponseEntity<ApiResponse<InterviewPracticeDTO>> startInterview(
            @RequestBody StartInterviewRequest request,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        InterviewPracticeDTO interview = interviewService.startInterview(userId, request);

        return ResponseEntity.ok(ApiResponse.success(
                "Entrevista iniciada exitosamente. ¡Mucha suerte!",
                interview
        ));
    }

    @GetMapping("/{interviewId}/questions")
    @Operation(summary = "Obtener todas las preguntas de una entrevista")
    public ResponseEntity<ApiResponse<List<InterviewQuestionDTO>>> getInterviewQuestions(
            @PathVariable Long interviewId,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        List<InterviewQuestionDTO> questions = interviewService.getInterviewQuestions(interviewId, userId);

        return ResponseEntity.ok(ApiResponse.success("Preguntas obtenidas", questions));
    }

    @GetMapping("/{interviewId}/next-question")
    @Operation(summary = "Obtener la siguiente pregunta sin responder")
    public ResponseEntity<ApiResponse<InterviewQuestionDTO>> getNextQuestion(
            @PathVariable Long interviewId,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        InterviewQuestionDTO question = interviewService.getNextQuestion(interviewId, userId);

        if (question == null) {
            return ResponseEntity.ok(ApiResponse.success(
                    "No hay más preguntas. Completa la entrevista para ver tu resultado.",
                    null
            ));
        }

        return ResponseEntity.ok(ApiResponse.success("Siguiente pregunta", question));
    }

    @PostMapping("/{interviewId}/questions/{questionId}/answer")
    @Operation(summary = "Responder una pregunta y recibir feedback de IA")
    public ResponseEntity<ApiResponse<InterviewQuestionDTO>> answerQuestion(
            @PathVariable Long interviewId,
            @PathVariable Long questionId,
            @RequestBody AnswerQuestionRequest request,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        InterviewQuestionDTO result = interviewService.answerQuestion(
                interviewId,
                questionId,
                request,
                userId
        );

        return ResponseEntity.ok(ApiResponse.success(
                "Respuesta evaluada. Revisa el feedback de IA.",
                result
        ));
    }

    @PostMapping("/{interviewId}/complete")
    @Operation(summary = "Completar entrevista y obtener resultado final")
    public ResponseEntity<ApiResponse<InterviewResultDTO>> completeInterview(
            @PathVariable Long interviewId,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        InterviewResultDTO result = interviewService.completeInterview(interviewId, userId);

        return ResponseEntity.ok(ApiResponse.success(
                "¡Entrevista completada! Aquí está tu resultado.",
                result
        ));
    }

    @GetMapping("/{interviewId}/result")
    @Operation(summary = "Obtener resultado de una entrevista completada")
    public ResponseEntity<ApiResponse<InterviewResultDTO>> getInterviewResult(
            @PathVariable Long interviewId,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        InterviewResultDTO result = interviewService.getInterviewResult(interviewId, userId);

        return ResponseEntity.ok(ApiResponse.success("Resultado obtenido", result));
    }

    @GetMapping("/history")
    @Operation(summary = "Obtener historial de mis prácticas de entrevistas")
    public ResponseEntity<ApiResponse<List<InterviewHistoryDTO>>> getMyHistory(
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        List<InterviewHistoryDTO> history = interviewService.getMyInterviewHistory(userId);

        return ResponseEntity.ok(ApiResponse.success(
                "Historial de entrevistas obtenido",
                history
        ));
    }

    @GetMapping("/{interviewId}")
    @Operation(summary = "Obtener detalles de una entrevista específica")
    public ResponseEntity<ApiResponse<InterviewPracticeDTO>> getInterviewDetails(
            @PathVariable Long interviewId,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        InterviewPracticeDTO interview = interviewService.getInterviewById(interviewId, userId);

        return ResponseEntity.ok(ApiResponse.success("Detalles obtenidos", interview));
    }


    private Long getUserIdFromAuth(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }
}

