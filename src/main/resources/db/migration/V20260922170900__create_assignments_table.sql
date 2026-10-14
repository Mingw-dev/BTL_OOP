-- Migration: create_assignments_table
CREATE TABLE assignments (
                             id BINARY(16) NOT NULL,
                             lesson_id BINARY(16) NULL,                 -- Gắn với bài học cụ thể (nếu có, có thể NULL)
                             title VARCHAR(255) NOT NULL,               -- Tiêu đề bài tập
                             description TEXT NULL,                     -- Yêu cầu chi tiết
                             max_score DECIMAL(5, 2) DEFAULT 10.00,     -- Thang điểm tối đa
                             due_date DATETIME(6) NULL,                 -- Hạn nộp bài
                             allow_late_submission BOOLEAN DEFAULT TRUE,-- Cho phép nộp muộn hay không
                             file_attachment_url VARCHAR(500) NULL,     -- File đính kèm đề bài (PDF, DOCX, ZIP)
                             created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                             updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

                             CONSTRAINT pk_assignments PRIMARY KEY (id),

                             CONSTRAINT fk_assignments_lesson FOREIGN KEY (lesson_id)
                                 REFERENCES lessons (id)
                                 ON DELETE SET NULL
                                 ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_assignments_class_due ON assignments (id, due_date);