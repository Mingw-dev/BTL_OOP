package com.example.ilms.controller;

import com.example.ilms.constant.SuccessCode;
import com.example.ilms.dto.Attendance.AttendanceResponseDTO;
import com.example.ilms.dto.Attendance.BatchAttendanceRequestDTO;
import com.example.ilms.dto.LessonSession.LessonSessionCreateDTO;
import com.example.ilms.dto.LessonSession.LessonSessionResponseDTO;
import com.example.ilms.dto.response.ApiResponse;
import com.example.ilms.entity.Attendance;
import com.example.ilms.entity.LessonSession;
import com.example.ilms.repository.LessonSessionRepository;
import com.example.ilms.service.LessonSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/classes/{classId}/lessonSessions")
public class LessonSessionController {
    private final LessonSessionService lessonSessionService;

    // Tạo ca học cho 1 lớp học
    @PostMapping
    public ApiResponse<Void> createLessonSession (@RequestBody @Valid LessonSessionCreateDTO request, @PathVariable UUID classId) {
        lessonSessionService.createLessonSession(classId,request);
        return ApiResponse.of(SuccessCode.CREATE_LESSON_SESSION_SUCCESS, null);
    }

    @GetMapping
    public ApiResponse<List<LessonSessionResponseDTO>> getLessonSession (@PathVariable UUID classId) {
        List<LessonSessionResponseDTO> dtoList = lessonSessionService.getClassSessions(classId);
        return ApiResponse.of(SuccessCode.GET_LESSON_SESSION_SUCCESS, dtoList);
    }
    @PostMapping("/{lessonSessionId}/attendances")
    public ApiResponse<Void> takeBatchAttendance (@PathVariable UUID lessonSessionId, @Valid @RequestBody BatchAttendanceRequestDTO requestDTO) {
        lessonSessionService.takeBatchAttendance(lessonSessionId, requestDTO);
        return ApiResponse.of(SuccessCode.TAKE_ATTENDANCE_SUCCESS, null);
    }
    @GetMapping("/{lessonSessionId}/attendances")
    public ApiResponse<List<AttendanceResponseDTO>> getAttendances(@PathVariable UUID lessonSessionId) {
        List<AttendanceResponseDTO> dto = lessonSessionService.getSessionAttendances(lessonSessionId);
        return ApiResponse.of(SuccessCode.GET_ATTENDANCES_SUCCESS, dto);
    }
}
