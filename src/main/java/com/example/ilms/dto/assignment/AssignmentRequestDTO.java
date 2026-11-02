package com.example.ilms.dto.assignment;

import com.example.ilms.dto.submission.SubmissionJsonDeserializer;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;
import lombok.Getter;
import org.hibernate.validator.constraints.URL;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** One request shape; Create adds required fields, while updates track absent versus explicit null. */
@Getter
public class AssignmentRequestDTO {
    public interface Create extends Default { }

    @NotNull(groups = Create.class)
    @JsonDeserialize(using = SubmissionJsonDeserializer.Id.class)
    private UUID courseId;
    @JsonDeserialize(using = SubmissionJsonDeserializer.Id.class)
    private UUID lessonId;
    @NotBlank(groups = Create.class)
    @Size(max = 255)
    @JsonDeserialize(using = SubmissionJsonDeserializer.Text.class)
    private String title;
    @JsonDeserialize(using = SubmissionJsonDeserializer.Text.class)
    private String description;
    @NotNull @DecimalMin(value = "0", inclusive = false) @DecimalMax("100.00")
    @JsonDeserialize(using = SubmissionJsonDeserializer.Decimal.class)
    private BigDecimal maxScore = new BigDecimal("10.00");
    @JsonDeserialize(using = LocalTime.class)
    private LocalDateTime dueDate;
    @NotNull
    @JsonDeserialize(using = Flag.class)
    private Boolean allowLateSubmission = true;
    @Size(max = 500) @URL
    @JsonDeserialize(using = SubmissionJsonDeserializer.Text.class)
    private String fileAttachmentUrl;

    @JsonIgnore
    private final Set<String> providedFields = new HashSet<>();

    public boolean has(String field) { return providedFields.contains(field); }
    public void setCourseId(UUID value) { providedFields.add("courseId"); courseId = value; }
    public void setLessonId(UUID value) { providedFields.add("lessonId"); lessonId = value; }
    public void setTitle(String value) { providedFields.add("title"); title = value; }
    public void setDescription(String value) { providedFields.add("description"); description = value; }
    public void setMaxScore(BigDecimal value) { providedFields.add("maxScore"); maxScore = value; }
    public void setDueDate(LocalDateTime value) { providedFields.add("dueDate"); dueDate = value; }
    public void setAllowLateSubmission(Boolean value) { providedFields.add("allowLateSubmission"); allowLateSubmission = value; }
    public void setFileAttachmentUrl(String value) { providedFields.add("fileAttachmentUrl"); fileAttachmentUrl = value; }

    @JsonAnySetter
    public void rejectUnknownField(String name, JsonNode value) {
        throw new IllegalArgumentException("Trường không được hỗ trợ: " + name);
    }

    @AssertTrue(message = "Tiêu đề được gửi phải có nội dung")
    @JsonIgnore
    public boolean isTitleValid() { return !has("title") || (title != null && !title.isBlank()); }

    @AssertTrue(message = "Điểm tối đa có nhiều nhất hai chữ số thập phân có nghĩa")
    @JsonIgnore
    public boolean isScorePrecisionValid() { return maxScore == null || maxScore.stripTrailingZeros().scale() <= 2; }

    @AssertTrue(message = "Thời gian phải phù hợp DATETIME(6)")
    @JsonIgnore
    public boolean isTimeValid() {
        return dueDate == null || (dueDate.getYear() >= 1000 && dueDate.getYear() <= 9999 && dueDate.getNano() % 1000 == 0);
    }

    @AssertTrue(message = "URL tài liệu không được blank; gửi NULL để xóa")
    @JsonIgnore
    public boolean isAttachmentUrlValid() { return fileAttachmentUrl == null || !fileAttachmentUrl.isBlank(); }

    public static class Flag extends JsonDeserializer<Boolean> {
        @Override
        public Boolean deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if (!parser.hasToken(JsonToken.VALUE_TRUE) && !parser.hasToken(JsonToken.VALUE_FALSE)) {
                return (Boolean) context.handleUnexpectedToken(Boolean.class, parser);
            }
            return parser.getBooleanValue();
        }
    }

    public static class LocalTime extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if (!parser.hasToken(JsonToken.VALUE_STRING)) {
                return (LocalDateTime) context.handleUnexpectedToken(LocalDateTime.class, parser);
            }
            try {
                return LocalDateTime.parse(parser.getText());
            } catch (DateTimeParseException ex) {
                return (LocalDateTime) context.handleWeirdStringValue(LocalDateTime.class, parser.getText(),
                        "Thời gian ISO LocalDateTime không chứa offset hoặc Z");
            }
        }
    }
}
