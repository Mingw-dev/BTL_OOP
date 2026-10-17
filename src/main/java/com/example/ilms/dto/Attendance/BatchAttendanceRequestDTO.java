package com.example.ilms.dto.Attendance;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

// Điểm danh cả lớp
@Data
public class BatchAttendanceRequestDTO {
    @NotEmpty(message = "Danh sách điểm danh không được rỗng")
    private List<AttendanceItem> attendances;

    @Data
    public static class AttendanceItem {
        @NotNull(message = "ID học viên không được trống")
        private UUID studentId;

        @NotNull(message = "Trạng thái điểm danh không được trống")
        private String status; // PRESENT, ABSENT, EXCUSED

        private String note;
    }

}
