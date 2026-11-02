package com.example.ilms.dto.announcement;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AnnouncementResponseDTO {
    private UUID id;
    private String title;
    private String content;
    private boolean isPinned;
    private UUID authorId;
    private String authorName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
