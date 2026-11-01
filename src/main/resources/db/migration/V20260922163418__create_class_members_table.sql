-- Migration: create_class_members_table
CREATE TABLE class_members (
                               id BINARY(16) NOT NULL,
                               user_id BINARY(16) NULL,
                               class_id BINARY(16) NULL,
                               role VARCHAR(255) NULL,
                               status VARCHAR(255) NULL,
                               joined_at DATETIME(6) NULL,

                               CONSTRAINT pk_class_members PRIMARY KEY (id),

                               CONSTRAINT fk_class_members_user FOREIGN KEY (user_id)
                                   REFERENCES users (id)
                                   ON DELETE RESTRICT
                                   ON UPDATE CASCADE,

                               CONSTRAINT fk_class_members_class FOREIGN KEY (class_id)
                                   REFERENCES classes (id)
                                   ON DELETE RESTRICT
                                   ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;