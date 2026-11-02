package com.example.ilms.specification;

import com.example.ilms.entity.Assessment;
import com.example.ilms.entity.Assignment;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class AssignmentSpecification {
    private AssignmentSpecification() { }

    public static Specification<Assignment> inClass(UUID classId, UUID lessonId, String search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Subquery<UUID> context = query.subquery(UUID.class);
            Root<Assessment> assessment = context.from(Assessment.class);
            context.select(assessment.get("id")).where(
                    cb.equal(assessment.get("id"), root.get("id")),
                    cb.equal(assessment.get("classEntity").get("id"), classId),
                    cb.isNull(assessment.get("course").get("deletedAt")));
            predicates.add(cb.exists(context));
            if (lessonId != null) predicates.add(cb.equal(root.get("lessonId"), lessonId));
            if (search != null && !search.isBlank()) {
                String escaped = search.trim().toLowerCase(Locale.ROOT)
                        .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + escaped + "%", '\\'));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
