CREATE TABLE classes (
                         id BINARY(16) NOT NULL,
                         class_code VARCHAR(50) UNIQUE NOT NULL,
                         name VARCHAR(255) NOT NULL,
                         title VARCHAR(255),
                         description TEXT,
                         visibility VARCHAR(20) DEFAULT 'PUBLIC',
                         thumbnail_url VARCHAR(500),
                         background_url VARCHAR(500),
                         require_approval BOOLEAN DEFAULT TRUE,
                         is_code_active BOOLEAN DEFAULT TRUE,
                         owner_id BINARY(16) NOT NULL,
                         start_at TIMESTAMP NULL,
                         end_at TIMESTAMP NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         deleted_at TIMESTAMP NULL,
                         CONSTRAINT pk_classes PRIMARY KEY (id),
                         CONSTRAINT fk_classes_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE class_members (
                              id BINARY(16) NOT NULL,
                              class_id BINARY(16) NOT NULL,
                              user_id BINARY(16) NOT NULL,
                              role VARCHAR(50) NOT NULL,
                              status VARCHAR(20) DEFAULT 'ACTIVE',
                              joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT pk_class_member PRIMARY KEY (id),
                              CONSTRAINT fk_cm_class FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
                              CONSTRAINT fk_cm_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                              INDEX idx_cm_role_status (role, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;