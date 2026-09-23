-- Migration: create_lessons_table
-- 2. Bảng Bài học (Lessons)
CREATE TABLE lessons (
                         id BINARY(16) NOT NULL,
                         chapter_id BINARY(16) NOT NULL,             -- Thuộc về chương nào
                         title VARCHAR(255) NOT NULL,                -- Tiêu đề bài học
                         content TEXT NULL,                          -- Nội dung bài học (HTML/Markdown)
                         lesson_type VARCHAR(50) DEFAULT 'DOCUMENT',    -- Thể loại: VIDEO, TEXT, QUIZ, DOCUMENT...
                         video_url VARCHAR(500) NULL,                -- Đường dẫn video (nếu có)
                         duration_minutes INT DEFAULT 0,             -- Thời lượng ước tính (phút)
                         order_index INT NOT NULL DEFAULT 1,         -- Thứ tự bài học trong chương
                         is_published BOOLEAN NOT NULL DEFAULT TRUE, -- Trạng thái xuất bản
#                          is_free_preview BOOLEAN NOT NULL DEFAULT FALSE, -- Cho phép học thử miễn phí hay không
                         created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                         updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

                         CONSTRAINT pk_lessons PRIMARY KEY (id),

                         CONSTRAINT fk_lessons_chapter FOREIGN KEY (chapter_id)
                             REFERENCES chapters (id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_lessons_chapter ON lessons (chapter_id);