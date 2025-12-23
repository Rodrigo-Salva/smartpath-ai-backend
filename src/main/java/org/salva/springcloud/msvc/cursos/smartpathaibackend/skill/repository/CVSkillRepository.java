package org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.repository;

import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CVSkillRepository extends JpaRepository<CVSkill, Long> {
    List<CVSkill> findByCvIdOrderByConfidenceScoreDesc(Long cvId);
    List<CVSkill> findByCvId(Long cvId);
}
