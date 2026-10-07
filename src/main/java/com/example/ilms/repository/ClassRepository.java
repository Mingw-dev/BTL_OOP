package com.example.ilms.repository;

import com.example.ilms.entity.ClassEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, UUID> {

    // 1. Dùng để kiểm tra khi generate random code (đã có sẵn)
    boolean existsByClassCode(String classCode);

    // 2. Tự động sinh query tìm lớp học theo code (không cần viết @Query)
    Optional<ClassEntity> findByClassCode(String classCode);

    // 3. Câu query phức tạp DÀNH RIÊNG cho tính năng phân trang (dùng trong hàm getClasses)
    @Query("SELECT c FROM ClassEntity c WHERE " +
            "(:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:visibility IS NULL OR c.visibility = :visibility)")
    Page<ClassEntity> findClassesWithFilter(
            @Param("search") String search,
            @Param("visibility") String visibility,
            Pageable pageable);
}