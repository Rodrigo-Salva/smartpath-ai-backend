package org.salva.springcloud.msvc.cursos.smartpathaibackend.career.repository;

import org.salva.springcloud.msvc.cursos.smartpathaibackend.career.model.CareerPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerPathRepository extends JpaRepository<CareerPath, Long> {

    List<CareerPath> findByIsActiveTrue();

    List<CareerPath> findByTargetRole(String targetRole);

    List<CareerPath> findByDifficultyLevel(String difficultyLevel);

    List<CareerPath> findByTargetRoleAndDifficultyLevel(String targetRole, String difficultyLevel);
}

