package com.example.ilms.dto.LessonSession;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class LessonSessionCreateDTO {
    private UUID roomId;
    @NotNull(message = "Ngày học không được để trống")
    private LocalDate sessionDate;
    @Min(1)
    private Integer startShift;
    @Min(1)
    private Integer endShift;
    private LocalTime startTime;
    private LocalTime endTime;
    private String note;
}
