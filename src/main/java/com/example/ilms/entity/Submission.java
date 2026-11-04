package com.example.ilms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "submissions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Builder.Default
    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber = 1;
    @Builder.Default
    @Column(name = "max_attempts")
    private Integer maxAttempts = 1;
    // Read legacy values without enum conversion; derive the API status from data and Grading.
    @Column(nullable = false, length = 50)
    private String status;
    @Column(name = "submission_text", columnDefinition = "LONGTEXT")
    private String submissionText;
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @OneToOne(mappedBy = "submission", fetch = FetchType.LAZY)
    private Grading grading;
}
