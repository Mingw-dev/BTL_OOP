package com.example.ilms.dto.course;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseCreateRequestDTO {

    @NotBlank(message = "Tên môn học không được để trống")
    private String name;

    private String description;

    private String visibility;
}