-- Migration: create_assessments_table
CREATE TABLE assessments (
                             id BINARY(16) NOT NULL,
                             class_id BINARY(16) NOT NULL,
                             course_id BINARY(16) NOT NULL,
                             created_by BINARY(16) NOT NULL,
                             title VARCHAR(255) NOT NULL,
                             created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                             updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

                             CONSTRAINT pk_assessments PRIMARY KEY (id),

                             CONSTRAINT fk_assessments_class FOREIGN KEY (class_id)
                                 REFERENCES classes (id)
                                 ON DELETE CASCADE
                                 ON UPDATE CASCADE,

                             CONSTRAINT fk_assessments_course FOREIGN KEY (course_id)
                                 REFERENCES courses (id)
                                 ON DELETE CASCADE
                                 ON UPDATE CASCADE,

                             CONSTRAINT fk_assessments_creator FOREIGN KEY (created_by)
                                 REFERENCES users (id)
                                 ON DELETE RESTRICT
                                 ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_assessments_class_due ON assessments (class_id, course_id);