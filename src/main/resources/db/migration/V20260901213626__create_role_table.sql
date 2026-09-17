CREATE TABLE roles (
                       id BINARY(16) NOT NULL,
                       code VARCHAR(50) NOT NULL,
                       name VARCHAR(100) NOT NULL,
                       description VARCHAR(255) NULL,
                       created_at DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                       CONSTRAINT pk_roles PRIMARY KEY (id),
                       CONSTRAINT uq_roles_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO roles (id, code, name, description)
VALUES (
           0x00000000000000000000000000000001,
           'ROLE_ADMIN',
           'Admin Role',
           'Admin role'
       );

INSERT INTO roles (id, code, name, description)
VALUES (
           0x00000000000000000000000000000002,
           'ROLE_USER',
           'User Role',
           'Default fallback role'
       );
