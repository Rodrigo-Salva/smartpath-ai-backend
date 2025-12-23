package org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.repository;

import org.salva.springcloud.msvc.cursos.smartpathaibackend.interview.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {
    List<InterviewQuestion> findByInterviewPracticeIdOrderByQuestionNumberAsc(Long interviewPracticeId);
    Optional<InterviewQuestion> findByInterviewPracticeIdAndQuestionNumber(Long interviewPracticeId, Integer questionNumber);
}