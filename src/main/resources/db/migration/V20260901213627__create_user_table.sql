-- Migration: create_user_table
CREATE TABLE users (
                       id BINARY(16) NOT NULL,
                       role_id BINARY(16) NOT NULL,
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

INSERT INTO `users` (`id`, `role_id`, `first_name`, `middle_name`, `last_name`, `email`, `password`, `status`, `created_at`, `updated_at`) VALUES
-- Admin
(UNHEX('AAAAAAAAAAAAAAAABBBBBBBBBBBBBBBB'), 0x00000000000000000000000000000001, 'Trần', 'Văn', 'Hoàng', 'admin@ptit.edu.vn', '$2a$10$Dk4tP.f.Xy.hKj4qR/Ycye2k.0p8Zf2VqA/y0qM1r.ZfW.2fL3a2C', 'ACTIVE', NOW(), NOW()),
-- User
(UNHEX('CCCCCCCCCCCCCCCCDDDDDDDDDDDDDDDD'), 0x00000000000000000000000000000002, 'Trần', 'Thị', 'Hương', 'huongtt@ptit.edu.vn', '$2a$10$Dk4tP.f.Xy.hKj4qR/Ycye2k.0p8Zf2VqA/y0qM1r.ZfW.2fL3a2C', 'ACTIVE', NOW(), NOW()),
(UNHEX('12345678901234567890123456789012'), 0x00000000000000000000000000000002, 'Nguyễn', 'Hoàng', 'Nam', 'namnh.student@gmail.com', '$2a$10$Dk4tP.f.Xy.hKj4qR/Ycye2k.0p8Zf2VqA/y0qM1r.ZfW.2fL3a2C', 'ACTIVE', NOW(), NOW()),
(UNHEX('09876543210987654321098765432109'), 0x00000000000000000000000000000002, 'Phạm', 'Thùy', 'Linh', 'linhpt.student@gmail.com', '$2a$10$Dk4tP.f.Xy.hKj4qR/Ycye2k.0p8Zf2VqA/y0qM1r.ZfW.2fL3a2C', 'ACTIVE', NOW(), NOW());