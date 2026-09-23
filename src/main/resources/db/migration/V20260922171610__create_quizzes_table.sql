-- Migration: create_quizzes_table
CREATE TABLE quizzes (
                         id BINARY(16) NOT NULL,
                         class_id BINARY(16) NULL,                   -- Thuộc lớp học nào (nếu giao cho lớp)
                         lesson_id BINARY(16) NULL,                  -- Thuộc bài học nào (nếu gắn vào bài học)
                         course_id BINARY(16) NULL,                  -- Thuộc khóa học nào
                         title VARCHAR(255) NOT NULL,                -- Tiêu đề bài trắc nghiệm
                         description TEXT NULL,                      -- Hướng dẫn làm bài
                         duration_minutes INT NOT NULL DEFAULT 15,   -- Thời gian làm bài (phút), ví dụ: 15, 45, 60
                         passing_score DECIMAL(5, 2) DEFAULT 5.00,   -- Điểm đạt (pass)
                         max_attempts INT DEFAULT 1,                 -- Số lần làm tối đa (NULL = không giới hạn)
                         shuffle_questions BOOLEAN DEFAULT TRUE,     -- Đảo ngẫu nhiên câu hỏi
                         shuffle_options BOOLEAN DEFAULT TRUE,       -- Đảo ngẫu nhiên đáp án
                         is_published BOOLEAN DEFAULT FALSE,         -- Trạng thái công bố
                         created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                         updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

                         CONSTRAINT pk_quizzes PRIMARY KEY (id),

                         CONSTRAINT fk_quizzes_class FOREIGN KEY (class_id)
                             REFERENCES classes (id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE,

                         CONSTRAINT fk_quizzes_lesson FOREIGN KEY (lesson_id)
                             REFERENCES lessons (id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE,

                         CONSTRAINT fk_quizzes_course FOREIGN KEY (course_id)
                             REFERENCES courses (id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_quizzes_lesson ON quizzes (lesson_id);
CREATE INDEX idx_quizzes_class ON quizzes (class_id);


