package org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.repository;

import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.entity.CV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CVRepository extends JpaRepository<CV, Long> {
    List<CV> findByUserIdOrderByCreatedAtDesc(Long userId);
}