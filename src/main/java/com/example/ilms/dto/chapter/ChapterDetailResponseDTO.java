package com.example.ilms.dto.chapter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterDetailResponseDTO {
    private UUID id;
    private UUID courseId;
    private String title;
    private String description;
    private Integer orderIndex;
    private Integer totalLessons;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Schema(nullable = true, example = "null")
    private LocalDateTime deletedAt;
}