package com.example.ilms.dto.chapter;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChapterCreateRequestDTO {

    @NotBlank(message = "Tên chương học không được để trống")
    private String title;
    private String description;
    private Integer orderIndex;
}