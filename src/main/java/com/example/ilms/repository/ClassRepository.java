package com.example.ilms.repository;

import com.example.ilms.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, UUID>, JpaSpecificationExecutor<ClassEntity>{

    boolean existsByClassCode(String classCode);

    Optional<ClassEntity> findByClassCode(String classCode);
}
