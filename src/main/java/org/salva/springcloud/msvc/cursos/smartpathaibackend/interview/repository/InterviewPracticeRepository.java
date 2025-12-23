package org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.repository;

import org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.entity.InterviewPractice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewPracticeRepository extends JpaRepository<InterviewPractice, Long> {
    List<InterviewPractice> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<InterviewPractice> findByUserIdAndStatus(Long userId, String status);
}