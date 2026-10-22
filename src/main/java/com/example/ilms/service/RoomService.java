    package com.example.ilms.service;

    import com.example.ilms.dto.Room.RoomCreateRequestDTO;
    import com.example.ilms.dto.Room.RoomResponseDTO;
    import com.example.ilms.dto.Room.RoomUpdateRequestDTO;
    import com.example.ilms.entity.Room;
    import com.example.ilms.exception.AppException;
    import com.example.ilms.exception.ErrorCode;
    import com.example.ilms.repository.RoomRepositoty;
    import lombok.RequiredArgsConstructor;
    import org.modelmapper.ModelMapper;
    import org.springframework.data.domain.Sort;
    import org.springframework.stereotype.Service;

    import java.util.List;
    import java.util.UUID;

    @Service
    @RequiredArgsConstructor
    public class RoomService {
        private final RoomRepositoty roomRepositoty;
        private final ModelMapper modelMapper;

        public void createRoom(RoomCreateRequestDTO dto) {
            Room room = modelMapper.map(dto, Room.class);
            roomRepositoty.save(room);
        }
        public RoomResponseDTO updateRoom(UUID roomId, RoomUpdateRequestDTO request) {
            Room room = roomRepositoty.findById(roomId)
                    .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
            room.setName(request.getName());
            room.setType(request.getType());
            room.setDescription(request.getDescription());

            room = roomRepositoty.save(room);
            return  modelMapper.map(room, RoomResponseDTO.class);
        }
        public List<RoomResponseDTO> getRooms() {
            return roomRepositoty.findAll(Sort.by("name").ascending())
                    .stream()
                    .map(room -> modelMapper.map(room, RoomResponseDTO.class))
                    .toList();
        }
        public void deleteRoom(UUID roomId) {
            Room room = roomRepositoty.findById(roomId)
                    .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
            roomRepositoty.delete(room);
        }
    }
