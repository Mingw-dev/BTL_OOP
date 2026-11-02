package com.example.ilms.service;

import com.example.ilms.entity.ClassMember;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.ClassMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnnouncementAccessService {
    private final ClassMemberRepository classMemberRepository;

    public ClassMember getActiveMember(UUID classId, User user) {
        if (user == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        ClassMember member = classMemberRepository.findByClassEntity_IdAndUser_Id(classId, user.getId()).orElseThrow(
                () -> new AppException(ErrorCode.CLASS_ACCESS_DENIED)
        );
        if(!"ACTIVE".equals(member.getStatus())) {
            throw new AppException(ErrorCode.CLASS_ACCESS_DENIED);
        }

        return member;
    }

    public void requiredRoleMember(UUID classId, User user) {
        ClassMember member = getActiveMember(classId, user);
        if (!"ADMIN".equals(member.getRole()) && !"TEACHER".equals(member.getRole())) {
            throw new AppException(ErrorCode.CLASS_ACCESS_DENIED);
        }
    }
    @Transactional(
            readOnly = true,
            propagation = Propagation.REQUIRES_NEW
    )
    public boolean isActiveMember(UUID classId, UUID userId) {
        return classMemberRepository
                .findByClassEntity_IdAndUser_Id(classId, userId)
                .map(member -> "ACTIVE".equals(member.getStatus()))
                .orElse(false);
    }
}
