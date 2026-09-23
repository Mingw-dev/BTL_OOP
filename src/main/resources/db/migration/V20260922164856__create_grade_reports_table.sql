-- Migration: create_grade_reports_table
CREATE TABLE grade_reports (
                               id BINARY(16) NOT NULL,
                               class_member_id BINARY(16) NOT NULL,       -- Khóa ngoại trỏ sang class_members
                               attendance_score DECIMAL(4, 2) NULL,        -- Điểm chuyên cần
                               midterm_score DECIMAL(4, 2) NULL,           -- Điểm giữa kì
                               final_score DECIMAL(4, 2) NULL,             -- Điểm cuối kì

                               created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                               updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

                               CONSTRAINT pk_grade_reports PRIMARY KEY (id),

                               CONSTRAINT uq_grade_reports_class_member UNIQUE (class_member_id),

                               CONSTRAINT fk_grade_reports_class_member FOREIGN KEY (class_member_id)
                                   REFERENCES class_members (id)
                                   ON DELETE CASCADE
                                   ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_grade_reports_member ON grade_reports (class_member_id);