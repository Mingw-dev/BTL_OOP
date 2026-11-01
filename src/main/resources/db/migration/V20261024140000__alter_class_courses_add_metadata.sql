ALTER TABLE class_courses
    ADD COLUMN teacher_id    BINARY(16)  NULL,
    ADD COLUMN semester      VARCHAR(50) NULL,
    ADD COLUMN academic_year VARCHAR(20) NULL,
    ADD COLUMN start_date    DATE        NULL,
    ADD COLUMN end_date      DATE        NULL,
    ADD CONSTRAINT fk_class_courses_teacher FOREIGN KEY (teacher_id)
        REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE,
    ADD INDEX idx_class_courses_semester (semester, academic_year);