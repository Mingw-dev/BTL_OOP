CREATE TABLE IF NOT EXISTS `chapters` (
                                          `id` BINARY(16) NOT NULL,
    `course_id` BINARY(16) NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `description` TEXT NULL,
    `order_index` INT NOT NULL DEFAULT 0,
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    `deleted_at` DATETIME NULL,
    PRIMARY KEY (`id`),
    KEY `idx_chapters_course_id` (`course_id`),
    KEY `idx_chapters_deleted_at` (`deleted_at`),
    CONSTRAINT `fk_chapters_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;