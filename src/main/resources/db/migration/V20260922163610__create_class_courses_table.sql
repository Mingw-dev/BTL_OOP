-- Migration: create_class_courses_table
CREATE TABLE class_courses (
                               id BINARY(16) NOT NULL,
                               class_id BINARY(16) NOT NULL,
                               course_id BINARY(16) NOT NULL,
                               assigned_at DATETIME(6) NULL,
                               status VARCHAR(50) DEFAULT 'ACTIVE',

                               CONSTRAINT pk_class_courses PRIMARY KEY (id),

                               CONSTRAINT uq_class_courses UNIQUE (class_id, course_id),

                               CONSTRAINT fk_class_courses_class FOREIGN KEY (class_id)
                                   REFERENCES classes (id)
                                   ON DELETE CASCADE
                                   ON UPDATE CASCADE,

                               CONSTRAINT fk_class_courses_course FOREIGN KEY (course_id)
                                   REFERENCES courses (id)
                                   ON DELETE CASCADE
                                   ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;