package org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSummaryDTO {
    private Integer totalUnread;
    private Integer totalNotifications;
    private List<NotificationDTO> recentNotifications;
}
