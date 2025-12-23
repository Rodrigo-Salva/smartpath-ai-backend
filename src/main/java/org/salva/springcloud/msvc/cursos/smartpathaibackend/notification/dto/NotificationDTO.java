package org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private String notificationType;
    private String category;
    private Boolean isRead;
    private String actionUrl;
    private String priority;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
