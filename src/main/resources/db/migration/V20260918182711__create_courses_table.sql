CREATE TABLE IF NOT EXISTS `courses` (
                                         `id` BINARY(16) NOT NULL,
                                         `course_code` VARCHAR(50) NOT NULL,
                                         `name` VARCHAR(255) NOT NULL,
                                         `description` TEXT NULL,
                                         `visibility` VARCHAR(50) NOT NULL DEFAULT 'PUBLIC',
                                         created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                         updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                         `deleted_at` DATETIME NULL,
                                         `owner_id` BINARY(16) NULL,
                                         PRIMARY KEY (`id`),
                                         UNIQUE KEY `uk_courses_course_code` (`course_code`),
                                         KEY `idx_courses_owner_id` (`owner_id`),
                                         KEY `idx_courses_deleted_at` (`deleted_at`),
                                         CONSTRAINT `fk_courses_owner` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;