-- Migration: create_rooms_table
CREATE TABLE rooms (
                       id BINARY(16) NOT NULL,
                       name VARCHAR(100) NOT NULL,
                       type VARCHAR(50) NULL,
                       description TEXT NULL,

                       CONSTRAINT pk_rooms PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;