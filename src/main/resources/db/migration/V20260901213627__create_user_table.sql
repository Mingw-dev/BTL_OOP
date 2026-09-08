-- Migration: create_user_table
CREATE TABLE users (
                       id BINARY(16) NOT NULL,
                       role_id BINARY(16) NOT NULL DEFAULT (0x00000000000000000000000000000001),
                       first_name VARCHAR(50) NOT NULL,
                       middle_name VARCHAR(50) NULL,
                       last_name VARCHAR(50) NOT NULL,
                       email VARCHAR(150) NOT NULL,
                       password VARCHAR(100) NOT NULL,
                       status ENUM('PENDING', 'ACTIVE', 'LOCKED', 'BANNED', 'DELETED') NOT NULL DEFAULT 'PENDING',
                       created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                       CONSTRAINT pk_users PRIMARY KEY (id),
                       CONSTRAINT uq_users_email UNIQUE (email),
                       CONSTRAINT fk_ru_role_id FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT,
                       INDEX idx_users_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;