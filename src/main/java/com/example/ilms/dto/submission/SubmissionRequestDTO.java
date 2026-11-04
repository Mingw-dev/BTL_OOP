package com.example.ilms.dto.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/** Create and update use the same payload, with different service operations. */
@Getter @Setter
public class SubmissionRequestDTO {
    @NotBlank
    @JsonDeserialize(using = SubmissionJsonDeserializer.Text.class)
    private String content;

    @Size(max = 0, message = "Chưa hỗ trợ liên kết tệp với bài nộp")
    @JsonDeserialize(contentUsing = SubmissionJsonDeserializer.Id.class)
    private List<UUID> attachmentIds;
}
