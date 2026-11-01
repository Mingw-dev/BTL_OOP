package com.example.ilms.repository;

import com.example.ilms.entity.QuestionConcept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
@Repository
public interface QuestionConceptRepository extends JpaRepository<QuestionConcept, UUID> {
    List<QuestionConcept> findByQuestionId(UUID questionId);
    void deleteByQuestionId(UUID questionId);
    List<QuestionConcept> findByConceptId(String conceptId);
    List<QuestionConcept> findByNameContainingIgnoreCase(String keyword);
    boolean existsByQuestionIdAndConceptId(UUID questionId, String conceptId);
    // Xóa tất cả liên kết trong MySQL khi một khái niệm gốc bị xóa bên Neo4j
    @Modifying
    @Transactional
    void deleteByConceptId(String conceptId);
}
