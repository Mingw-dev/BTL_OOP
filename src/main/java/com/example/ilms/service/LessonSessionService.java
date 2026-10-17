package com.example.ilms.service;

import com.example.ilms.dto.Attendance.BatchAttendanceRequestDTO;
import com.example.ilms.dto.LessonSession.LessionSessionResponseDTO;
import com.example.ilms.dto.LessonSession.LessonSessionCreateDTO;
import com.example.ilms.entity.ClassEntity;
import com.example.ilms.entity.LessonSession;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.example.ilms.repository.RoomReposity;

import java.util.List;
import java.util.UUID;
import com.example.ilms.entity.Room;
@Service
public class LessonSessionService {
    private LessonSessionRepository lessonSessionRepository;
    private AttendanceReposity attendanceReposity;
    private RoomReposity roomReposity;
    private ClassRepository classRepository;
    private UserRepository userRepository;
    private ModelMapper modelMapper;
    // tao 1 ca hoc
    @Transactional
    public void createLessonSession(UUID classId, LessonSessionCreateDTO request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new AppException(ErrorCode.INVAILD_LESSONSESSION);
        }
        ClassEntity classEntity = classRepository.findById(classId).orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        Room room = roomReposity.findById(request.getRoomId()).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        boolean isRoomBusy = lessonSessionRepository.existsRoomConflict(request.getRoomId(), request.getStartTime(), request.getEndTime());
        if (isRoomBusy) {
            throw new AppException(ErrorCode.ROOM_SCHEDULE_CONFLICT);
        }
        LessonSession lessonSession = LessonSession.builder()
                .classEntity(classEntity)
                .room(room)
                .sessionDate(request.getSessionDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .startShift(request.getStartShift())
                .endShift(request.getEndShift())
                .note(request.getNote())
                .build();
        lessonSessionRepository.save(lessonSession);
    }
    // danh sách ca học
    public List<LessionSessionResponseDTO> getClassSessions(UUID classId) {
        return lessonSessionRepository
                .findByClassEntityIdOrderByStartTimeAsc(classId)
                .stream() //Stream là một luồng xử lý dữ liệu, cho phép thực hiện các thao tác như lọc, chuyển đổi, sắp xếp dữ liệu mà không cần tự viết vòng lặp for
                .map(lessonSession -> modelMapper.map(lessonSession,LessionSessionResponseDTO.class))
                // map dùng để chuyển đổi từng phần tử của một Stream thành một giá trị hoặc đối tượng khác.
                .toList();
    }

    @Transactional
    public void takeBatchAttendance(UUID lessonSessionId, BatchAttendanceRequestDTO requestDTO) {
        LessonSession lessonSession = lessonSessionRepository.findById(lessonSessionId).orElseThrow(() -> new AppException(ErrorCode.SESSION_NOT_FOUND));

    }

}
