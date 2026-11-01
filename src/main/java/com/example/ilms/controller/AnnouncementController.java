package com.example.ilms.controller;

import com.example.ilms.constant.SuccessCode;
import com.example.ilms.dto.announcement.AnnouncementCreateRequestDTO;
import com.example.ilms.dto.announcement.AnnouncementResponseDTO;
import com.example.ilms.dto.common.PageResponse;
import com.example.ilms.dto.response.ApiResponse;
import com.example.ilms.entity.User;
import com.example.ilms.service.ClassAnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/classes/class/{classId}/Announcements")
public class AnnouncementController {
    private final ClassAnnouncementService classAnnouncementService;

    @PostMapping
    public ApiResponse<Void> createAnnouncement(@RequestBody @Valid AnnouncementCreateRequestDTO requestDTO, @PathVariable UUID classId,
                                                @AuthenticationPrincipal User user) {
        classAnnouncementService.createAnnouncement(requestDTO, classId, user);
        return ApiResponse.of(SuccessCode.CREATE_ANNOUCEMENT_SUCCESS, null);
    }

    @GetMapping
    public ApiResponse<PageResponse<AnnouncementResponseDTO>> getAnnouncements(
            @PathVariable UUID classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user
    ) {
        PageResponse<AnnouncementResponseDTO> result = classAnnouncementService.getAnouncements(classId, page, size, user);
        return ApiResponse.of(SuccessCode.GET_ANNOUCEMENTS_SUCESS, result);
    }

    @PatchMapping("/{announcementId}/pin")
    public ApiResponse<Void> togglePinStatus(
            @PathVariable UUID classId,
            @PathVariable("announcementId") UUID announcementId,
            @RequestParam boolean pin,
            @AuthenticationPrincipal User currentUser
    ) {
        classAnnouncementService.TogglePinStatus(classId, announcementId, pin, currentUser);

        return ApiResponse.of(
                SuccessCode.UPDATE_ANNOUNCEMENT_SUCCESS, null
        );
    }

}
