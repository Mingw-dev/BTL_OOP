package com.example.ilms.repository;

import com.example.ilms.entity.ClassMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassMemberRepository extends JpaRepository<ClassMember, UUID> {

    Optional<ClassMember> findByClassEntity_IdAndUser_Id(UUID classId, UUID userId);

    @Query("""
            SELECT cm
            FROM ClassMember cm
            JOIN cm.user u
            WHERE cm.classEntity.id = :classId
              AND (:role IS NULL OR cm.role = :role)
              AND (:status IS NULL OR cm.status = :status)
              AND (
                    :search IS NULL
                    OR :search = ''
                    OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.middleName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
              )
            """)
    Page<ClassMember> findMembers(
            @Param("classId") UUID classId,
            @Param("role") String role,
            @Param("status") String status,
            @Param("search") String search,
            Pageable pageable
    );
}