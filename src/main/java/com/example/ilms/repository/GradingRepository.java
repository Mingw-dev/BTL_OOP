package com.example.ilms.repository;

import com.example.ilms.entity.Grading;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface GradingRepository extends JpaRepository<Grading, UUID> {
    Optional<Grading> findBySubmission_Id(UUID submissionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from Grading g where g.submission.id = :submissionId")
    Optional<Grading> findForUpdate(@Param("submissionId") UUID submissionId);
}
