-- Migration: create_exams_table
CREATE TABLE exams (
                       id BINARY(16) NOT NULL,
                       class_id BINARY(16) NOT NULL,              -- Kỳ thi của lớp nào
                       course_id BINARY(16) NULL,                 -- Thuộc môn học/khóa học nào
                       title VARCHAR(255) NOT NULL,               -- Tên bài kiểm tra
                       description TEXT NULL,                     -- Hướng dẫn làm bài
                       exam_type VARCHAR(50) DEFAULT 'MIDTERM',   -- Loại: QUIZ, MIDTERM, FINAL
                       duration_minutes INT NOT NULL,             -- Thời gian làm bài (phút)
                       total_score DECIMAL(5, 2) DEFAULT 10.00,   -- Tổng điểm
                       start_time DATETIME(6) NOT NULL,           -- Thời gian mở đề thi
                       end_time DATETIME(6) NOT NULL,             -- Thời gian đóng đề thi
                       is_published BOOLEAN NOT NULL DEFAULT FALSE, -- Trạng thái công bố đề
                       created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

                       CONSTRAINT pk_exams PRIMARY KEY (id),

                       CONSTRAINT fk_exams_class FOREIGN KEY (class_id)
                           REFERENCES classes (id)
                           ON DELETE CASCADE
                           ON UPDATE CASCADE,

                       CONSTRAINT fk_exams_course FOREIGN KEY (course_id)
                           REFERENCES courses (id)
                           ON DELETE SET NULL
                           ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_exams_class_time ON exams (class_id, start_time, end_time);