package com.example.ilms.dto.grading;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class GradingResponseDTO {
    private UUID id;
    private UUID submissionId;
    private UUID graderId;
    private String graderName;
    private BigDecimal score;
    private String feedback;
    private LocalDateTime gradedAt;
    private Integer correctAnswersCount;
    private BigDecimal scaledScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
