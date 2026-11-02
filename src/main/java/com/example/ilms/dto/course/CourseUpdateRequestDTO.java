package com.example.ilms.dto.course;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseUpdateRequestDTO {

    private String name;

    private String description;

    private String visibility;
}