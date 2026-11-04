package com.example.ilms.dto.submission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter
public class SubmissionGradeRequestDTO {
    @NotNull
    @JsonDeserialize(using = SubmissionJsonDeserializer.Decimal.class)
    private BigDecimal score;
    @JsonDeserialize(using = SubmissionJsonDeserializer.Text.class)
    private String feedback;
    @JsonIgnore
    @Setter(lombok.AccessLevel.NONE)
    private boolean feedbackPresent;
    @Size(max = 0, message = "Chưa hỗ trợ chấm theo rubric")
    @JsonDeserialize(contentUsing = SubmissionJsonDeserializer.Decimal.class)
    private Map<String, BigDecimal> rubricScores;

    @JsonSetter("feedback")
    public void setFeedback(String feedback) {
        this.feedback = feedback;
        this.feedbackPresent = true;
    }
}
