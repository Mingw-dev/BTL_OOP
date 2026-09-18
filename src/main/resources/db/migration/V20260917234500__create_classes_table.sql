CREATE TABLE classes (
                         id VARCHAR(36) PRIMARY KEY,
                         class_code VARCHAR(50) UNIQUE NOT NULL,
                         name VARCHAR(255) NOT NULL,
                         title VARCHAR(255),
                         description TEXT,
                         thumbnail_url VARCHAR(500),
                         background_url VARCHAR(500),
                         require_approval BOOLEAN DEFAULT TRUE,
                         is_code_active BOOLEAN DEFAULT TRUE,
                         owner_id VARCHAR(36),
                         status VARCHAR(20) DEFAULT 'ACTIVE',
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         deleted_at TIMESTAMP NULL
);

CREATE TABLE class_member (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              class_id VARCHAR(36) NOT NULL,
                              user_id VARCHAR(36) NOT NULL,
                              role VARCHAR(50) NOT NULL,
                              status VARCHAR(20) DEFAULT 'ACTIVE',
                              joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);