-- Migration: create_lesson_sessions_table
CREATE TABLE lesson_sessions (
                                 id BINARY(16) NOT NULL,
                                 room_id BINARY(16) NULL,
                                 class_id BINARY(16) NOT NULL,
                                 session_date DATE NOT NULL,
                                 start_shift INT NULL,
                                 end_shift INT NULL,
                                 start_time TIME NULL,
                                 end_time TIME NULL,
                                 note TEXT NULL,

                                 CONSTRAINT pk_lesson_sessions PRIMARY KEY (id),

                                 CONSTRAINT fk_lesson_sessions_class FOREIGN KEY (class_id)
                                     REFERENCES classes (id)
                                     ON DELETE CASCADE
                                     ON UPDATE CASCADE,

                                 CONSTRAINT fk_lesson_sessions_room FOREIGN KEY (room_id)
                                     REFERENCES rooms (id)
                                     ON DELETE SET NULL
                                     ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_sessions_room_date ON lesson_sessions (room_id, session_date);