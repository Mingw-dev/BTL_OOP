package com.example.ilms.repository;

import com.example.ilms.entity.LessonSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface LessonSessionRepository extends JpaRepository<LessonSession, UUID> {
    // Lấy danh sách các buổi học của 1 lớp, sắp xếp theo thời gian tăng dần
    List<LessonSession> findByClassEntityIdOrderBySessionDateAscStartTimeAsc(UUID classId);

    // 1. Kiểm tra trùng phòng học trong khoảng thời gian từ startTime đến endTime
    @Query("""
        SELECT COUNT(s) > 0
        FROM LessonSession s
        WHERE s.room.id = :roomId
          AND s.sessionDate = :sessionDate
          AND s.startTime < :endTime
          AND s.endTime > :startTime
        """)
    boolean existsRoomConflict(
            @Param("roomId") UUID roomId,
            @Param("sessionDate") LocalDate sessionDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}
