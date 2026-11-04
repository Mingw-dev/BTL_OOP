package com.example.ilms.controller;

import com.example.ilms.constant.SuccessCode;
import com.example.ilms.dto.chapter.ChapterCreateRequestDTO;
import com.example.ilms.dto.chapter.ChapterDetailResponseDTO;
import com.example.ilms.dto.chapter.ChapterResponseDTO;
import com.example.ilms.dto.chapter.ChapterSummaryResponseDTO;
import com.example.ilms.dto.chapter.ChapterUpdateRequestDTO;
import com.example.ilms.dto.response.ApiResponse;
import com.example.ilms.entity.User;
import com.example.ilms.service.ChapterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    // Phân quyền (Admin / Giảng viên chủ môn học) được kiểm tra trong ChapterService
    @PostMapping("/courses/{courseId}/chapters")
    public ResponseEntity<ApiResponse<ChapterResponseDTO>> createChapter(
            @PathVariable UUID courseId,
            @RequestBody @Valid ChapterCreateRequestDTO request,
            @AuthenticationPrincipal User currentUser) {

        ChapterResponseDTO result = chapterService.createChapter(courseId, request, currentUser);

        return ResponseEntity
                .status(SuccessCode.CREATE_CHAPTER_SUCCESS.getStatus())
                .body(ApiResponse.of(SuccessCode.CREATE_CHAPTER_SUCCESS, result));
    }

    @GetMapping("/courses/{courseId}/chapters")
    public ApiResponse<List<ChapterSummaryResponseDTO>> getChaptersByCourse(
            @PathVariable UUID courseId,
            @RequestParam(defaultValue = "false") boolean includeDeleted,
            @AuthenticationPrincipal User currentUser) {

        return ApiResponse.of(
                SuccessCode.GET_CHAPTERS_SUCCESS,
                chapterService.getChaptersByCourse(courseId, includeDeleted, currentUser)
        );
    }

    @GetMapping("/chapters/{chapterId}")
    public ApiResponse<ChapterDetailResponseDTO> getChapterById(
            @PathVariable UUID chapterId,
            @AuthenticationPrincipal User currentUser) {

        return ApiResponse.of(
                SuccessCode.GET_CHAPTER_SUCCESS,
                chapterService.getChapterById(chapterId, currentUser)
        );
    }

    @PutMapping("/chapters/{chapterId}")
    public ApiResponse<ChapterDetailResponseDTO> updateChapter(
            @PathVariable UUID chapterId,
            @RequestBody @Valid ChapterUpdateRequestDTO request,
            @AuthenticationPrincipal User currentUser) {

        return ApiResponse.of(
                SuccessCode.UPDATE_CHAPTER_SUCCESS,
                chapterService.updateChapter(chapterId, request, currentUser)
        );
    }
}