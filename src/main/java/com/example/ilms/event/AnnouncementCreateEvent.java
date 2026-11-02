package com.example.ilms.event;

import java.util.UUID;

public record AnnouncementCreateEvent(UUID classId, UUID announcementId) {
}
// record dùng để tạo một kiểu dữ liệu gom các thông tin liên quan