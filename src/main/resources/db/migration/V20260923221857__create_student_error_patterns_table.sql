-- Migration: create_student_error_patterns_table
CREATE TABLE `student_error_patterns` (
                                          `id` binary(16) NOT NULL,
                                          `user_id` binary(16) NOT NULL,
                                          `knowledge_entity_id` varchar(64) NOT NULL,
                                          `error_description` text NOT NULL,
                                          `frequency` int(11) NOT NULL DEFAULT 1,
                                          `last_occurred_at` datetime(6) DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
                                          PRIMARY KEY (`id`),
                                          KEY `idx_error_patterns_user` (`user_id`),
                                          KEY `idx_error_patterns_knowledge` (`knowledge_entity_id`),
                                          CONSTRAINT `fk_error_patterns_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;