package com.example.ilms.repository;

import com.example.ilms.entity.ClassCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClassCourseRepository extends JpaRepository<ClassCourse, UUID> {

    boolean existsByClassEntity_IdAndCourse_Id(UUID classId, UUID courseId);

//    Comment tạm để lúc khác push code lên sau
//    @Query(value = """
//            SELECT cc FROM ClassCourse cc
//            JOIN FETCH cc.course c
//            JOIN FETCH cc.classEntity cl
//            LEFT JOIN FETCH cc.teacher t
//            WHERE cl.id = :classId
//              AND c.deletedAt IS NULL
//              AND (:status IS NULL OR cc.status = :status)
//              AND (:semester IS NULL OR cc.semester = :semester)
//            """,
//            countQuery = """
//            SELECT COUNT(cc) FROM ClassCourse cc
//            JOIN cc.course c
//            WHERE cc.classEntity.id = :classId
//              AND c.deletedAt IS NULL
//              AND (:status IS NULL OR cc.status = :status)
//              AND (:semester IS NULL OR cc.semester = :semester)
//            """)
//    Page<ClassCourse> findByClass(
//            @Param("classId") UUID classId,
//            @Param("status") String status,
//            @Param("semester") String semester,
//            Pageable pageable);
}