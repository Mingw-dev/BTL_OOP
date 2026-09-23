-- Migration: create_materials_table
CREATE TABLE materials (
                           id BINARY(16) NOT NULL,
                           course_id BINARY(16) NULL,                 -- Tài liệu chung của khóa học
                           lesson_id BINARY(16) NULL,                 -- Gắn trực tiếp với một bài học cụ thể
                           uploaded_by BINARY(16) NOT NULL,           -- Người tải lên (users)
                           title VARCHAR(255) NOT NULL,               -- Tên tài liệu
                           description TEXT NULL,                     -- Mô tả ngắn
                           file_url VARCHAR(500) NOT NULL,            -- Đường dẫn file (S3, Cloudinary, Local storage)
                           file_type VARCHAR(50) NULL,                -- PDF, DOCX, ZIP, MP4,...
                           file_size_bytes BIGINT NULL,               -- Kích thước file theo bytes
                           is_downloadable BOOLEAN DEFAULT TRUE,      -- Cho phép học viên tải về hay chỉ xem online
                           created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                           updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

                           CONSTRAINT pk_materials PRIMARY KEY (id),

                           CONSTRAINT fk_materials_course FOREIGN KEY (course_id)
                               REFERENCES courses (id)
                               ON DELETE CASCADE
                               ON UPDATE CASCADE,

                           CONSTRAINT fk_materials_lesson FOREIGN KEY (lesson_id)
                               REFERENCES lessons (id)
                               ON DELETE CASCADE
                               ON UPDATE CASCADE,

                           CONSTRAINT fk_materials_uploader FOREIGN KEY (uploaded_by)
                               REFERENCES users (id)
                               ON DELETE RESTRICT
                               ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_materials_course ON materials (course_id);
CREATE INDEX idx_materials_lesson ON materials (lesson_id);