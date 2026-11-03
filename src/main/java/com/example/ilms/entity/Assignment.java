package com.example.ilms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/** Assessment and Assignment share an ID by application convention, without a new database FK. */
@Entity
@Table(name = "assignments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Assignment {
    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "lesson_id", columnDefinition = "BINARY(16)")
    private UUID lessonId;
    @Column(nullable = false, length = 255)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(name = "max_score", precision = 5, scale = 2)
    private BigDecimal maxScore;
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    @Column(name = "allow_late_submission")
    private Boolean allowLateSubmission;
    @Column(name = "file_attachment_url", length = 500)
    private String fileAttachmentUrl;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
