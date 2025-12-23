package org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.repository;

import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.entity.CVAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CVAnalysisRepository extends JpaRepository<CVAnalysis, Long> {
    Optional<CVAnalysis> findByCvId(Long cvId);
}
