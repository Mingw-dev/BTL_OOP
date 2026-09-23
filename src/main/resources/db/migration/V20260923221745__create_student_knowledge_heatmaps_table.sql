-- Migration: create_student_knowledge_heatmaps_table
CREATE TABLE `student_knowledge_heatmaps` (
                                              `id` binary(16) NOT NULL,
                                              `user_id` binary(16) NOT NULL,
                                              `knowledge_entity_id` varchar(64) NOT NULL, -- Khớp với định dạng ID bên phía Neo4j hoặc bảng mapping
                                              `mastery_level` decimal(4,3) NOT NULL DEFAULT 0.000, -- Chỉ số thông thạo từ 0.000 đến 1.000
                                              `last_assessed_at` datetime(6) DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
                                              `created_at` datetime(6) DEFAULT current_timestamp(6),
                                              PRIMARY KEY (`id`),
                                              UNIQUE KEY `uq_user_knowledge` (`user_id`, `knowledge_entity_id`),
                                              KEY `idx_heatmap_user` (`user_id`),
                                              KEY `idx_heatmap_knowledge` (`knowledge_entity_id`),
                                              CONSTRAINT `fk_heatmap_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
