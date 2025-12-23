package org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.repository;

import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByNameIgnoreCase(String name);
    List<Skill> findByIsActiveTrueOrderByNameAsc();
    List<Skill> findByCategoryAndIsActiveTrue(String category);
}
