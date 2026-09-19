package com.example.ilms.repository;

import com.example.ilms.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;


public interface CourseRepository extends JpaRepository<Course, UUID> {

    @Query(value = "SELECT * FROM courses c WHERE " +
            "(:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.course_code) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:visibility IS NULL OR c.visibility = :visibility) AND " +
            "(:status IS NULL OR " +
            "(:status = true AND c.deleted_at IS NULL) OR " +
            "(:status = false AND c.deleted_at IS NOT NULL)) " +
            "ORDER BY c.created_at DESC, c.name ASC " +
            "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<Course> findCoursesWithFilters(
            @Param("search") String search,
            @Param("visibility") String visibility,
            @Param("status") Boolean status,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = "SELECT COUNT(*) FROM courses c WHERE " +
            "(:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.course_code) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:visibility IS NULL OR c.visibility = :visibility) AND " +
            "(:status IS NULL OR " +
            "(:status = true AND c.deleted_at IS NULL) OR " +
            "(:status = false AND c.deleted_at IS NOT NULL))",
            nativeQuery = true)
    long countCoursesWithFilters(
            @Param("search") String search,
            @Param("visibility") String visibility,
            @Param("status") Boolean status
    );
}