package com.example.ilms.dto.classDTO;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AttendanceResponseDTO {
    private UUID id;
    private UUID sessionId;
    private UUID studentId;
    private String status;
    private String note;
}