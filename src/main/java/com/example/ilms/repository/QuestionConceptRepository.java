package com.example.ilms.repository;

import com.example.ilms.entity.QuestionConcept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface QuestionConceptRepository extends JpaRepository<QuestionConcept, UUID> {
    List<QuestionConcept> findByQuestionId(UUID questionId);
    void deleteByQuestionId(UUID questionId);
    List<QuestionConcept> findByConceptId(String conceptId);
    List<QuestionConcept> findByNameContainingIgnoreCase(String keyword);
    boolean existsByQuestionIdAndConceptId(UUID questionId, String conceptId);
}
