-- Migration: create_attendances_table
CREATE TABLE attendances (
                             id BINARY(16) NOT NULL,
                             session_id BINARY(16) NOT NULL,
                             user_id BINARY(16) NOT NULL,
                             status VARCHAR(20) NOT NULL DEFAULT 'ABSENT', -- Trạng thái: 'PRESENT', 'ABSENT', 'LATE', 'EXCUSED'
                             check_in_time DATETIME(6) NULL,            -- Thời gian thực tế điểm danh/quẹt thẻ
                             note VARCHAR(255) NULL,                    -- Ghi chú (ví dụ: lý do nghỉ phép, đi muộn 15p)
                             created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                             updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

                             CONSTRAINT pk_attendances PRIMARY KEY (id),

                             CONSTRAINT uq_attendances_session_user UNIQUE (session_id, user_id),

                             CONSTRAINT fk_attendances_session FOREIGN KEY (session_id)
                                 REFERENCES lesson_sessions (id)
                                 ON DELETE CASCADE
                                 ON UPDATE CASCADE,

                             CONSTRAINT fk_attendances_user FOREIGN KEY (user_id)
                                 REFERENCES users (id)
                                 ON DELETE CASCADE
                                 ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_attendances_session ON attendances (session_id);
CREATE INDEX idx_attendances_user ON attendances (user_id);