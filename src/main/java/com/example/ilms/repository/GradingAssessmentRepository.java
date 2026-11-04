package com.example.ilms.repository;

import com.example.ilms.entity.Assessment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/** Minimal read/lock dependency on existing Exam rows; no Exam CRUD or schema changes. */
public interface GradingAssessmentRepository extends Repository<Assessment, UUID> {
    @Query("select g.submission.id from Grading g where g.id = :id")
    Optional<UUID> findSubmissionId(@Param("id") UUID id);

    interface ExamContext {
        BigDecimal getMaxScore();
        LocalDateTime getDeadline();
    }

    @Query(value = "select total_score as maxScore, end_time as deadline from exams where id = :id", nativeQuery = true)
    Optional<ExamContext> findExamContext(@Param("id") UUID id);

    @Query(value = "select total_score as maxScore, end_time as deadline from exams where id = :id for update", nativeQuery = true)
    Optional<ExamContext> findExamContextForUpdate(@Param("id") UUID id);

    @Query("""
            select count(c) > 0 from ClassEntity c where c.deletedAt is null and
            (c.owner.id = :userId or c.id in
                (select m.classEntity.id from ClassMember m where m.user.id = :userId
                 and m.role = 'TEACHER' and m.status = 'ACTIVE'))
            """)
    boolean canManageAnyClass(@Param("userId") UUID userId);
}
