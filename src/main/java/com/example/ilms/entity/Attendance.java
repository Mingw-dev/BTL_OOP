package com.example.ilms.entity;

import com.example.ilms.type.AttendanceStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendances")
@Getter @Setter
@NoArgsConstructor @Builder @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class) // dùng để tự động theo dõi và cập nhật các thông tin như thời gian tạo và thời gian chỉnh sửa cuối cùng của một Entity.

public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    // Khóa ngoại liên kết với bảng lesson_sessions (cột session_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private LessonSession lessonSession;

    // Khóa ngoại liên kết với bảng users (cột user_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User student; // Đổi từ student thành user cho khớp SQL

    // Trạng thái: 'PRESENT', 'ABSENT', 'LATE', 'EXCUSED'
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    // Thời gian thực tế quẹt thẻ / điểm danh
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(columnDefinition = "TEXT")
    private String note; // Ghi chú

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
