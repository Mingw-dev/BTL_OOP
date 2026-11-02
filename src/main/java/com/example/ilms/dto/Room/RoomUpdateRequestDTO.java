package com.example.ilms.dto.Room;

import lombok.Data;

@Data
public class RoomUpdateRequestDTO {
    private String name;

    private String type;
    private String description;
}
