-- Migration: material_chunks
DROP TABLE IF EXISTS `material_chunks`;
CREATE TABLE `material_chunks` (
                                   `id` binary(16) NOT NULL,
                                   `material_id` binary(16) NOT NULL,
                                   `chunk_index` int(11) NOT NULL DEFAULT 0,
                                   `token_count` int(11) DEFAULT NULL,
                                   `content_text` longtext NOT NULL,
                                   `embedding_vector` text DEFAULT NULL, -- Hoặc kiểu VECTOR (nếu dùng MySQL hỗ trợ native vector như MySQL 9.0+)
                                   `created_at` datetime(6) DEFAULT current_timestamp(6),
                                   PRIMARY KEY (`id`),
                                   KEY `idx_material_chunks_material` (`material_id`),
                                   CONSTRAINT `fk_material_chunks_material` FOREIGN KEY (`material_id`) REFERENCES `materials` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
