package com.example.ilms.dto.Room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomCreateRequestDTO {

    @NotBlank(message = "Tên phòng không được để trống")
    @Size(max = 100, message = "Tên phòng tối đa 100 ký tự")
    private String name;

    @Size(max = 50, message = "Loại phòng tối đa 50 ký tự")
    private String type;

    private String description;
}