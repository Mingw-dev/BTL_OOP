CREATE TABLE invitations (
                             id BINARY(16) NOT NULL,
                             class_id BINARY(16) NOT NULL,
                             invite_by BINARY(16) NOT NULL,
                             email VARCHAR(255) NOT NULL,
                             role VARCHAR(50) NOT NULL,
                             token VARCHAR(255) NOT NULL,
                             status ENUM('PENDING', 'ACCEPTED', 'EXPIRED') NOT NULL DEFAULT 'PENDING',
                             expires_at DATETIME(6) NOT NULL,
                             created_at DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6),

                             CONSTRAINT pk_invitations PRIMARY KEY (id),
                             CONSTRAINT uq_invitations_token UNIQUE (token),

                             CONSTRAINT fk_invitations_class_id
                                 FOREIGN KEY (class_id)
                                     REFERENCES classes(id)
                                     ON DELETE CASCADE,

                             CONSTRAINT fk_invitations_invite_by
                                 FOREIGN KEY (invite_by)
                                     REFERENCES users(id)
                                     ON DELETE CASCADE,

                             INDEX idx_invitations_email (email),
                             INDEX idx_invitations_class_id (class_id)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;