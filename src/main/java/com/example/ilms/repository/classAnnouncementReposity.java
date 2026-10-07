package com.example.ilms.repository;

import com.example.ilms.entity.ClassAnnouncement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
@Repository
public interface classAnnouncementReposity extends JpaRepository<ClassAnnouncement, UUID> {
    // Lấy thông báo theo lớp học
    Page<ClassAnnouncement> findByClassEntityId(UUID authorId, Pageable pageable);

}
