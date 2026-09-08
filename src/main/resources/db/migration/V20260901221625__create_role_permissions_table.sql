-- Migration: create_role_permissions_table
CREATE TABLE role_permissions (
                                  role_id BINARY(16) NOT NULL,
                                  permission_id BINARY(16) NOT NULL,
                                  created_at DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6),
                                  CONSTRAINT pk_role_permissions PRIMARY KEY (role_id, permission_id),
                                  CONSTRAINT fk_rp_role_id FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
                                  CONSTRAINT fk_rp_permission_id FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;