package com.example.ilms.dto.course;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CourseResponseDTO {
    private UUID id;
    private String courseCode;
    private String name;
    private String description;
    private String visibility;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private UUID ownerId;
}