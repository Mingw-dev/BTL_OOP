package com.example.ilms.dto.assignment;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AssignmentSummaryResponseDTO {
    private UUID id;
    private UUID lessonId;
    private String title;
    private BigDecimal maxScore;
    private LocalDateTime dueDate;
    private Boolean allowLateSubmission;
    private LocalDateTime createdAt;
}
