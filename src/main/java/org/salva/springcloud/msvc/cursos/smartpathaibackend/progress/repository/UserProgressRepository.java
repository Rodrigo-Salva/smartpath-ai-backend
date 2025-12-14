package org.salva.springcloud.msvc.cursos.smartpathaibackend.progress.repository;

import org.salva.springcloud.msvc.cursos.smartpathaibackend.progress.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    List<UserProgress> findByUserId(Long userId);

    List<UserProgress> findByUserIdAndStatus(Long userId, UserProgress.ProgressStatus status);

    Optional<UserProgress> findByUserIdAndResourceId(Long userId, Long resourceId);

    @Query("SELECT COUNT(p) FROM UserProgress p WHERE p.user.id = :userId AND p.status = 'COMPLETED'")
    Long countCompletedByUserId(Long userId);

    @Query("SELECT AVG(p.progressPercentage) FROM UserProgress p WHERE p.user.id = :userId")
    Double getAverageProgressByUserId(Long userId);
}
