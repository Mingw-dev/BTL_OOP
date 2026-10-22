package com.example.ilms.dto.Attendance;

import com.example.ilms.type.AttendanceStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

// Điểm danh cả lớp
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAttendanceRequestDTO {
    @NotEmpty(message = "Danh sách điểm danh không được rỗng")
    private List<AttendanceItem> attendances;

    @Data
    public static class AttendanceItem {
        @NotNull(message = "ID học viên không được trống")
        private UUID studentId;

        @NotNull(message = "Trạng thái điểm danh không được trống")
        private AttendanceStatus status; // PRESENT, ABSENT, EXCUSED

        private String note;
    }

}
