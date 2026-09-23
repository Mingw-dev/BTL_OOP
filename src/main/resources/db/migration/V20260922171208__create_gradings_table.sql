-- Migration: create_gradings_table
CREATE TABLE gradings (
                          id BINARY(16) NOT NULL,
                          submission_id BINARY(16) NOT NULL,          -- "Có kết quả" (Mỗi bài nộp có bài chấm)
                          grader_id BINARY(16) NOT NULL,              -- "Chấm bởi" (giảng viên / người chấm)
                          score DECIMAL(5, 2) NOT NULL,               -- Điểm số chính thức
                          feedback TEXT NULL,                         -- Nhận xét của người chấm
                          graded_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), -- Thời gian chấm

    -- Các thuộc tính dẫn xuất / thống kê thêm (nét đứt)
                          correct_answers_count INT NULL,             -- Số câu đúng (nếu là bài trắc nghiệm)
                          scaled_score DECIMAL(5, 2) NULL,            -- Điểm số dẫn xuất (điểm hệ 4, điểm thang 100...)

                          created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                          updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

                          CONSTRAINT pk_gradings PRIMARY KEY (id),

                          CONSTRAINT uq_gradings_submission UNIQUE (submission_id),

                          CONSTRAINT fk_gradings_submission FOREIGN KEY (submission_id)
                              REFERENCES submissions (id)
                              ON DELETE CASCADE
                              ON UPDATE CASCADE,

                          CONSTRAINT fk_gradings_grader FOREIGN KEY (grader_id)
                              REFERENCES users (id)
                              ON DELETE RESTRICT
                              ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_gradings_submission ON gradings (submission_id);
CREATE INDEX idx_gradings_grader ON gradings (grader_id);