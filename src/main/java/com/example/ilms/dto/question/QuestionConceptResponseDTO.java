package com.example.ilms.dto.question;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class QuestionConceptResponseDTO {
    private UUID id;
    private String conceptId;
    private String name;
    private String description;
    private BigDecimal relevanceWeight;
    private UUID questionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
