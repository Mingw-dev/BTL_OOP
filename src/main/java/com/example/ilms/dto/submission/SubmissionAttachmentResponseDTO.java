package com.example.ilms.dto.submission;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/** Reserved response shape. No Material/Submission association is created. */
@Getter @Setter
public class SubmissionAttachmentResponseDTO {
    private UUID fileId;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
}
