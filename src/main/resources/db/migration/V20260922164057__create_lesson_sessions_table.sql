-- Migration: create_lesson_sessions_table
CREATE TABLE lesson_sessions (
                                 id BINARY(16) NOT NULL,
                                 room_id BINARY(16) NOT NULL,
                                 class_id BINARY(16) NOT NULL,
                                 time_slot_id BINARY(16) NOT NULL,
                                 session_date DATE NOT NULL,
                                 note TEXT NULL,

                                 CONSTRAINT pk_lesson_sessions PRIMARY KEY (id),
                                 CONSTRAINT uk_room_time_date UNIQUE (room_id, time_slot_id, session_date),
                                 CONSTRAINT uk_class_time_date UNIQUE (class_id, time_slot_id, session_date),
    
                                 CONSTRAINT fk_lesson_sessions_class FOREIGN KEY (class_id)
                                     REFERENCES classes (id)
                                     ON DELETE CASCADE
                                     ON UPDATE CASCADE,

                                 CONSTRAINT fk_lesson_sessions_room FOREIGN KEY (room_id)
                                     REFERENCES rooms (id)
                                     ON DELETE RESTRICT
                                     ON UPDATE CASCADE,

                                     CONSTRAINT fk_lesson_sessions_time_slot FOREIGN KEY (time_slot_id)
                                     REFERENCES time_slots (id)
                                     ON DELETE RESTRICT
                                     ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_sessions_room_date ON lesson_sessions (room_id, session_date, time_slot_id);