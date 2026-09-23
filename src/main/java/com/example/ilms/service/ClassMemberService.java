package com.example.ilms.service;

import com.example.ilms.dto.classDTO.ClassMemberResponseDTO;
import com.example.ilms.dto.classDTO.JoinByCodeRequestDTO;
import com.example.ilms.dto.common.PageResponse;
import com.example.ilms.dto.user.UserResponseDTO;
import com.example.ilms.entity.ClassEntity;
import com.example.ilms.entity.ClassMember;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.ClassMemberRepository;
import com.example.ilms.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassMemberService {

    private final ClassMemberRepository classMemberRepository;
    private final ClassRepository classRepository;

    @Transactional(readOnly = true)
    public PageResponse<ClassMemberResponseDTO> getMembers(
            UUID classId,
            String role,
            String status,
            String search,
            int page,
            int size,
            User currentUser
    ) {
        validateViewPermission(classId, currentUser);

        Pageable pageable = PageRequest.of(page, size);

        Page<ClassMember> memberPage = classMemberRepository.findMembers(
                classId,
                role,
                status,
                search,
                pageable
        );

        List<ClassMemberResponseDTO> items = memberPage.getContent()
                .stream()
                .map(this::toResponseDTO)
                .toList();

        return PageResponse.<ClassMemberResponseDTO>builder()
                .items(items)
                .pageNumber(memberPage.getNumber())
                .pageSize(memberPage.getSize())
                .totalElements(memberPage.getTotalElements())
                .totalPages(memberPage.getTotalPages())
                .isLast(memberPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public ClassMemberResponseDTO getMember(
            UUID classId,
            UUID userId,
            User currentUser
    ) {
        validateViewPermission(classId, currentUser);

        ClassMember member = classMemberRepository
                .findByClassEntity_IdAndUser_Id(classId, userId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.CLASS_MEMBER_NOT_FOUND)
                );

        return toResponseDTO(member);
    }

    @Transactional
    public ClassMemberResponseDTO joinByCode(
            JoinByCodeRequestDTO request,
            User currentUser
    ) {
        ClassEntity classEntity = classRepository
                .findByClassCode(request.getClassCode())
                .orElseThrow(() ->
                        new AppException(ErrorCode.CLASS_CODE_NOT_FOUND)
                );

        if (Boolean.FALSE.equals(classEntity.getIsCodeActive())) {
            throw new AppException(ErrorCode.CLASS_CODE_INACTIVE);
        }

        boolean alreadyMember = classMemberRepository
                .findByClassEntity_IdAndUser_Id(
                        classEntity.getId(),
                        currentUser.getId()
                )
                .isPresent();

        if (alreadyMember) {
            throw new AppException(ErrorCode.ALREADY_CLASS_MEMBER);
        }

        String memberStatus =
                Boolean.FALSE.equals(classEntity.getRequireApproval())
                        ? "ACTIVE"
                        : "PENDING";

        ClassMember member = ClassMember.builder()
                .user(currentUser)
                .classEntity(classEntity)
                .role("STUDENT")
                .status(memberStatus)
                .build();

        member = classMemberRepository.save(member);

        return toResponseDTO(member);
    }

    @Transactional
    public ClassMemberResponseDTO approveMember(
            UUID classId,
            UUID userId,
            User currentUser
    ) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.CLASS_NOT_FOUND)
                );

        boolean isOwner = classEntity.getOwner() != null
                && classEntity.getOwner().getId().equals(currentUser.getId());

        boolean isSystemAdmin = currentUser.getRole() != null
                && "ROLE_ADMIN".equals(currentUser.getRole().getCode());

        boolean isTeacher = classMemberRepository
                .findByClassEntity_IdAndUser_Id(
                        classId,
                        currentUser.getId()
                )
                .map(member ->
                        "TEACHER".equals(member.getRole())
                                && "ACTIVE".equals(member.getStatus())
                )
                .orElse(false);

        if (!isOwner && !isTeacher && !isSystemAdmin) {
            throw new AppException(ErrorCode.CLASS_ACCESS_DENIED);
        }

        ClassMember member = classMemberRepository
                .findByClassEntity_IdAndUser_Id(classId, userId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.CLASS_MEMBER_NOT_FOUND)
                );

        if (!"PENDING".equals(member.getStatus())) {
            throw new AppException(ErrorCode.CLASS_MEMBER_NOT_PENDING);
        }

        member.setStatus("ACTIVE");

        member = classMemberRepository.save(member);

        return toResponseDTO(member);
    }

    @Transactional
    public ClassMemberResponseDTO changeMemberRole(
            UUID classId,
            UUID userId,
            String newRole,
            User currentUser
    ) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.CLASS_NOT_FOUND)
                );

        boolean isOwner = classEntity.getOwner() != null
                && classEntity.getOwner().getId().equals(currentUser.getId());

        boolean isSystemAdmin = currentUser.getRole() != null
                && "ROLE_ADMIN".equals(currentUser.getRole().getCode());

        if (!isOwner && !isSystemAdmin) {
            throw new AppException(ErrorCode.CLASS_ACCESS_DENIED);
        }

        ClassMember member = classMemberRepository
                .findByClassEntity_IdAndUser_Id(classId, userId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.CLASS_MEMBER_NOT_FOUND)
                );

        if (classEntity.getOwner() != null
                && classEntity.getOwner().getId().equals(userId)) {
            throw new AppException(
                    ErrorCode.CLASS_OWNER_ROLE_CANNOT_CHANGE
            );
        }

        member.setRole(newRole);

        member = classMemberRepository.save(member);

        return toResponseDTO(member);
    }

    @Transactional
    public void removeMember(
            UUID classId,
            UUID userId,
            User currentUser
    ) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.CLASS_NOT_FOUND)
                );

        boolean isOwner = classEntity.getOwner() != null
                && classEntity.getOwner().getId().equals(currentUser.getId());

        boolean isSystemAdmin = currentUser.getRole() != null
                && "ROLE_ADMIN".equals(currentUser.getRole().getCode());

        boolean isTeacher = classMemberRepository
                .findByClassEntity_IdAndUser_Id(
                        classId,
                        currentUser.getId()
                )
                .map(member ->
                        "TEACHER".equals(member.getRole())
                                && "ACTIVE".equals(member.getStatus())
                )
                .orElse(false);

        if (!isOwner && !isTeacher && !isSystemAdmin) {
            throw new AppException(ErrorCode.CLASS_ACCESS_DENIED);
        }

        ClassMember member = classMemberRepository
                .findByClassEntity_IdAndUser_Id(classId, userId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.CLASS_MEMBER_NOT_FOUND)
                );

        if (classEntity.getOwner() != null
                && classEntity.getOwner().getId().equals(userId)) {
            throw new AppException(
                    ErrorCode.CLASS_OWNER_CANNOT_BE_REMOVED
            );
        }

        if (isTeacher
                && !isOwner
                && !isSystemAdmin
                && !"STUDENT".equals(member.getRole())) {
            throw new AppException(ErrorCode.CLASS_ACCESS_DENIED);
        }

        classMemberRepository.delete(member);
    }

    @Transactional
    public void leaveClass(
            UUID classId,
            User currentUser
    ) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.CLASS_NOT_FOUND)
                );

        ClassMember member = classMemberRepository
                .findByClassEntity_IdAndUser_Id(
                        classId,
                        currentUser.getId()
                )
                .orElseThrow(() ->
                        new AppException(ErrorCode.CLASS_MEMBER_NOT_FOUND)
                );

        boolean isOwner = classEntity.getOwner() != null
                && classEntity.getOwner().getId().equals(currentUser.getId());

        boolean isTeacher = "TEACHER".equals(member.getRole());

        if (isOwner || isTeacher) {
            throw new AppException(
                    ErrorCode.CLASS_MEMBER_CANNOT_LEAVE
            );
        }

        classMemberRepository.delete(member);
    }

    private void validateViewPermission(
            UUID classId,
            User currentUser
    ) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.CLASS_NOT_FOUND)
                );

        if (currentUser == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        boolean isSystemAdmin =
                currentUser.getRole() != null
                        && "ROLE_ADMIN".equals(
                        currentUser.getRole().getCode()
                );

        boolean isOwner =
                classEntity.getOwner() != null
                        && classEntity.getOwner().getId()
                        .equals(currentUser.getId());

        boolean isActiveMember =
                classMemberRepository
                        .findByClassEntity_IdAndUser_Id(
                                classId,
                                currentUser.getId()
                        )
                        .map(member ->
                                "ACTIVE".equals(member.getStatus())
                        )
                        .orElse(false);

        if (!isSystemAdmin && !isOwner && !isActiveMember) {
            throw new AppException(
                    ErrorCode.CLASS_ACCESS_DENIED
            );
        }
    }

    private ClassMemberResponseDTO toResponseDTO(
            ClassMember member
    ) {
        User user = member.getUser();

        UserResponseDTO userResponse = UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .build();

        return ClassMemberResponseDTO.builder()
                .user(userResponse)
                .joinedAt(member.getJoinedAt())
                .role(member.getRole())
                .status(member.getStatus())
                .build();
    }
}