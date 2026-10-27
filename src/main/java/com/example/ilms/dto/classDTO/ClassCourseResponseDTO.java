package com.example.ilms.dto.classDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassCourseResponseDTO {
    private UUID classId;
    private String className;
    private UUID courseId;
    private String courseCode;
    private String courseTitle;
    private UUID teacherId;
    private String teacherName;
    private String semester;
    private String academicYear;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private LocalDateTime assignedAt;
}