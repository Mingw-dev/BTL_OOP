package com.example.ilms.repository;

import com.example.ilms.entity.Attendance;
import com.example.ilms.entity.LessonSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceReposity extends JpaRepository<Attendance, UUID> {
    List<Attendance> findByLessonSession_Id(UUID sessionId);
}
