-- Migration: create_ai_chat_messages_table
CREATE TABLE ai_chat_messages (
                                  id BINARY(16) NOT NULL,                     -- Mã tin nhắn (Khóa chính)
                                  session_id BINARY(16) NOT NULL,             -- Khóa ngoại "Thuộc về" phiên hỏi đáp nào
                                  content LONGTEXT NOT NULL,                  -- Nội dung tin nhắn
                                  message_type VARCHAR(20) NOT NULL,          -- Loại tin nhắn: 'USER', 'ASSISTANT', 'SYSTEM'
                                  sent_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6), -- Thời gian gửi
                                  user_feedback TEXT NULL,                    -- Phản hồi người dùng (góp ý bằng lời)
                                  feedback_rating VARCHAR(20) NULL,           -- Đánh giá phản hồi: 'LIKE', 'DISLIKE' hoặc số sao '1'..'5'

                                  CONSTRAINT pk_ai_chat_messages PRIMARY KEY (id),

                                  CONSTRAINT fk_ai_messages_session FOREIGN KEY (session_id)
                                      REFERENCES ai_chat_sessions (id)
                                      ON DELETE CASCADE
                                      ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_ai_messages_session_time ON ai_chat_messages (session_id, sent_at ASC);