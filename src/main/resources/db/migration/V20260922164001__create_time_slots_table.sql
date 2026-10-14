-- Migration: create_time_slots_table
CREATE TABLE time_slots (
                            id BINARY(16) NOT NULL,
                            name VARCHAR(50) NOT NULL,                  -- VD: 'Ca 1', 'Ca 2', 'Ca Sáng', 'Slot 1'
                            start_time TIME NOT NULL,                   -- Giờ bắt đầu (VD: '07:00:00')
                            end_time TIME NOT NULL,                     -- Giờ kết thúc (VD: '09:00:00')
                            description VARCHAR(255) NULL,              -- Ghi chú thêm
                            start_shift INT NULL,
                            end_shift INT NULL,
                            created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
                            updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

                            CONSTRAINT pk_time_slots PRIMARY KEY (id),
                            CONSTRAINT uq_time_slot_name UNIQUE (name)  -- Đảm bảo không tạo trùng tên ca học
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;