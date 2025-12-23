package org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.repository;

import org.salva.springcloud.msvc.cursos.smartpathaibackend.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    Integer countByUserIdAndIsReadFalse(Long userId);
    List<Notification> findByUserIdAndCategoryOrderByCreatedAtDesc(Long userId, String category);
}
