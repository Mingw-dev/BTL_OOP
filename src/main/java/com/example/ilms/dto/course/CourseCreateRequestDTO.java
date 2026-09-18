package com.example.ilms.dto.course;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class CourseCreateRequestDTO {

    @NotBlank(message = "Tên môn học không được để trống")
    private String name;

    private String description;

    private String visibility;
}