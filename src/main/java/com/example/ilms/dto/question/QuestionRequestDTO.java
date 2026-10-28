package com.example.ilms.dto.question;

import com.example.ilms.dto.common.ConceptMappingDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class QuestionRequestDTO {
    @NotBlank(message = "Nội dung câu hỏi không được để trống")
    private String content;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    @NotBlank(message = "Đáp án đúng không được để trống")
    private String correctAnswer;
    private Double defaultPoints;
    private String difficultyLevel;
    private List<ConceptMappingDTO> concepts;
}