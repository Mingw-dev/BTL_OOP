package com.example.ilms.repository;

import com.example.ilms.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoomRepositoty extends JpaRepository<Room, UUID> {
}
