package com.example.ilms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "questions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EntityListeners(AuditingEntityListener.class)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "course_id", columnDefinition = "BINARY(16)")
    private UUID courseId;

    @Column(name = "chapter_id", columnDefinition = "BINARY(16)")
    private UUID chapterId;

    @Column(name = "created_by", columnDefinition = "BINARY(16)")
    private UUID createdBy;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "question_type", nullable = false, length = 30)
    private String questionType; // SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE

    @Column(name = "difficulty_level", nullable = false, length = 20)
    private String difficultyLevel;

    @Column(name = "default_points")
    private Double defaultPoints;

    @Column(name = "option_a", nullable = false, columnDefinition = "TEXT")
    private String optionA;

    @Column(name = "option_b", nullable = false, columnDefinition = "TEXT")
    private String optionB;

    @Column(name = "option_c", columnDefinition = "TEXT")
    private String optionC;

    @Column(name = "option_d", columnDefinition = "TEXT")
    private String optionD;

    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "source_type", nullable = false, length = 30)
    private String sourceType; // MANUAL, AI_GENERATED

    @Column(name = "ai_prompt_context", columnDefinition = "TEXT")
    private String aiPromptContext;

    @Column(name = "is_verified_by_teacher", nullable = false)
    private Boolean isVerifiedByTeacher;

    @Column(name = "is_active")
    private Boolean isActive;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}