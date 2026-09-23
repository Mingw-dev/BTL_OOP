CREATE TABLE submissions (
                             id BINARY(16) NOT NULL,
                             assignment_id BINARY(16) NULL,              -- Thuộc bài tập nào (hoặc exam_id nếu là bài thi)
                             user_id BINARY(16) NOT NULL,                -- "Làm bài bởi" (người nộp bài)
                             attempt_number INT NOT NULL DEFAULT 1,      -- Lần nộp (lần 1, lần 2...)
                             max_attempts INT DEFAULT 1,                 -- Số lần nộp tối đa
                             status VARCHAR(50) NOT NULL DEFAULT 'SUBMITTED', -- Trạng thái: DRAFT, SUBMITTED, GRADED, LATE
#                              file_url VARCHAR(500) NULL,                 -- File bài làm đính kèm (nếu nộp file)
                             submission_text LONGTEXT NULL,              -- Nội dung làm bài trực tiếp (nếu có)
                             submitted_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),

                             CONSTRAINT pk_submissions PRIMARY KEY (id),

    -- Liên kết với người làm bài
                             CONSTRAINT fk_submissions_user FOREIGN KEY (user_id)
                                 REFERENCES users (id)
                                 ON DELETE CASCADE
                                 ON UPDATE CASCADE,

    -- Liên kết tới bảng bài tập (đã tạo ở bước trước)
                             CONSTRAINT fk_submissions_assignment FOREIGN KEY (assignment_id)
                                 REFERENCES assignments (id)
                                 ON DELETE CASCADE
                                 ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_submissions_user_assignment ON submissions (user_id, assignment_id);