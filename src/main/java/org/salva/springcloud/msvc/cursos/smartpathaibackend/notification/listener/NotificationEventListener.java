package org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.service.NotificationService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    // Notificación cuando se analiza un CV
    public void onCVAnalyzed(Long userId, Long cvId, Double score) {
        String message = String.format(
                "Tu CV ha sido analizado. Score: %.1f/10. Revisa el análisis detallado.",
                score
        );

        notificationService.createSystemNotification(
                userId,
                "📄 CV Analizado",
                message,
                "CV_ANALYSIS"
        );
    }

    // Notificación cuando se detectan skills
    public void onSkillsDetected(Long userId, Integer skillCount) {
        String message = String.format(
                "Hemos detectado %d habilidades en tu CV. Revisa el detalle.",
                skillCount
        );

        notificationService.createSystemNotification(
                userId,
                "🎯 Skills Detectadas",
                message,
                "SKILL_GAP"
        );
    }

    // Notificación cuando se completa una entrevista
    public void onInterviewCompleted(Long userId, Double score) {
        String message = String.format(
                "¡Entrevista completada! Tu score: %.1f/10. Revisa el feedback detallado.",
                score
        );

        notificationService.createSystemNotification(
                userId,
                "🎤 Entrevista Completada",
                message,
                "INTERVIEW"
        );
    }

    // Notificación cuando se genera una recomendación
    public void onRecommendationGenerated(Long userId, Integer count) {
        String message = String.format(
                "Hemos generado %d nuevas recomendaciones personalizadas para ti.",
                count
        );

        notificationService.createSystemNotification(
                userId,
                "💡 Nuevas Recomendaciones",
                message,
                "RECOMMENDATION"
        );
    }
}

