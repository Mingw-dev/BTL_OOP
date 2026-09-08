-- Migration: create_permission_table
CREATE TABLE permissions (
                             id BINARY(16) NOT NULL,
                             code VARCHAR(50) NOT NULL,
                             name VARCHAR(100) NOT NULL,
                             module VARCHAR(50) NULL,
                             description VARCHAR(255) NULL,
                             created_at DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6),
                             updated_at DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                             CONSTRAINT pk_permissions PRIMARY KEY (id),
                             CONSTRAINT uq_permissions_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;