package org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {
    private String title;
    private String message;
    private String notificationType;
    private String category;
    private String actionUrl;
    private String priority;
}
