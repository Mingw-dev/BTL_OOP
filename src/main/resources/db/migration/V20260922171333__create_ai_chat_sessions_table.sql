-- Migration: create_ai_chat_sessions_table
CREATE TABLE ai_chat_sessions (
                                  id BINARY(16) NOT NULL,                     -- Mã phiên (Khóa chính)
                                  user_id BINARY(16) NOT NULL,                -- Khóa ngoại trỏ sang bảng users (chủ sở hữu phiên)
                                  title VARCHAR(255) NOT NULL,                -- Tiêu đề phiên (tự sinh từ prompt đầu tiên)
                                  started_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6), -- Thời gian bắt đầu
                                  last_updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6), -- Thời gian cập nhật lần cuối

                                  CONSTRAINT pk_ai_chat_sessions PRIMARY KEY (id),

                                  CONSTRAINT fk_ai_chat_sessions_user FOREIGN KEY (user_id)
                                      REFERENCES users (id)
                                      ON DELETE CASCADE
                                      ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_ai_sessions_user ON ai_chat_sessions (user_id, last_updated_at DESC);

