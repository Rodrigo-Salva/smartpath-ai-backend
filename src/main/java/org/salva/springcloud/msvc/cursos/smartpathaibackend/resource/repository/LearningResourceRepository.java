package org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.repository;

import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.model.LearningResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningResourceRepository extends JpaRepository<LearningResource, Long> {

    List<LearningResource> findByIsActiveTrue();

    List<LearningResource> findByResourceType(String resourceType);

    List<LearningResource> findByIsFreeTrue();

    List<LearningResource> findByDifficultyLevel(String difficultyLevel);

    @Query("SELECT r FROM LearningResource r WHERE :tag MEMBER OF r.tags AND r.isActive = true")
    List<LearningResource> findByTag(@Param("tag") String tag);

    List<LearningResource> findByProvider(String provider);
}

