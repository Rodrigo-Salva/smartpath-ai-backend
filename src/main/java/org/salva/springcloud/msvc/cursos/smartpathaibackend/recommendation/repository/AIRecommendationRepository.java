package org.salva.springcloud.msvc.cursos.smartpathaibackend.recommendation.repository;

import org.salva.springcloud.msvc.cursos.smartpathaibackend.recommendation.model.AIRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIRecommendationRepository extends JpaRepository<AIRecommendation, Long> {

    List<AIRecommendation> findByUserId(Long userId);

    @Query("SELECT r FROM AIRecommendation r WHERE r.user.id = :userId ORDER BY r.recommendationScore DESC")
    List<AIRecommendation> findTopRecommendationsByUserId(@Param("userId") Long userId);

    List<AIRecommendation> findByUserIdAndIsAccepted(Long userId, Boolean isAccepted);

    List<AIRecommendation> findByResourceId(Long resourceId);
}

