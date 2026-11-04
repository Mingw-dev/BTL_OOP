package com.example.ilms.specification;

import com.example.ilms.entity.Submission;
import com.example.ilms.type.SubmissionStatus;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class SubmissionSpecification {
    private SubmissionSpecification() { }

    public static Specification<Submission> filter(UUID assignmentId, LocalDateTime dueDate,
            String status, String search, String sortField, boolean ascending) {
        return (root, query, cb) -> {
            Join<Object, Object> grading = root.join("grading", JoinType.LEFT);
            Expression<String> effectiveStatus = cb.<String>selectCase()
                    .when(cb.isNotNull(grading.get("id")), SubmissionStatus.GRADED.name())
                    .when(dueDate == null ? cb.disjunction()
                            : cb.greaterThan(root.get("submittedAt"), dueDate), SubmissionStatus.LATE.name())
                    .when(cb.greaterThan(root.get("attemptNumber"), 1), SubmissionStatus.RESUBMITTED.name())
                    .otherwise(SubmissionStatus.SUBMITTED.name());
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("assessment").get("id"), assignmentId));
            if (status != null) predicates.add(cb.equal(effectiveStatus, status));
            if (search != null && !search.isBlank()) {
                Path<Object> user = root.get("user");
                Expression<String> middle = cb.trim(cb.coalesce(user.get("middleName"), ""));
                Expression<String> middleWithSpace = cb.<String>selectCase()
                        .when(cb.equal(middle, ""), "").otherwise(cb.concat(middle, " "));
                Expression<String> name = cb.concat(cb.concat(user.get("firstName"), " "),
                        cb.concat(middleWithSpace, user.get("lastName")));
                String pattern = "%" + escapeLike(search.trim().toLowerCase(Locale.ROOT)) + "%";
                predicates.add(cb.or(cb.like(cb.lower(name), pattern, '\\'),
                        cb.like(uuidText(user.get("id"), cb), pattern, '\\')));
            }
            // Pageable has no Sort: order the derived status, rather than the legacy status column.
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                Expression<?> field = "status".equals(sortField) ? effectiveStatus : root.get(sortField);
                List<Order> orders = new ArrayList<>();
                orders.add(ascending ? cb.asc(field) : cb.desc(field));
                if (!"id".equals(sortField)) orders.add(cb.asc(root.get("id")));
                query.orderBy(orders);
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static Expression<String> uuidText(Expression<?> id, CriteriaBuilder cb) {
        Expression<String> hex = cb.lower(cb.function("hex", String.class, id));
        Expression<String> text = cb.substring(hex, 1, 8);
        int[] starts = {9, 13, 17, 21};
        int[] lengths = {4, 4, 4, 12};
        for (int i = 0; i < starts.length; i++) {
            text = cb.concat(cb.concat(text, "-"), cb.substring(hex, starts[i], lengths[i]));
        }
        return text;
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
