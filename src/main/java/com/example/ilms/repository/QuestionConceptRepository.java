package com.example.ilms.repository;

import com.example.ilms.entity.QuestionConcept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

public interface QuestionConceptRepository extends JpaRepository<QuestionConcept, UUID> {
    List<QuestionConcept> findByQuestionId(UUID questionId);
    void deleteByQuestionId(UUID questionId);
}
