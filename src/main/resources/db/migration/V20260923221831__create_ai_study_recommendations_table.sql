-- Migration: create_ai_study_recommendations_table
CREATE TABLE `ai_study_recommendations` (
                                            `id` binary(16) NOT NULL,
                                            `user_id` binary(16) NOT NULL,
                                            `knowledge_entity_id` varchar(64) NOT NULL,
                                            `priority_level` varchar(20) NOT NULL DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, URGENT
                                            `status` varchar(20) NOT NULL DEFAULT 'PENDING', -- PENDING, IN_PROGRESS, COMPLETED
                                            `recommendation_note` text DEFAULT NULL,
                                            `created_at` datetime(6) DEFAULT current_timestamp(6),
                                            `updated_at` datetime(6) DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
                                            PRIMARY KEY (`id`),
                                            KEY `idx_recommendations_user` (`user_id`),
                                            CONSTRAINT `fk_recommendations_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;