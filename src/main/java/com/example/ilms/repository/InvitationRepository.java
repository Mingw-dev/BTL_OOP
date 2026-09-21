package com.example.ilms.repository;

import com.example.ilms.entity.Invitation;
import com.example.ilms.type.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    Optional<Invitation> findByToken(String token);

    Optional<Invitation> findFirstByClassEntity_IdAndEmailAndStatusOrderByCreatedAtDesc(
            UUID classId,
            String email,
            InvitationStatus status
    );
}