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

    // Lấy danh sách (LỚP + MÔN HỌC) mà 1 user đang tham gia.
    // Mỗi dòng = 1 lớp user đang học (status ACTIVE) ghép với 1 môn được gán cho lớp đó (status ACTIVE).
    @Query(value = "SELECT HEX(cl.id) AS classId, " +
            "cl.name AS className, " +
            "cl.class_code AS classCode, " +
            "HEX(c.id) AS courseId, " +
            "c.name AS name, " +
            "c.description AS description, " +
            "HEX(c.owner_id) AS ownerId, " +
            "CAST(cm.joined_at AS CHAR) AS registeredAt " +
            "FROM class_members cm " +
            "JOIN classes cl ON cl.id = cm.class_id " +
            "JOIN class_courses cc ON cc.class_id = cl.id " +
            "JOIN courses c ON c.id = cc.course_id " +
            "WHERE cm.user_id = UNHEX(REPLACE(:userId, '-', '')) " +
            "AND cm.status = 'ACTIVE' " +
            "AND cc.status = 'ACTIVE' " +
            "AND cl.deleted_at IS NULL " +
            "AND c.deleted_at IS NULL " +
            "ORDER BY cm.joined_at DESC, cl.name ASC, c.name ASC " +
            "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<UserCourseProjection> findUserCourses(
            @Param("userId") String userId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = "SELECT COUNT(*) " +
            "FROM class_members cm " +
            "JOIN classes cl ON cl.id = cm.class_id " +
            "JOIN class_courses cc ON cc.class_id = cl.id " +
            "JOIN courses c ON c.id = cc.course_id " +
            "WHERE cm.user_id = UNHEX(REPLACE(:userId, '-', '')) " +
            "AND cm.status = 'ACTIVE' " +
            "AND cc.status = 'ACTIVE' " +
            "AND cl.deleted_at IS NULL " +
            "AND c.deleted_at IS NULL",
            nativeQuery = true)
    long countUserCourses(@Param("userId") String userId);

    interface UserCourseProjection {
        String getClassId();
        String getClassName();
        String getClassCode();
        String getCourseId();
        String getName();
        String getDescription();
        String getOwnerId();
        String getRegisteredAt();
    }
}