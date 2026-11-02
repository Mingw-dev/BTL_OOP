package com.example.ilms.service;

import com.example.ilms.dto.classDTO.AssignCourseToClassRequestDTO;
import com.example.ilms.dto.classDTO.BatchAssignCoursesRequestDTO;
import com.example.ilms.dto.classDTO.ClassCourseResponseDTO;
import com.example.ilms.dto.common.BatchOperationResponseDTO;
import com.example.ilms.dto.common.PageResponse;
import com.example.ilms.entity.ClassCourse;
import com.example.ilms.entity.ClassEntity;
import com.example.ilms.entity.ClassMember;
import com.example.ilms.entity.Course;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.ClassCourseRepository;
import com.example.ilms.repository.ClassMemberRepository;
import com.example.ilms.repository.ClassRepository;
import com.example.ilms.repository.CourseRepository;
import com.example.ilms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassCourseService {

    private final ClassCourseRepository classCourseRepository;
    private final ClassRepository classRepository;
    private final ClassMemberRepository classMemberRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    // ===================== GET: môn học của một lớp =====================

    @Transactional(readOnly = true)
    public PageResponse<ClassCourseResponseDTO> getCoursesByClass(
            UUID classId, String status, String semester,
            int page, int size, User currentUser) {

        ClassEntity cls = findActiveClass(classId);

        if (!isAdmin(currentUser)) {
            boolean isActiveMember = classMemberRepository
                    .findByClassEntity_IdAndUser_Id(cls.getId(), currentUser.getId())
                    .map(ClassMember::getStatus)
                    .map("ACTIVE"::equals)
                    .orElse(false);
            if (!isActiveMember) {
                throw new AppException(ErrorCode.CLASS_ACCESS_DENIED);
            }
        }

        int safePage = Math.max(page, 0);
        int safeSize = size < 1 ? 10 : Math.min(size, 100);

        Page<ClassCourse> result = classCourseRepository.findByClass(
                classId,
                normalize(status) == null ? null : normalize(status).toUpperCase(),
                normalize(semester),
                PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "assignedAt")));

        return PageResponse.<ClassCourseResponseDTO>builder()
                .items(result.getContent().stream().map(this::toDTO).toList())
                .pageNumber(safePage)
                .pageSize(safeSize)
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .isLast(result.isLast())
                .build();
    }

    // ===================== POST: gán một môn =====================

    @Transactional
    public ClassCourseResponseDTO assignCourse(UUID classId, AssignCourseToClassRequestDTO request) {
        ClassEntity cls = findActiveClass(classId);
        validateDateRange(request.getStartDate(), request.getEndDate());

        User teacher = null;
        if (request.getTeacherId() != null) {
            teacher = userRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        }

        ClassCourse saved = createAssignment(
                cls, request.getCourseId(), teacher,
                request.getSemester().trim(), request.getAcademicYear().trim(),
                request.getStartDate(), request.getEndDate());

        return toDTO(saved);
    }

//    // ===================== POST: gán nhiều môn =====================
//
//    @Transactional
//    public BatchOperationResponseDTO batchAssignCourses(UUID classId, BatchAssignCoursesRequestDTO request) {
//        ClassEntity cls = findActiveClass(classId);
//
//        // Bỏ trùng và phần tử null trong danh sách gửi lên
//        List<UUID> courseIds = request.getCourseIds().stream()
//                .filter(Objects::nonNull)
//                .distinct()
//                .toList();
//
//        List<UUID> successIds = new ArrayList<>();
//        List<BatchOperationResponseDTO.FailedItem> failedItems = new ArrayList<>();
//
//        for (UUID courseId : courseIds) {
//            try {
//                createAssignment(cls, courseId, null,
//                        request.getSemester().trim(), request.getAcademicYear().trim(),
//                        null, null);
//                successIds.add(courseId);
//            } catch (AppException ex) {
//                // Lỗi của từng môn không làm hỏng cả lô
//                failedItems.add(new BatchOperationResponseDTO.FailedItem(
//                        courseId, ex.getErrorCode().getMessage()));
//            }
//        }
//
//        return BatchOperationResponseDTO.builder()
//                .totalRequested(courseIds.size())
//                .successCount(successIds.size())
//                .failedCount(failedItems.size())
//                .successIds(successIds)
//                .failedItems(failedItems)
//                .build();
//    }

    // ===================== helpers =====================

    private ClassCourse createAssignment(
            ClassEntity cls, UUID courseId, User teacher,
            String semester, String academicYear,
            LocalDate startDate, LocalDate endDate) {

        Course course = courseRepository.findById(courseId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (classCourseRepository.existsByClassEntity_IdAndCourse_Id(cls.getId(), course.getId())) {
            throw new AppException(ErrorCode.COURSE_ALREADY_ASSIGNED);
        }

        ClassCourse classCourse = ClassCourse.builder()
                .classEntity(cls)
                .course(course)
                .teacher(teacher)
                .semester(semester)
                .academicYear(academicYear)
                .startDate(startDate)
                .endDate(endDate)
                .assignedAt(LocalDateTime.now())
                .status("ACTIVE")
                .build();

        return classCourseRepository.save(classCourse);
    }

    private ClassEntity findActiveClass(UUID classId) {
        return classRepository.findById(classId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
    }

    private void validateDateRange(LocalDate start, LocalDate end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }
    }

    private boolean isAdmin(User user) {
        return user.getRole() != null && "ROLE_ADMIN".equals(user.getRole().getCode());
    }

    private String normalize(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private String fullName(User u) {
        if (u == null) return null;
        StringBuilder sb = new StringBuilder();
        if (u.getLastName() != null) sb.append(u.getLastName()).append(' ');
        if (u.getMiddleName() != null && !u.getMiddleName().isBlank()) sb.append(u.getMiddleName()).append(' ');
        if (u.getFirstName() != null) sb.append(u.getFirstName());
        return sb.toString().trim();
    }

    private ClassCourseResponseDTO toDTO(ClassCourse cc) {
        User teacher = cc.getTeacher();
        return ClassCourseResponseDTO.builder()
                .classId(cc.getClassEntity().getId())
                .className(cc.getClassEntity().getName())
                .courseId(cc.getCourse().getId())
                .courseCode(cc.getCourse().getCourseCode())
                .courseTitle(cc.getCourse().getName())
                .teacherId(teacher != null ? teacher.getId() : null)
                .teacherName(fullName(teacher))
                .semester(cc.getSemester())
                .academicYear(cc.getAcademicYear())
                .startDate(cc.getStartDate())
                .endDate(cc.getEndDate())
                .status(cc.getStatus())
                .assignedAt(cc.getAssignedAt())
                .build();
    }
}