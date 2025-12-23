package org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.repository;

import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.model.LearningResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningResourceRepository extends JpaRepository<LearningResource, Long> {
    List<LearningResource> findByIsActiveTrue();
    List<LearningResource> findByIsActiveTrueOrderByCreatedAtDesc();
    List<LearningResource> findByResourceTypeAndIsActiveTrue(String resourceType);
    List<LearningResource> findByDifficultyLevelAndIsActiveTrue(String difficultyLevel);
    List<LearningResource> findByIsFreeTrue();
    Optional<LearningResource> findByUrl(String url);
    List<LearningResource> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
}
