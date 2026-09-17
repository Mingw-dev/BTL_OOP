package com.example.ilms.repository;

import com.example.ilms.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;


public interface CourseRepository extends JpaRepository<Course, UUID> {

    @Query("SELECT c FROM Course c WHERE " +
            "(:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:visibility IS NULL OR c.visibility = :visibility) AND " +
            "(:status IS NULL OR " +
            "(:status = true AND c.deletedAt IS NULL) OR " +
            "(:status = false AND c.deletedAt IS NOT NULL))")
    Page<Course> findCoursesWithFilters(
            @Param("search") String search,
            @Param("visibility") String visibility,
            @Param("status") Boolean status,
            Pageable pageable
    );
}