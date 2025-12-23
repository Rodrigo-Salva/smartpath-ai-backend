package org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.repository;

import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillGapRepository extends JpaRepository<SkillGap, Long> {
    List<SkillGap> findByUserIdAndIsClosedFalseOrderByImportanceLevelAsc(Long userId);
    List<SkillGap> findByUserIdAndTargetRole(Long userId, String targetRole);
}
