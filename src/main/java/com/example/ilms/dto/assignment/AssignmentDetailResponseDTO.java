package com.example.ilms.dto.assignment;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssignmentDetailResponseDTO extends AssignmentSummaryResponseDTO {
    private UUID classId;
    private UUID courseId;
    private String description;
    private String fileAttachmentUrl;
    private LocalDateTime updatedAt;
}
