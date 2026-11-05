package com.example.ilms.service;

import com.example.ilms.dto.Attendance.AttendanceResponseDTO;
import com.example.ilms.dto.Attendance.BatchAttendanceRequestDTO;
import com.example.ilms.dto.LessonSession.LessonSessionResponseDTO;
import com.example.ilms.dto.LessonSession.LessonSessionCreateDTO;
import com.example.ilms.entity.*;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.ilms.type.AttendanceStatus;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
@Service
@RequiredArgsConstructor
public class LessonSessionService {
    private final LessonSessionRepository lessonSessionRepository;
    private final AttendanceReposity attendanceReposity;
    private final RoomRepositoty roomRepositoty;
    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final ClassMemberRepository classMemberRepository;
    private final AnnouncementAccessService announcementAccessService;
    private final ModelMapper modelMapper;
    // tao 1 ca hoc
    @Transactional
    public void createLessonSession(UUID classId, LessonSessionCreateDTO request, User currentUser) {
        announcementAccessService.requiredRoleMember(classId, currentUser);
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new AppException(ErrorCode.INVAILD_LESSONSESSION);
        }
        ClassEntity classEntity = classRepository.findById(classId).orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        Room room = roomRepositoty.findById(request.getRoomId()).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        boolean isRoomBusy = lessonSessionRepository.existsRoomConflict(request.getRoomId(),request.getSessionDate(), request.getStartTime(), request.getEndTime());
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
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<LessonSessionResponseDTO> getClassSessions(UUID classId, User currentUser) {
        announcementAccessService.getActiveMember(classId, currentUser);
        return lessonSessionRepository
                .findByClassEntityIdOrderBySessionDateAscStartTimeAsc(classId)
                .stream() //Stream là một luồng xử lý dữ liệu, cho phép thực hiện các thao tác như lọc, chuyển đổi, sắp xếp dữ liệu mà không cần tự viết vòng lặp for
                .map(lessonSession -> LessonSessionResponseDTO.builder()
                        .classId(lessonSession.getClassEntity().getId())
                        .lessonSessionId(lessonSession.getId())
                        .className(lessonSession.getClassEntity().getName())
                        .sessionDate(lessonSession.getSessionDate())
                        .startTime(lessonSession.getStartTime())
                        .endTime(lessonSession.getEndTime())
                        .note(lessonSession.getNote())
                        .roomName(lessonSession.getRoom().getName())
                        .build()
                )
                // map dùng để chuyển đổi từng phần tử của một Stream thành một giá trị hoặc đối tượng khác.
                .toList();
    }

    @Transactional
    public void takeBatchAttendance(UUID classId, UUID lessonSessionId, BatchAttendanceRequestDTO requestDTO, User currentUser) {
        ClassEntity classEntity = classRepository.findById(classId).orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        LessonSession lessonSession = lessonSessionRepository.findById(lessonSessionId)
                .orElseThrow(() -> new AppException(ErrorCode.SESSION_NOT_FOUND));

        if (!lessonSession.getClassEntity().getId().equals(classId)) {
            throw new AppException(ErrorCode.SESSION_NOT_FOUND);
        }
        announcementAccessService.requiredRoleMember(lessonSession.getClassEntity().getId(), currentUser);

        if(requestDTO == null || requestDTO.getAttendances() == null || requestDTO.getAttendances().size() == 0) {
            throw new AppException(ErrorCode.INVALID_REQUESTS);
        }
        // 2. Thu thập danh sách ID học sinh và kiểm tra trùng lặp ID trong payload
        Set<UUID> studentIds = new HashSet<>();
        for(BatchAttendanceRequestDTO.AttendanceItem item : requestDTO.getAttendances()) {
            if (item == null || item.getStudentId() == null || item.getStatus() == null) {
                throw new AppException(ErrorCode.INVALID_REQUESTS);
            }
            if (!studentIds.add(item.getStudentId())) { // phương thức add của Set sẽ trả về false nếu giá trị đã có
                throw new AppException(ErrorCode.DUPLICATE_ATTENDANCE_STUDENT);
            }
            ClassMember member = classMemberRepository.findByClassEntity_IdAndUser_Id(lessonSession.getClassEntity().getId(), item.getStudentId())
                    .orElseThrow(() -> new AppException(ErrorCode.CLASS_MEMBER_NOT_FOUND));

            if (!"ACTIVE".equals(member.getStatus())) {
                throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
            }

            if (!"STUDENT".equals(member.getRole())) {
                throw new AppException(ErrorCode.CLASS_MEMBER_NOT_STUDENT);
            }
        }

        Map<UUID, Attendance> existingAttendance = new HashMap<>();

        List<Attendance> attendanceList = attendanceReposity.findByLessonSession_Id(lessonSessionId);
        for (Attendance a :  attendanceList) {
            UUID studentId = a.getStudent().getId();
            existingAttendance.put(studentId, a);
        }

        List<Attendance> toSave = new ArrayList<>();
        for(BatchAttendanceRequestDTO.AttendanceItem item : requestDTO.getAttendances()) {
            Attendance attendance = existingAttendance.get(item.getStudentId());
            if(attendance != null) {
                attendance.setStatus(item.getStatus());
                attendance.setNote(item.getNote());
            }
            else {
                User student = userRepository.findById(item.getStudentId())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
                attendance = Attendance.builder()
                        .student(student)
                        .status(item.getStatus())
                        .note(item.getNote())
                        .lessonSession(lessonSession)
                        .build();
            }
            toSave.add(attendance);
        }
        attendanceReposity.saveAll(toSave);
    }
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getSessionAttendances(UUID classId, UUID lessonSessionId, User currentUser) {
        LessonSession lessonSession = lessonSessionRepository.findById(lessonSessionId).orElseThrow(() -> new AppException(ErrorCode.SESSION_NOT_FOUND));
        announcementAccessService.requiredRoleMember(lessonSession.getClassEntity().getId(), currentUser);
        if (!lessonSession.getClassEntity().getId().equals(classId)) {
            throw new AppException(ErrorCode.SESSION_NOT_FOUND);
        }
        return attendanceReposity.findByLessonSession_Id(lessonSessionId)
                .stream()
                .map(a -> AttendanceResponseDTO.builder()
                        .classId(a.getLessonSession().getClassEntity().getId())
                        .sessionId(a.getLessonSession().getId())
                        .studentId(a.getStudent().getId())
                        .status(a.getStatus())
                        .note(a.getNote())  
                        .build()
                    )
                .toList();
    }
}
