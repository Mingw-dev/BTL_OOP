package com.example.ilms.dto.question;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionConceptRequestDTO {
    @NotNull(message = "ID câu hỏi không được để trống")
    private UUID questionId;

    @NotBlank(message = "ID khái niệm không được để trống")
    private String conceptId;

    @NotBlank(message = "tên khái niệm không được để trống")
    private String name;

    private String description;
    private BigDecimal relevanceWeight;
}