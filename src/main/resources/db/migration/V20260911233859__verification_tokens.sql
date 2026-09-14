-- Migration: verification_tokens
CREATE TABLE verification_tokens (
                                  id BINARY(16) NOT NULL,
                                  user_id BINARY(16) NOT NULL,
                                  token VARCHAR(255) NOT NULL,
                                  email VARCHAR(255) NOT NULL,
                                  type VARCHAR(100) NOT NULL,
                                  status ENUM('PENDING', 'VERIFIED', 'EXPIRED', 'FAILED', 'REVOKED') NOT NULL DEFAULT 'PENDING',
                                  expires_at DATETIME(6) NOT NULL,
                                  created_at DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6),
                                  CONSTRAINT pk_verify_token PRIMARY KEY (id),
                                  CONSTRAINT fk_verify_token_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;