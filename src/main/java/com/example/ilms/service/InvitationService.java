package com.example.ilms.service;

import com.example.ilms.dto.classDTO.InviteByEmailRequestDTO;
import com.example.ilms.entity.ClassEntity;
import com.example.ilms.entity.ClassMember;
import com.example.ilms.entity.Invitation;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.ClassMemberRepository;
import com.example.ilms.repository.ClassRepository;
import com.example.ilms.repository.InvitationRepository;
import com.example.ilms.repository.UserRepository;
import com.example.ilms.type.InvitationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private static final long INVITATION_EXPIRY_DAYS = 7;

    private final InvitationRepository invitationRepository;
    private final ClassRepository classRepository;
    private final ClassMemberRepository classMemberRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Transactional
    public void inviteByEmail(
            UUID classId,
            InviteByEmailRequestDTO request,
            User currentUser
    ) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        String role = request.getRole()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!role.equals("STUDENT") && !role.equals("TEACHER")) {
            throw new AppException(ErrorCode.INVALID_INVITATION_ROLE);
        }

        validateInvitePermission(
                classEntity,
                role,
                currentUser
        );

        Optional<User> invitedUser = userRepository.findByEmail(email);

        if (invitedUser.isPresent()) {
            boolean alreadyMember = classMemberRepository
                    .findByClassEntity_IdAndUser_Id(
                            classId,
                            invitedUser.get().getId()
                    )
                    .isPresent();

            if (alreadyMember) {
                throw new AppException(ErrorCode.ALREADY_CLASS_MEMBER);
            }
        }

        LocalDateTime now = LocalDateTime.now();

        Optional<Invitation> pendingInvitation =
                invitationRepository
                        .findFirstByClassEntity_IdAndEmailAndStatusOrderByCreatedAtDesc(
                                classId,
                                email,
                                InvitationStatus.PENDING
                        );

        if (pendingInvitation.isPresent()) {
            Invitation oldInvitation = pendingInvitation.get();

            if (oldInvitation.getExpiresAt().isAfter(now)) {
                throw new AppException(
                        ErrorCode.INVITATION_ALREADY_EXISTS
                );
            }

            oldInvitation.setStatus(InvitationStatus.EXPIRED);
            invitationRepository.save(oldInvitation);
        }

        String token = UUID.randomUUID().toString();

        Invitation invitation = Invitation.builder()
                .classEntity(classEntity)
                .inviteBy(currentUser)
                .email(email)
                .role(role)
                .token(token)
                .status(InvitationStatus.PENDING)
                .expiresAt(now.plusDays(INVITATION_EXPIRY_DAYS))
                .build();

        invitationRepository.save(invitation);

        String invitationUrl =
                frontendUrl
                        + "/classes/invitations/accept?token="
                        + token;

        emailService.sendClassInvitationEmail(
                email,
                classEntity.getName(),
                invitationUrl
        );
    }

    @Transactional
    public void acceptInvitation(
            String token,
            User currentUser
    ) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(
                        () -> new AppException(
                                ErrorCode.INVITATION_NOT_FOUND
                        )
                );

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new AppException(
                    ErrorCode.INVITATION_ALREADY_PROCESSED
            );
        }

        LocalDateTime now = LocalDateTime.now();

        if (!invitation.getExpiresAt().isAfter(now)) {
            throw new AppException(
                    ErrorCode.INVITATION_EXPIRED
            );
        }

        if (currentUser.getEmail() == null
                || !invitation.getEmail()
                .equalsIgnoreCase(currentUser.getEmail())) {

            throw new AppException(
                    ErrorCode.INVITATION_EMAIL_MISMATCH
            );
        }

        ClassEntity classEntity = invitation.getClassEntity();

        Optional<ClassMember> existingMember =
                classMemberRepository
                        .findByClassEntity_IdAndUser_Id(
                                classEntity.getId(),
                                currentUser.getId()
                        );

        if (existingMember.isPresent()) {
            invitation.setStatus(
                    InvitationStatus.ACCEPTED
            );

            invitationRepository.save(invitation);
            return;
        }

        String memberStatus =
                Boolean.FALSE.equals(
                        classEntity.getRequireApproval()
                )
                        ? "ACTIVE"
                        : "PENDING";

        ClassMember classMember = ClassMember.builder()
                .classEntity(classEntity)
                .user(currentUser)
                .role(invitation.getRole())
                .status(memberStatus)
                .build();

        classMemberRepository.save(classMember);

        invitation.setStatus(
                InvitationStatus.ACCEPTED
        );

        invitationRepository.save(invitation);
    }

    private void validateInvitePermission(
            ClassEntity classEntity,
            String requestedRole,
            User currentUser
    ) {
        boolean isSystemAdmin =
                currentUser.getRole() != null
                        && "ROLE_ADMIN".equals(
                        currentUser.getRole().getCode()
                );

        boolean isOwner =
                classEntity.getOwner() != null
                        && classEntity.getOwner().getId()
                        .equals(currentUser.getId());

        if (isSystemAdmin || isOwner) {
            return;
        }

        ClassMember inviterMembership =
                classMemberRepository
                        .findByClassEntity_IdAndUser_Id(
                                classEntity.getId(),
                                currentUser.getId()
                        )
                        .orElseThrow(
                                () -> new AppException(
                                        ErrorCode.CLASS_ACCESS_DENIED
                                )
                        );

        if (!"ACTIVE".equals(inviterMembership.getStatus())) {
            throw new AppException(
                    ErrorCode.CLASS_ACCESS_DENIED
            );
        }

        if ("TEACHER".equals(inviterMembership.getRole())) {
            if (!"STUDENT".equals(requestedRole)) {
                throw new AppException(
                        ErrorCode.CLASS_ACCESS_DENIED
                );
            }

            return;
        }

        boolean isPublicClass =
                classEntity.getVisibility() == null
                        || "PUBLIC".equalsIgnoreCase(
                        classEntity.getVisibility()
                );

        boolean isStudent =
                "STUDENT".equals(inviterMembership.getRole());

        if (isStudent
                && isPublicClass
                && "STUDENT".equals(requestedRole)) {
            return;
        }

        throw new AppException(
                ErrorCode.CLASS_ACCESS_DENIED
        );
    }
}