package com.example.ilms.dto.classDTO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
@JsonPropertyOrder({"id"})
@Data
public class ClassResponseDTO {
    private UUID Id;
    private String classCode;
    private String name;
    private String title;
    private String description;
    private String visibility;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private UUID ownerId;
}
