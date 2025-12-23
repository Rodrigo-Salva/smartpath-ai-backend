package org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkAsReadRequest {
    private List<Long> notificationIds;
}
