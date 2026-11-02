package com.example.ilms.repository;

import com.example.ilms.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {
    @Query("""
            select count(cc) > 0 from ClassCourse cc
            where cc.classEntity.id = :classId and cc.course.id = :courseId and cc.status = 'ACTIVE'
            """)
    boolean hasActiveCourseLink(@Param("classId") UUID classId, @Param("courseId") UUID courseId);
}
