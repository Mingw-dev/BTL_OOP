-- Migration: create_class_announcements_table
CREATE TABLE class_announcements (
                                     id BINARY(16) NOT NULL,
                                     class_id BINARY(16) NOT NULL,
                                     author_id BINARY(16) NOT NULL,
                                     title VARCHAR(255) NOT NULL,
                                     content LONGTEXT NOT NULL,
                                     is_pinned BOOLEAN NOT NULL DEFAULT FALSE,
                                     created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                                     updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

                                     CONSTRAINT pk_class_announcements PRIMARY KEY (id),

                                     CONSTRAINT fk_announcements_class FOREIGN KEY (class_id)
                                         REFERENCES classes (id)
                                         ON DELETE CASCADE
                                         ON UPDATE CASCADE,

                                     CONSTRAINT fk_announcements_author FOREIGN KEY (author_id)
                                         REFERENCES users (id)
                                         ON DELETE RESTRICT
                                         ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_announcements_class_pinned ON class_announcements (class_id, is_pinned DESC);