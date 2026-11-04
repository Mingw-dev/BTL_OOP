package com.example.ilms.dto.submission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class BatchGradeSubmissionsRequestDTO {
    @NotEmpty
    @Valid
    private List<@NotNull GradeItem> grades;

    @Getter @Setter
    public static class GradeItem {
        @NotNull
        @JsonDeserialize(using = SubmissionJsonDeserializer.Id.class)
        private UUID submissionId;
        @NotNull
        @JsonDeserialize(using = SubmissionJsonDeserializer.Decimal.class)
        private BigDecimal score;
        @JsonDeserialize(using = SubmissionJsonDeserializer.Text.class)
        private String feedback;
        @JsonIgnore
        @Setter(lombok.AccessLevel.NONE)
        private boolean feedbackPresent;

        @JsonSetter("feedback")
        public void setFeedback(String feedback) {
            this.feedback = feedback;
            this.feedbackPresent = true;
        }
    }
}
