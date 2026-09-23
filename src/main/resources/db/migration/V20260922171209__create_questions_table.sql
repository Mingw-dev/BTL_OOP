-- Migration: create_questions_table
CREATE TABLE questions (
                           id BINARY(16) NOT NULL,
                           course_id BINARY(16) NULL,                  -- Khóa ngoại trỏ sang courses
                           chapter_id BINARY(16) NULL,                 -- Khóa ngoại trỏ sang chapters
                           created_by BINARY(16) NULL,                 -- Khóa ngoại trỏ sang users (người tạo / GV)
                           question_text LONGTEXT NOT NULL,
                           question_type VARCHAR(30) NOT NULL DEFAULT 'SINGLE_CHOICE', -- SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE
                           difficulty VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',           -- EASY, MEDIUM, HARD
                           explanation TEXT NULL,                      -- Lời giải thích chi tiết
                           source_type VARCHAR(30) NOT NULL DEFAULT 'MANUAL',          -- 'MANUAL', 'AI_GENERATED'
                           ai_prompt_context TEXT NULL,                -- Context/prompt mà AI dùng để gen câu hỏi
                           is_verified_by_teacher BOOLEAN NOT NULL DEFAULT FALSE,      -- Giáo viên đã duyệt câu AI tạo chưa
                           created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                           updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

                           CONSTRAINT pk_questions PRIMARY KEY (id),

    -- Các ràng buộc toàn vẹn dữ liệu
                           CONSTRAINT fk_questions_course FOREIGN KEY (course_id)
                               REFERENCES courses (id)
                               ON DELETE CASCADE
                               ON UPDATE CASCADE,

                           CONSTRAINT fk_questions_chapter FOREIGN KEY (chapter_id)
                               REFERENCES chapters (id)
                               ON DELETE SET NULL
                               ON UPDATE CASCADE,

                           CONSTRAINT fk_questions_creator FOREIGN KEY (created_by)
                               REFERENCES users (id)
                               ON DELETE SET NULL
                               ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Các Index phục vụ lọc và tìm kiếm câu hỏi
CREATE INDEX idx_questions_course_difficulty ON questions (course_id, difficulty);
CREATE INDEX idx_questions_chapter ON questions (chapter_id);
CREATE INDEX idx_questions_source_verified ON questions (source_type, is_verified_by_teacher);