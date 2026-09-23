-- Migration: create_chapters_table
-- 1. Bảng Chương học (Chapters / Modules)
CREATE TABLE chapters (
                          id BINARY(16) NOT NULL,
                          course_id BINARY(16) NOT NULL,              -- Thuộc về khóa học nào
                          title VARCHAR(255) NOT NULL,                -- Tiêu đề chương
                          description TEXT NULL,                      -- Mô tả chương
                          order_index INT NOT NULL DEFAULT 1,         -- Thứ tự hiển thị chương trong khóa học
                          is_published BOOLEAN NOT NULL DEFAULT TRUE, -- Trạng thái xuất bản
                          created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                          updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

                          CONSTRAINT pk_chapters PRIMARY KEY (id),

                          CONSTRAINT fk_chapters_course FOREIGN KEY (course_id)
                              REFERENCES courses (id)
                              ON DELETE CASCADE
                              ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_chapters_course ON chapters (course_id);


