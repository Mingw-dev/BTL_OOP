-- Migration: create_quiz_answers_table
CREATE TABLE quiz_answers (
                              id BINARY(16) NOT NULL,
                              submission_id BINARY(16) NOT NULL,          -- Thuộc lần nộp bài nào
                              question_id BINARY(16) NOT NULL,            -- Câu hỏi nào
                              selected_option_id BINARY(16) NULL,         -- Học viên chọn phương án nào (quiz_options)
                              answer_text TEXT NULL,                      -- Dành cho trường hợp câu hỏi tự luận / điền từ
                              is_correct BOOLEAN DEFAULT FALSE,           -- Đánh dấu câu này làm đúng hay sai
                              earned_score DECIMAL(5, 2) DEFAULT 0.00,    -- Điểm đạt được của câu hỏi này

                              CONSTRAINT pk_quiz_answers PRIMARY KEY (id),

                              CONSTRAINT uq_submission_question UNIQUE (submission_id, question_id),

                              CONSTRAINT fk_quiz_answers_submission FOREIGN KEY (submission_id)
                                  REFERENCES submissions (id)
                                  ON DELETE CASCADE
                                  ON UPDATE CASCADE,

                              CONSTRAINT fk_quiz_answers_question FOREIGN KEY (question_id)
                                  REFERENCES quiz_questions (id)
                                  ON DELETE CASCADE
                                  ON UPDATE CASCADE,

                              CONSTRAINT fk_quiz_answers_option FOREIGN KEY (selected_option_id)
                                  REFERENCES quiz_options (id)
                                  ON DELETE SET NULL
                                  ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_quiz_answers_submission ON quiz_answers (submission_id);