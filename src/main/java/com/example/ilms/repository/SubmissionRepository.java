package com.example.ilms.repository;

import com.example.ilms.entity.Submission;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID>, JpaSpecificationExecutor<Submission> {
    @Override
    @EntityGraph(attributePaths = {"user", "grading", "grading.grader"})
    Page<Submission> findAll(Specification<Submission> specification, Pageable pageable);

    List<Submission> findByAssessment_IdAndUser_Id(UUID assessmentId, UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Submission s where s.assessment.id = :assessmentId and s.user.id = :userId")
    List<Submission> findCurrentForUpdate(@Param("assessmentId") UUID assessmentId, @Param("userId") UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Submission s where s.id = :id")
    Optional<Submission> findForUpdate(@Param("id") UUID id);

    @Query("select s.assessment.id from Submission s where s.id = :id")
    Optional<UUID> findAssessmentId(@Param("id") UUID id);

    long countByAssessment_IdAndUser_Id(UUID assessmentId, UUID userId);

    @Query("""
            select s.user.id from Submission s where s.assessment.id = :assessmentId
            group by s.user.id having count(s) > 1
            """)
    List<UUID> findDuplicateStudents(@Param("assessmentId") UUID assessmentId);
}
