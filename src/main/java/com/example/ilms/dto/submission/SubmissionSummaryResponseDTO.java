package com.example.ilms.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SubmissionSummaryResponseDTO {
    private UUID id;
    private UUID studentId;
    private String studentCode;
    private String studentName;
    private String studentAvatar;
    private String status;
    private LocalDateTime submittedAt;
    @JsonProperty("isLate")
    private Boolean isLate;
    private BigDecimal score;
    private Integer attachmentCount;
}
