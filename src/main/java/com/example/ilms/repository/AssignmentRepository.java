package com.example.ilms.repository;

import com.example.ilms.entity.Assignment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface AssignmentRepository extends JpaRepository<Assignment, UUID>, JpaSpecificationExecutor<Assignment> {
    // Submission writers and Assignment configuration writers lock this same existing row first.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Assignment a where a.id = :id")
    Optional<Assignment> findForUpdate(@Param("id") UUID id);

    @Query(value = "select count(*) from exams where id = :id", nativeQuery = true)
    long countExamWithSameId(@Param("id") UUID id);

    @Query(value = "select count(*) from lessons where id = :id", nativeQuery = true)
    long countLesson(@Param("id") UUID id);

    @Query(value = """
            select count(*) from lessons l join chapters ch on ch.id = l.chapter_id
            where l.id = :lessonId and ch.course_id = :courseId
            """, nativeQuery = true)
    long countLessonInCourse(@Param("lessonId") UUID lessonId, @Param("courseId") UUID courseId);

    @Query(value = "select count(*) from submissions where assessment_id = :id", nativeQuery = true)
    long countSubmissions(@Param("id") UUID id);

    @Query(value = """
            select max(g.score) from gradings g join submissions s on s.id = g.submission_id
            where s.assessment_id = :id
            """, nativeQuery = true)
    BigDecimal findHighestGrade(@Param("id") UUID id);
}
