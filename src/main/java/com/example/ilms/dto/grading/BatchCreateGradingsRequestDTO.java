package com.example.ilms.dto.grading;

import com.example.ilms.dto.submission.SubmissionJsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.ConvertGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter @Setter
public class BatchCreateGradingsRequestDTO {
    @NotEmpty
    @Valid
    @ConvertGroup(to = GradingRequestDTO.Create.class)
    private List<@NotNull Item> items;

    @Getter @Setter
    public static class Item extends GradingRequestDTO {
        @NotNull
        @JsonDeserialize(using = SubmissionJsonDeserializer.Id.class)
        private UUID submissionId;
    }
}
