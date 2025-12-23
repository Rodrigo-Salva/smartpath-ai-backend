package org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "notification_type")
    private String notificationType; // INFO, SUCCESS, WARNING, ERROR

    @Column(name = "category")
    private String category; // CV_ANALYSIS, SKILL_GAP, INTERVIEW, RECOMMENDATION, PROGRESS

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "action_url")
    private String actionUrl;

    @Column(name = "priority")
    private String priority; // LOW, MEDIUM, HIGH, URGENT

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

