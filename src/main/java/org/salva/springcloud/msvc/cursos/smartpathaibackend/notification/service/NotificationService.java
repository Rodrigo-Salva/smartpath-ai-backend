package org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.repository.UserRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.dto.CreateNotificationRequest;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.dto.NotificationDTO;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.dto.NotificationSummaryDTO;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.entity.Notification;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public NotificationDTO createNotification(Long userId, CreateNotificationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Notification notification = Notification.builder()
                .user(user)
                .title(request.getTitle())
                .message(request.getMessage())
                .notificationType(request.getNotificationType() != null ? request.getNotificationType() : "INFO")
                .category(request.getCategory())
                .actionUrl(request.getActionUrl())
                .priority(request.getPriority() != null ? request.getPriority() : "MEDIUM")
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Notificación creada para usuario {}: {}", userId, request.getTitle());

        return mapToDTO(saved);
    }

    @Transactional
    public void createSystemNotification(Long userId, String title, String message, String category) {
        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setTitle(title);
        request.setMessage(message);
        request.setNotificationType("INFO");
        request.setCategory(category);
        request.setPriority("MEDIUM");

        createNotification(userId, request);
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NotificationSummaryDTO getNotificationSummary(Long userId) {
        Integer unreadCount = notificationRepository.countByUserIdAndIsReadFalse(userId);
        List<NotificationDTO> recent = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .limit(5)
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return NotificationSummaryDTO.builder()
                .totalUnread(unreadCount)
                .totalNotifications(recent.size())
                .recentNotifications(recent)
                .build();
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para marcar esta notificación");
        }

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);

        log.info("Notificación {} marcada como leída", notificationId);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);

        unread.forEach(n -> {
            n.setIsRead(true);
            n.setReadAt(LocalDateTime.now());
        });

        notificationRepository.saveAll(unread);
        log.info("Todas las notificaciones marcadas como leídas para usuario {}", userId);
    }

    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta notificación");
        }

        notificationRepository.delete(notification);
        log.info("Notificación {} eliminada", notificationId);
    }

    @Transactional
    public void deleteAllRead(Long userId) {
        List<Notification> read = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(Notification::getIsRead)
                .collect(Collectors.toList());

        notificationRepository.deleteAll(read);
        log.info("Notificaciones leídas eliminadas para usuario {}", userId);
    }

    private NotificationDTO mapToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .category(notification.getCategory())
                .isRead(notification.getIsRead())
                .actionUrl(notification.getActionUrl())
                .priority(notification.getPriority())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}

