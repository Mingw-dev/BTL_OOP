-- Migration: create_quiz_options_table
CREATE TABLE quiz_options (
                              id BINARY(16) NOT NULL,
                              question_id BINARY(16) NOT NULL,            -- Thuộc câu hỏi nào
                              option_text TEXT NOT NULL,                  -- Nội dung đáp án (A, B, C, D...)
                              is_correct BOOLEAN NOT NULL DEFAULT FALSE,  -- True: đáp án đúng, False: đáp án sai
                              order_index INT NOT NULL DEFAULT 1,         -- Thứ tự hiển thị lựa chọn

                              CONSTRAINT pk_quiz_options PRIMARY KEY (id),

                              CONSTRAINT fk_quiz_options_question FOREIGN KEY (question_id)
                                  REFERENCES quiz_questions (id)
                                  ON DELETE CASCADE
                                  ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_quiz_options_question ON quiz_options (question_id);