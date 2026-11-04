package com.example.ilms.service;

import com.example.ilms.dto.chapter.ChapterCreateRequestDTO;
import com.example.ilms.dto.chapter.ChapterDetailResponseDTO;
import com.example.ilms.dto.chapter.ChapterResponseDTO;
import com.example.ilms.dto.chapter.ChapterSummaryResponseDTO;
import com.example.ilms.dto.chapter.ChapterUpdateRequestDTO;
import com.example.ilms.entity.Chapter;
import com.example.ilms.entity.Course;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.ChapterRepository;
import com.example.ilms.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public ChapterResponseDTO createChapter(UUID courseId, ChapterCreateRequestDTO request, User currentUser) {
        // Chặn sớm theo vai trò (trước khi tra DB) để người không có quyền không dò được môn học nào tồn tại
        assertAdminOrTeacher(currentUser);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // Admin, hoặc giảng viên đồng thời là chủ môn học, mới được tạo chương
        if (!canManage(course, currentUser)) {
            throw new AppException(ErrorCode.CHAPTER_FORBIDDEN);
        }

        // Không truyền orderIndex thì xếp vào cuối danh sách chương hiện có
        // (môn chưa có chương nào: findMaxOrderIndexByCourseId trả -1 nên chương đầu có order_index = 0)
        Integer orderIndex = request.getOrderIndex();
        if (orderIndex == null) {
            orderIndex = chapterRepository.findMaxOrderIndexByCourseId(courseId) + 1;
        }

        Chapter chapter = Chapter.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .orderIndex(orderIndex)
                .course(course)
                .build();

        chapter = chapterRepository.saveAndFlush(chapter);

        return ChapterResponseDTO.builder()
                .id(chapter.getId())
                .courseId(course.getId())
                .title(chapter.getTitle())
                .description(chapter.getDescription())
                .orderIndex(chapter.getOrderIndex())
                .totalLessons(0)
                .createdAt(chapter.getCreatedAt())
                .updatedAt(chapter.getUpdatedAt())
                .deletedAt(chapter.getDeletedAt())
                .build();
    }

    // GET /api/v1/courses/{courseId}/chapters
    @Transactional(readOnly = true)
    public List<ChapterSummaryResponseDTO> getChaptersByCourse(
            UUID courseId, boolean includeDeleted, User currentUser) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        boolean canManage = canManage(course, currentUser);
        assertCanView(course, canManage);

        // Chỉ Admin/chủ môn học mới được xem cả chương đã xóa mềm
        boolean withDeleted = includeDeleted && canManage;

        List<Chapter> chapters = chapterRepository.findByCourseId(courseId, withDeleted);

        // Một query đếm bài học cho toàn bộ chương của môn
        Map<UUID, Long> lessonCounts = new HashMap<>();
        for (ChapterRepository.LessonCountProjection row
                : chapterRepository.countLessonsByCourseId(courseId.toString())) {
            lessonCounts.put(parseHexUuid(row.getChapterId()), row.getTotal());
        }

        return chapters.stream()
                .map(chapter -> ChapterSummaryResponseDTO.builder()
                        .id(chapter.getId())
                        .courseId(courseId)
                        .title(chapter.getTitle())
                        .description(chapter.getDescription())
                        .orderIndex(chapter.getOrderIndex())
                        .totalLessons(lessonCounts.getOrDefault(chapter.getId(), 0L).intValue())
                        .createdAt(chapter.getCreatedAt())
                        .deletedAt(chapter.getDeletedAt())
                        .build())
                .toList();
    }

    // GET /api/v1/chapters/{chapterId}
    @Transactional(readOnly = true)
    public ChapterDetailResponseDTO getChapterById(UUID chapterId, User currentUser) {
        Chapter chapter = chapterRepository.findByIdWithCourse(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        Course course = chapter.getCourse();

        boolean canManage = canManage(course, currentUser);
        assertCanView(course, canManage);

        // Người thường không được thấy chương đã xóa mềm
        if (chapter.getDeletedAt() != null && !canManage) {
            throw new AppException(ErrorCode.CHAPTER_NOT_FOUND);
        }

        return toDetailDTO(chapter, course);
    }

    // PUT /api/v1/chapters/{chapterId}
    @Transactional
    public ChapterDetailResponseDTO updateChapter(
            UUID chapterId, ChapterUpdateRequestDTO request, User currentUser) {

        // Chặn sớm theo vai trò (trước khi tra DB) để người không có quyền không dò được chương nào tồn tại
        assertAdminOrTeacher(currentUser);

        Chapter chapter = chapterRepository.findByIdWithCourse(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        Course course = chapter.getCourse();

        // Còn phải là Admin hoặc chủ của môn học này
        if (!canManage(course, currentUser)) {
            throw new AppException(ErrorCode.CHAPTER_FORBIDDEN);
        }

        // Chương đã xóa mềm thì không cho sửa
        if (chapter.getDeletedAt() != null) {
            throw new AppException(ErrorCode.CHAPTER_NOT_FOUND);
        }

        if (request.getTitle() != null) {
            if (request.getTitle().isBlank()) {
                throw new AppException(ErrorCode.INVALID_CHAPTER_DATA);
            }
            chapter.setTitle(request.getTitle().trim());
        }

        if (request.getDescription() != null) {
            chapter.setDescription(request.getDescription());
        }

        // saveAndFlush để updatedAt (@UpdateTimestamp) được cập nhật trước khi trả về
        chapter = chapterRepository.saveAndFlush(chapter);

        return toDetailDTO(chapter, course);
    }

    private ChapterDetailResponseDTO toDetailDTO(Chapter chapter, Course course) {
        long totalLessons = chapterRepository.countLessonsByChapterId(chapter.getId().toString());

        return ChapterDetailResponseDTO.builder()
                .id(chapter.getId())
                .courseId(course.getId())
                .title(chapter.getTitle())
                .description(chapter.getDescription())
                .orderIndex(chapter.getOrderIndex())
                .totalLessons((int) totalLessons)
                .createdAt(chapter.getCreatedAt())
                .updatedAt(chapter.getUpdatedAt())
                .deletedAt(chapter.getDeletedAt())
                .build();
    }

    // Chỉ Admin hoặc Giảng viên mới được tạo/sửa chương. Vai trò khác (ví dụ ROLE_USER) bị từ chối ngay.
    private void assertAdminOrTeacher(User user) {
        String role = roleName(user);
        if (!"ADMIN".equals(role) && !"TEACHER".equals(role)) {
            throw new AppException(ErrorCode.CHAPTER_FORBIDDEN);
        }
    }

    // Được quản lý chương khi:
    //  - là Admin, hoặc
    //  - là Giảng viên (ROLE_TEACHER) VÀ là chủ (owner) của môn học.
    // Chỉ là owner thôi thì chưa đủ: owner có thể bị gán cho user thường qua PATCH /courses/{id}/instructor.
    private boolean canManage(Course course, User user) {
        String role = roleName(user);
        if ("ADMIN".equals(role)) {
            return true;
        }

        boolean isOwner = course.getOwner() != null
                && course.getOwner().getId().equals(user.getId());

        return "TEACHER".equals(role) && isOwner;
    }

    // Lấy tên role đã bỏ tiền tố ROLE_ ("ROLE_ADMIN" và "ADMIN" đều ra "ADMIN"), không phân biệt hoa thường
    private String roleName(User user) {
        if (user.getRole() == null || user.getRole().getCode() == null) {
            return "";
        }
        String code = user.getRole().getCode().trim().toUpperCase();
        return code.startsWith("ROLE_") ? code.substring(5) : code;
    }

    private void assertCanView(Course course, boolean canManage) {
        if (canManage) {
            return;
        }
        if (!"PUBLIC".equalsIgnoreCase(course.getVisibility()) || course.getDeletedAt() != null) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }
    }

    // HEX() trả về 32 ký tự liền nhau, cần ghép lại dạng 8-4-4-4-12 để UUID.fromString đọc được
    private UUID parseHexUuid(String hex) {
        String h = hex.trim();
        return UUID.fromString(h.substring(0, 8) + "-" + h.substring(8, 12) + "-"
                + h.substring(12, 16) + "-" + h.substring(16, 20) + "-" + h.substring(20));
    }
}