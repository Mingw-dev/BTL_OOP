-- Migration: create_exam_questions_table
CREATE TABLE exam_questions (
                                id BINARY(16) NOT NULL,
                                exam_id BINARY(16) NOT NULL,                -- Khóa ngoại trỏ sang exams
                                question_id BINARY(16) NOT NULL,            -- Khóa ngoại trỏ sang questions
                                score DECIMAL(5, 2) NOT NULL DEFAULT 1.00,  -- Điểm của câu hỏi này trong đề thi
                                order_index INT NOT NULL DEFAULT 1,         -- Thứ tự câu hỏi trong đề (Câu 1, Câu 2...)

                                added_method VARCHAR(30) NOT NULL DEFAULT 'MANUAL',
    -- 'MANUAL': Giáo viên tự duyệt chọn vào đề
    -- 'AI_AUTO_GEN': AI đọc tài liệu/concept gen trực tiếp vào đề
    -- 'AI_AUTO_PICK': AI lọc ngẫu nhiên từ kho câu hỏi theo ma trận độ khó

                                created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),

                                CONSTRAINT pk_exam_questions PRIMARY KEY (id),

    -- Đảm bảo 1 đề thi không gán trùng 1 câu hỏi 2 lần
                                CONSTRAINT uq_exam_question UNIQUE (exam_id, question_id),

    -- Ràng buộc khóa ngoại
                                CONSTRAINT fk_exam_questions_exam FOREIGN KEY (exam_id)
                                    REFERENCES exams (id)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,

                                CONSTRAINT fk_exam_questions_question FOREIGN KEY (question_id)
                                    REFERENCES questions (id)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Index phục vụ query lấy danh sách câu hỏi theo thứ tự đề thi
CREATE INDEX idx_exam_questions_exam_order ON exam_questions (exam_id, order_index ASC);
CREATE INDEX idx_exam_questions_question ON exam_questions (question_id);