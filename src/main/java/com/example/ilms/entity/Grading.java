package com.example.ilms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "gradings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Grading {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private Submission submission;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grader_id", nullable = false)
    private User grader;
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;
    @Column(columnDefinition = "TEXT")
    private String feedback;
    @Column(name = "graded_at", nullable = false)
    private LocalDateTime gradedAt;
    @Column(name = "correct_answers_count")
    private Integer correctAnswersCount;
    @Column(name = "scaled_score", precision = 5, scale = 2)
    private BigDecimal scaledScore;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
