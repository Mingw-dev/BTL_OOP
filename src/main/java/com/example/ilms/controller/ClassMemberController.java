package com.example.ilms.controller;

import com.example.ilms.constant.SuccessCode;
import com.example.ilms.dto.classDTO.AcceptInvitationRequestDTO;
import com.example.ilms.dto.classDTO.ApproveClassMemberRequestDTO;
import com.example.ilms.dto.classDTO.ChangeClassMemberRoleRequestDTO;
import com.example.ilms.dto.classDTO.ClassMemberResponseDTO;
import com.example.ilms.dto.classDTO.InviteByEmailRequestDTO;
import com.example.ilms.dto.classDTO.JoinByCodeRequestDTO;
import com.example.ilms.dto.common.PageResponse;
import com.example.ilms.dto.response.ApiResponse;
import com.example.ilms.entity.User;
import com.example.ilms.service.ClassMemberService;
import com.example.ilms.service.InvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
public class ClassMemberController {

    private final ClassMemberService classMemberService;
    private final InvitationService invitationService;

    @GetMapping("/{classId}/members")
    public ApiResponse<PageResponse<ClassMemberResponseDTO>> getMembers(
            @PathVariable UUID classId,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser
    ) {
        PageResponse<ClassMemberResponseDTO> members =
                classMemberService.getMembers(
                        classId,
                        role,
                        status,
                        search,
                        page,
                        size,
                        currentUser
                );

        return ApiResponse.of(
                SuccessCode.GET_CLASS_MEMBERS_SUCCESS,
                members
        );
    }

    @GetMapping("/{classId}/members/{userId}")
    public ApiResponse<ClassMemberResponseDTO> getMember(
            @PathVariable UUID classId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal User currentUser
    ) {
        ClassMemberResponseDTO member =
                classMemberService.getMember(
                        classId,
                        userId,
                        currentUser
                );

        return ApiResponse.of(
                SuccessCode.GET_CLASS_MEMBER_SUCCESS,
                member
        );
    }

    @PostMapping("/join-by-code")
    public ApiResponse<ClassMemberResponseDTO> joinByCode(
            @RequestBody @Valid JoinByCodeRequestDTO request,
            @AuthenticationPrincipal User currentUser
    ) {
        ClassMemberResponseDTO member =
                classMemberService.joinByCode(
                        request,
                        currentUser
                );

        return ApiResponse.of(
                SuccessCode.ENROLL_CLASS_SUCCESS,
                member
        );
    }

    @PostMapping("/{classId}/members/invite-by-email")
    public ApiResponse<Void> inviteByEmail(
            @PathVariable UUID classId,
            @RequestBody @Valid InviteByEmailRequestDTO request,
            @AuthenticationPrincipal User currentUser
    ) {
        invitationService.inviteByEmail(
                classId,
                request,
                currentUser
        );

        return ApiResponse.of(
                SuccessCode.INVITE_CLASS_MEMBER_SUCCESS,
                null
        );
    }

    @PostMapping("/invitations/accept")
    public ApiResponse<Void> acceptInvitation(
            @RequestBody @Valid AcceptInvitationRequestDTO request,
            @AuthenticationPrincipal User currentUser
    ) {
        invitationService.acceptInvitation(
                request.getToken(),
                currentUser
        );

        return ApiResponse.of(
                SuccessCode.ACCEPT_CLASS_INVITATION_SUCCESS,
                null
        );
    }

    @PostMapping("/{classId}/approve")
    public ApiResponse<ClassMemberResponseDTO> approveMember(
            @PathVariable UUID classId,
            @RequestBody @Valid ApproveClassMemberRequestDTO request,
            @AuthenticationPrincipal User currentUser
    ) {
        ClassMemberResponseDTO member =
                classMemberService.approveMember(
                        classId,
                        request.getUserId(),
                        currentUser
                );

        return ApiResponse.of(
                SuccessCode.APPROVE_CLASS_MEMBER_SUCCESS,
                member
        );
    }

    @PatchMapping("/{classId}/members/{userId}/role")
    public ApiResponse<ClassMemberResponseDTO> changeMemberRole(
            @PathVariable UUID classId,
            @PathVariable UUID userId,
            @RequestBody @Valid ChangeClassMemberRoleRequestDTO request,
            @AuthenticationPrincipal User currentUser
    ) {
        ClassMemberResponseDTO member =
                classMemberService.changeMemberRole(
                        classId,
                        userId,
                        request.getRole(),
                        currentUser
                );

        return ApiResponse.of(
                SuccessCode.CHANGE_CLASS_MEMBER_ROLE_SUCCESS,
                member
        );
    }

    @DeleteMapping("/{classId}/members/{userId}")
    public ApiResponse<Void> removeMember(
            @PathVariable UUID classId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal User currentUser
    ) {
        classMemberService.removeMember(
                classId,
                userId,
                currentUser
        );

        return ApiResponse.of(
                SuccessCode.REMOVE_CLASS_MEMBER_SUCCESS,
                null
        );
    }

    @PostMapping("/{classId}/leave")
    public ApiResponse<Void> leaveClass(
            @PathVariable UUID classId,
            @AuthenticationPrincipal User currentUser
    ) {
        classMemberService.leaveClass(
                classId,
                currentUser
        );

        return ApiResponse.of(
                SuccessCode.LEAVE_CLASS_SUCCESS,
                null
        );
    }
}