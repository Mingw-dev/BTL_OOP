package com.example.ilms.dto.question;

import com.example.ilms.dto.common.ConceptMappingDTO;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class QuestionResponseDTO {
    private UUID id;
    private String content;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private Double defaultPoints;
    private String difficultyLevel;
    private String conceptName;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ConceptMappingDTO> concepts;
}