package com.example.ilms.dto.Room;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Data
@Builder
public class RoomResponseDTO {
    private UUID id;
    private String name;
    private String type;
    private String description;
}
