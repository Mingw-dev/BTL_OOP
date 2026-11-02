package com.example.ilms.dto.course;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateCourseInstructorRequestDTO {

    @NotNull(message = "ID giảng viên không được để trống")
    private UUID instructorId;
}