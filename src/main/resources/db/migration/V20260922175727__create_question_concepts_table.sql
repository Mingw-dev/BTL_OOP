-- Migration: create_question_concepts_table
CREATE TABLE question_concepts (
                                   id BINARY(16) NOT NULL,
                                   question_id BINARY(16) NOT NULL,            -- Khóa ngoại trỏ sang questions (MySQL)
                                   concept_id VARCHAR(64) NOT NULL,            -- ID của node Concept bên Neo4j
                                   relevance_weight DECIMAL(3, 2) NOT NULL DEFAULT 1.00, -- Trọng số liên quan (0.00 -> 1.00)

                                   CONSTRAINT pk_question_concepts PRIMARY KEY (id),

    -- Đảm bảo không gán trùng lặp 1 concept nhiều lần cho cùng 1 câu hỏi
                                   CONSTRAINT uq_qc_question_concept UNIQUE (question_id, concept_id),

    -- Khóa ngoại trỏ sang bảng questions
                                   CONSTRAINT fk_qc_question FOREIGN KEY (question_id)
                                       REFERENCES questions (id)
                                       ON DELETE CASCADE
                                       ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Index tra cứu theo Concept (dùng cho Neo4j tra ngược sang MySQL)
CREATE INDEX idx_qc_concept ON question_concepts (concept_id);

-- Index tra cứu theo Câu hỏi (lấy danh sách concepts của 1 question)
CREATE INDEX idx_qc_question ON question_concepts (question_id);