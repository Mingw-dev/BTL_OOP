package com.example.ilms.dto.Attendance;

import com.example.ilms.type.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponseDTO{
    private UUID classId;
    private UUID sessionId;
    private UUID studentId;
    private AttendanceStatus status;
    private String note;
}

