package com.example.ilms.dto.grading;

import com.example.ilms.dto.submission.SubmissionJsonDeserializer;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/** Create requires score; update preserves absent fields and accepts explicit nullable values. */
@Getter
public class GradingRequestDTO {
    public interface Create extends Default { }

    @NotNull(groups = Create.class)
    @DecimalMin("0.00") @DecimalMax("100.00")
    @JsonDeserialize(using = SubmissionJsonDeserializer.Decimal.class)
    private BigDecimal score;

    @JsonDeserialize(using = SubmissionJsonDeserializer.Text.class)
    private String feedback;

    @PositiveOrZero
    @JsonDeserialize(using = GradingJsonDeserializer.IntegerValue.class)
    private Integer correctAnswersCount;

    @DecimalMin("0.00") @DecimalMax("999.99")
    @JsonDeserialize(using = SubmissionJsonDeserializer.Decimal.class)
    private BigDecimal scaledScore;

    @JsonIgnore
    private final Set<String> providedFields = new HashSet<>();

    public boolean has(String name) { return providedFields.contains(name); }
    public void setScore(BigDecimal value) { providedFields.add("score"); score = value; }
    public void setFeedback(String value) { providedFields.add("feedback"); feedback = value; }
    public void setCorrectAnswersCount(Integer value) {
        providedFields.add("correctAnswersCount"); correctAnswersCount = value;
    }
    public void setScaledScore(BigDecimal value) { providedFields.add("scaledScore"); scaledScore = value; }

    @JsonIgnore
    @AssertTrue(message = "Điểm có nhiều nhất hai chữ số thập phân có nghĩa")
    public boolean isScorePrecisionValid() {
        return precisionValid(score) && precisionValid(scaledScore);
    }

    @JsonIgnore
    @AssertTrue(message = "Điểm được gửi không được NULL")
    public boolean isProvidedScoreValid() { return !has("score") || score != null; }

    @JsonAnySetter
    public void rejectUnknownField(String name, JsonNode value) {
        throw new IllegalArgumentException("Trường chấm điểm không được hỗ trợ: " + name);
    }

    private boolean precisionValid(BigDecimal value) {
        return value == null || value.stripTrailingZeros().scale() <= 2;
    }
}
