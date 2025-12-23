package org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.repository.UserRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.common.dto.ApiResponse;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.dto.NotificationDTO;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.dto.NotificationSummaryDTO;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Sistema de notificaciones")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Obtener todas mis notificaciones")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getMyNotifications(
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        List<NotificationDTO> notifications = notificationService.getUserNotifications(userId);

        return ResponseEntity.ok(ApiResponse.success("Notificaciones obtenidas", notifications));
    }

    @GetMapping("/unread")
    @Operation(summary = "Obtener notificaciones no leídas")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getUnreadNotifications(
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        List<NotificationDTO> notifications = notificationService.getUnreadNotifications(userId);

        return ResponseEntity.ok(ApiResponse.success("Notificaciones no leídas", notifications));
    }

    @GetMapping("/summary")
    @Operation(summary = "Obtener resumen de notificaciones")
    public ResponseEntity<ApiResponse<NotificationSummaryDTO>> getNotificationSummary(
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        NotificationSummaryDTO summary = notificationService.getNotificationSummary(userId);

        return ResponseEntity.ok(ApiResponse.success("Resumen obtenido", summary));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Marcar notificación como leída")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        notificationService.markAsRead(id, userId);

        return ResponseEntity.ok(ApiResponse.success("Notificación marcada como leída", null));
    }

    @PutMapping("/mark-all-read")
    @Operation(summary = "Marcar todas las notificaciones como leídas")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        notificationService.markAllAsRead(userId);

        return ResponseEntity.ok(ApiResponse.success("Todas las notificaciones marcadas como leídas", null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una notificación")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        notificationService.deleteNotification(id, userId);

        return ResponseEntity.ok(ApiResponse.success("Notificación eliminada", null));
    }

    @DeleteMapping("/delete-read")
    @Operation(summary = "Eliminar todas las notificaciones leídas")
    public ResponseEntity<ApiResponse<Void>> deleteAllRead(
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        notificationService.deleteAllRead(userId);

        return ResponseEntity.ok(ApiResponse.success("Notificaciones leídas eliminadas", null));
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }
}

