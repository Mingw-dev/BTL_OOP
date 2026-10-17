package com.example.ilms.dto.LessonSession;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class LessionSessionResponseDTO {
    private UUID lessonId;
    private UUID classId;
    private UUID roomId;
    private String roomName;
    private UUID teacherId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
