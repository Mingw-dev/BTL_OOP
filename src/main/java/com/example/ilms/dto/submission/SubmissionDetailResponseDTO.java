package com.example.ilms.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SubmissionDetailResponseDTO {
    private UUID id;
    private UUID assignmentId;
    private String assignmentTitle;
    private UUID studentId;
    private String studentName;
    private String content;
    private List<SubmissionAttachmentResponseDTO> attachments;
    private String status;
    private LocalDateTime submittedAt;
    @JsonProperty("isLate")
    private Boolean isLate;
    private BigDecimal score;
    private String feedback;
    private UUID gradedByTeacherId;
    private String gradedByTeacherName;
    private LocalDateTime gradedAt;
    private Integer submissionAttempt;
}
