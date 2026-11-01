package com.example.ilms.dto.course;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserCourseResponseDTO {
    private UUID classId;
    private String className;
    private String classCode;

    private UUID courseId;
    private String name;
    private String description;
    private UUID ownerId;

    private LocalDateTime registeredAt;
}