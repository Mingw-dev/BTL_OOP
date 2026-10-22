package com.example.ilms.dto.LessonSession;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class LessonSessionResponseDTO {
    UUID classId;
    UUID lessonSessionId;
    private String className;
    private String roomName;
    private LocalDate sessionDate;
    private String note;
    private LocalTime startTime;
    private LocalTime endTime;
}
