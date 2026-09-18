package com.example.ilms.dto.classDTO;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ClassResponseDTO {
    private UUID id;
    private String classCode;
    private String name;
    private String title;
    private String description;
    private String visibility;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
