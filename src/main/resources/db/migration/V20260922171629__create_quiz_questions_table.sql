-- Migration: create_quiz_questions_table
-- 2. Bảng Câu hỏi trắc nghiệm (Quiz Questions)
CREATE TABLE quiz_questions (
                                id BINARY(16) NOT NULL,
                                quiz_id BINARY(16) NOT NULL,                -- Thuộc bài quiz nào
                                question_text TEXT NOT NULL,                -- Nội dung câu hỏi (chữ hoặc HTML)
                                question_type VARCHAR(30) DEFAULT 'SINGLE_CHOICE', -- SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE
                                score DECIMAL(5, 2) NOT NULL DEFAULT 1.00,  -- Điểm số của câu hỏi
                                explanation TEXT NULL,                      -- Lời giải thích/giải pháp chi tiết khi làm xong
                                order_index INT NOT NULL DEFAULT 1,         -- Thứ tự câu hỏi trong đề
                                created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),

                                CONSTRAINT pk_quiz_questions PRIMARY KEY (id),

                                CONSTRAINT fk_quiz_questions_quiz FOREIGN KEY (quiz_id)
                                    REFERENCES quizzes (id)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_quiz_questions_quiz ON quiz_questions (quiz_id, order_index ASC);

