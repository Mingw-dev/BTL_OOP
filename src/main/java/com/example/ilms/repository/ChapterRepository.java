package com.example.ilms.repository;

import com.example.ilms.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChapterRepository extends JpaRepository<Chapter, UUID> {

    // Dùng để tự sinh order_index tiếp theo khi tạo chương mới mà không truyền orderIndex
    @Query("SELECT COALESCE(MAX(c.orderIndex), -1) FROM Chapter c " +
            "WHERE c.course.id = :courseId AND c.deletedAt IS NULL")
    int findMaxOrderIndexByCourseId(@Param("courseId") UUID courseId);

    // Danh sách chương của một khóa học, sắp theo order_index.
    // includeDeleted = false thì loại bỏ các chương đã xóa mềm.
    @Query("SELECT c FROM Chapter c " +
            "WHERE c.course.id = :courseId " +
            "AND (:includeDeleted = true OR c.deletedAt IS NULL) " +
            "ORDER BY c.orderIndex ASC, c.createdAt ASC")
    List<Chapter> findByCourseId(@Param("courseId") UUID courseId,
                                 @Param("includeDeleted") boolean includeDeleted);

    // Lấy chương kèm môn học (và owner) để kiểm tra quyền mà không bị lazy-load nhiều lần
    @Query("SELECT c FROM Chapter c " +
            "JOIN FETCH c.course co " +
            "LEFT JOIN FETCH co.owner " +
            "WHERE c.id = :chapterId")
    Optional<Chapter> findByIdWithCourse(@Param("chapterId") UUID chapterId);

    // Đếm số bài học của từng chương trong một khóa học (1 query cho cả danh sách, tránh N+1).
    // Bảng lessons chưa có entity nên dùng native query, theo cùng kiểu UNHEX/HEX như CourseRepository.
    @Query(value = "SELECT HEX(l.chapter_id) AS chapterId, COUNT(*) AS total " +
            "FROM lessons l " +
            "JOIN chapters c ON c.id = l.chapter_id " +
            "WHERE c.course_id = UNHEX(REPLACE(:courseId, '-', '')) " +
            "GROUP BY l.chapter_id",
            nativeQuery = true)
    List<LessonCountProjection> countLessonsByCourseId(@Param("courseId") String courseId);

    @Query(value = "SELECT COUNT(*) FROM lessons l " +
            "WHERE l.chapter_id = UNHEX(REPLACE(:chapterId, '-', ''))",
            nativeQuery = true)
    long countLessonsByChapterId(@Param("chapterId") String chapterId);

    interface LessonCountProjection {
        String getChapterId();
        Long getTotal();
    }
}