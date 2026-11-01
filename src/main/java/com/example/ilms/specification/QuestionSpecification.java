package com.example.ilms.specification;

import com.example.ilms.entity.Question;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestionSpecification {

    public static Specification<Question> filterQuestions(String search, String difficulty, UUID conceptId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Luôn chỉ lấy các câu hỏi đang active (Soft-delete)
            predicates.add(cb.isTrue(root.get("isActive")));

            // 2. Tìm kiếm theo nội dung câu hỏi (Tương đối)
            if (search != null && !search.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("content")), "%" + search.toLowerCase() + "%"));
            }

            // 3. Lọc theo độ khó
            if (difficulty != null && !difficulty.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("difficultyLevel"), difficulty));
            }

            // 4. Lọc theo Concept ID
            if (conceptId != null) {
                predicates.add(cb.equal(root.get("concept").get("id"), conceptId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}