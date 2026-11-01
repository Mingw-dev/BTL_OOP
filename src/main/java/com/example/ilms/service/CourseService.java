package com.example.ilms.service;

import java.time.LocalDateTime;
import com.example.ilms.dto.common.PageResponse;
import com.example.ilms.dto.course.CourseCreateRequestDTO;
import com.example.ilms.dto.course.CourseResponseDTO;
import com.example.ilms.dto.course.UserCourseResponseDTO;
import com.example.ilms.entity.Course;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.CourseRepository;
import com.example.ilms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public void createCourse(CourseCreateRequestDTO request, User owner) {

        Course course = modelMapper.map(request, Course.class);

        String randomCode = UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        course.setCourseCode(randomCode);

        course.setOwner(owner);

        courseRepository.save(course);
    }

    public PageResponse<CourseResponseDTO> getCourses(
            String search,
            Boolean status,
            boolean isArchived,
            int page,
            int size,
            User currentUser) {

        Boolean effectiveStatus = isArchived ? Boolean.FALSE : status;

        boolean isAdmin = currentUser.getRole() != null
                && "ADMIN".equalsIgnoreCase(currentUser.getRole().getCode());

        String visibility = isAdmin ? null : "PUBLIC";

        int limit = size;
        int offset = page * size;

        List<Course> courses = courseRepository.findCoursesWithFilters(
                search, visibility, effectiveStatus, limit, offset);

        long totalElements = courseRepository.countCoursesWithFilters(
                search, visibility, effectiveStatus);

        if (search != null && !search.isBlank() && courses.isEmpty()) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean isLast = (page + 1) >= totalPages;

        List<CourseResponseDTO> items = courses.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return PageResponse.<CourseResponseDTO>builder()
                .items(items)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .isLast(isLast)
                .build();
    }

    public CourseResponseDTO getCourseById(UUID id, User currentUser) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        boolean isAdmin = currentUser.getRole() != null
                && "ADMIN".equalsIgnoreCase(currentUser.getRole().getCode());

        // User thường: không được xem môn PRIVATE, cũng không được xem môn đã xóa mềm
        if (!isAdmin) {
            if (!"PUBLIC".equalsIgnoreCase(course.getVisibility()) || course.getDeletedAt() != null) {
                throw new AppException(ErrorCode.COURSE_NOT_FOUND);
            }
        }

        return toResponseDTO(course);
    }

    @Transactional
    public void softDeleteCourse(UUID id, User currentUser) {
        Course course = courseRepository.findById(id)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        boolean isAdmin = currentUser.getRole() != null
                && "ROLE_ADMIN".equals(currentUser.getRole().getCode());

        boolean isOwner = course.getOwner() != null
                && course.getOwner().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AppException(ErrorCode.COURSE_FORBIDDEN);
        }

        course.setDeletedAt(LocalDateTime.now());
        courseRepository.save(course);
    }

    @Transactional
    public CourseResponseDTO restoreCourse(UUID id, User currentUser) {
        boolean isAdmin = currentUser.getRole() != null
                && "ROLE_ADMIN".equals(currentUser.getRole().getCode());

        // Chỉ Admin được khôi phục. Kiểm tra quyền trước để người thường không dò được môn nào tồn tại
        if (!isAdmin) {
            throw new AppException(ErrorCode.COURSE_RESTORE_FORBIDDEN);
        }

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (course.getDeletedAt() == null) {
            throw new AppException(ErrorCode.COURSE_NOT_DELETED);
        }

        course.setDeletedAt(null);
        Course restoredCourse = courseRepository.save(course);

        return toResponseDTO(restoredCourse);
    }

    // Lấy danh sách (lớp + môn học) mà 1 user đang tham gia
    public PageResponse<UserCourseResponseDTO> getCoursesByUser(
            UUID userId,
            int page,
            int size,
            User currentUser) {

        // Admin xem được của bất kỳ ai; người dùng thường chỉ được xem của chính mình
        boolean isAdmin = currentUser.getRole() != null
                && "ROLE_ADMIN".equals(currentUser.getRole().getCode());
        boolean isSelf = currentUser.getId().equals(userId);

        // Kiểm tra quyền TRƯỚC khi kiểm tra tồn tại, để người thường không dò được userId nào có thật
        if (!isAdmin && !isSelf) {
            throw new AppException(ErrorCode.COURSE_ACCESS_DENIED);
        }

        if (!isSelf && !userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        // Chặn page âm / size không hợp lệ để không sinh OFFSET/LIMIT sai gây lỗi 500
        int safePage = Math.max(page, 0);
        int safeSize = (size < 1) ? 10 : Math.min(size, 100);

        int limit = safeSize;
        int offset = safePage * safeSize;

        String userIdStr = userId.toString();

        List<CourseRepository.UserCourseProjection> rows =
                courseRepository.findUserCourses(userIdStr, limit, offset);

        long totalElements = courseRepository.countUserCourses(userIdStr);

        int totalPages = (int) Math.ceil((double) totalElements / safeSize);
        boolean isLast = (safePage + 1) >= totalPages;

        List<UserCourseResponseDTO> items = rows.stream()
                .map(this::toUserCourseResponseDTO)
                .collect(Collectors.toList());

        return PageResponse.<UserCourseResponseDTO>builder()
                .items(items)
                .pageNumber(safePage)
                .pageSize(safeSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .isLast(isLast)
                .build();
    }

    private UserCourseResponseDTO toUserCourseResponseDTO(CourseRepository.UserCourseProjection row) {
        return UserCourseResponseDTO.builder()
                .classId(parseHexUuid(row.getClassId()))
                .className(row.getClassName())
                .classCode(row.getClassCode())
                .courseId(parseHexUuid(row.getCourseId()))
                .name(row.getName())
                .description(row.getDescription())
                .ownerId(parseHexUuid(row.getOwnerId()))
                .registeredAt(parseDateTime(row.getRegisteredAt()))
                .build();
    }

    // HEX() trả về chuỗi 32 ký tự liền không dấu gạch ngang,
    // phải ghép lại thành định dạng 8-4-4-4-12 thì UUID.fromString mới đọc được.
    private UUID parseHexUuid(String hex) {
        if (hex == null || hex.isBlank()) {
            return null;
        }
        String h = hex.trim();
        String formatted = h.substring(0, 8) + "-" + h.substring(8, 12) + "-"
                + h.substring(12, 16) + "-" + h.substring(16, 20) + "-" + h.substring(20);
        return UUID.fromString(formatted);
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(value.trim().replace(' ', 'T'));
    }

    private CourseResponseDTO toResponseDTO(Course course) {
        CourseResponseDTO dto = modelMapper.map(course, CourseResponseDTO.class);
        if (course.getOwner() != null) {
            dto.setOwnerId(course.getOwner().getId());
        }
        return dto;
    }
}