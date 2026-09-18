-- Migration: create_user_table
CREATE TABLE users (
                       id BINARY(16) NOT NULL,
                       role_id BINARY(16) NOT NULL DEFAULT (0x00000000000000000000000000000002),
                       first_name VARCHAR(50) NOT NULL,
                       middle_name VARCHAR(50) NULL,
                       last_name VARCHAR(50) NOT NULL,
                       avatar_url VARCHAR(255) DEFAULT NULL,
                       email VARCHAR(150) NOT NULL,
                       password VARCHAR(100) NOT NULL,
                       status ENUM('PENDING', 'ACTIVE', 'LOCKED', 'BANNED', 'DELETED') NOT NULL DEFAULT 'PENDING',
                       created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                       deleted_at DATETIME(6) DEFAULT NULL,
                       CONSTRAINT pk_users PRIMARY KEY (id),
                       CONSTRAINT uq_users_email UNIQUE (email),
                       CONSTRAINT fk_ru_role_id FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT,
                       INDEX idx_users_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO users (id, role_id, first_name, middle_name, last_name, email, password, status)
VALUES (
           0x00000000000000000000000000000001,
           0x00000000000000000000000000000001,
           'Admin',
           'Admin',
           'Admin',
           'admin@gmail.com',
           '$2a$10$7Z8Kq5z0jL6.s8jPjG/lcu0h6T5fN6m0v7a8uQ8k1m0p8l2rG8k2',
           'ACTIVE'
       );
# admin123 password