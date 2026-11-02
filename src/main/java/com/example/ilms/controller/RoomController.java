package com.example.ilms.controller;

import com.example.ilms.constant.SuccessCode;
import com.example.ilms.dto.Room.RoomCreateRequestDTO;
import com.example.ilms.dto.Room.RoomResponseDTO;
import com.example.ilms.dto.Room.RoomUpdateRequestDTO;
import com.example.ilms.dto.response.ApiResponse;
import com.example.ilms.entity.Room;
import com.example.ilms.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/rooms")
public class RoomController {
    private final RoomService roomService;
    @PostMapping
    public ApiResponse<Void> createRoom(@RequestBody @Valid RoomCreateRequestDTO dto) {
        roomService.createRoom(dto);
        return ApiResponse.of(SuccessCode.CREATE_ROOMS_SUUCESS, null);
    }
    @GetMapping
    public ApiResponse<List<RoomResponseDTO>> getRooms() {
        List<RoomResponseDTO> res = roomService.getRooms();
        return ApiResponse.of(SuccessCode.GET_ROOMS_SUCCESS, res);
    }
    @PutMapping("{roomId}")
    public ApiResponse<RoomResponseDTO> updateRoom(@PathVariable UUID roomId, @RequestBody @Valid RoomUpdateRequestDTO dto) {
        RoomResponseDTO result = roomService.updateRoom(roomId, dto);
        return ApiResponse.of(SuccessCode.UPDATE_ROOM_SUCCESS, result);
    }

    @DeleteMapping("{roomId}")
    public ApiResponse<Void> deleteRoom(@PathVariable UUID roomId) {
        roomService.deleteRoom(roomId);
        return ApiResponse.of(SuccessCode.DELETE_ROOM_SUCCESS, null);
    }
}
