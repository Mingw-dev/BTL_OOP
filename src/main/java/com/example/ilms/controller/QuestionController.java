package com.example.ilms.controller;

import com.example.ilms.constant.SuccessCode;
import com.example.ilms.dto.response.ApiResponse;
import com.example.ilms.dto.common.PageResponse;
import com.example.ilms.dto.question.QuestionRequestDTO;
import com.example.ilms.dto.question.QuestionResponseDTO;
import com.example.ilms.entity.User;
import com.example.ilms.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
//@PreAuthorize("hasAuthority('ROLE_ADMIN')") // tạm thời chỉ có admin mới dc dùng ngân hàng câu hỏi

public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public ApiResponse<PageResponse<QuestionResponseDTO>> getAllQuestions(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String difficultyLevel,
            @RequestParam(required = false) UUID conceptId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @AuthenticationPrincipal User currentUser) {

        PageResponse<QuestionResponseDTO> response = questionService.getQuestions(
                search, difficultyLevel, conceptId, page, size, sort, currentUser);
        return ApiResponse.of(SuccessCode.GET_QUESTIONS_SUCCESS, response);
    }

    @GetMapping("/{id}")
    public ApiResponse<QuestionResponseDTO> getQuestionById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        return ApiResponse.of(SuccessCode.GET_QUESTION_DETAIL_SUCCESS, questionService.getQuestionById(id, currentUser));
    }

    @PostMapping
    public ApiResponse<QuestionResponseDTO> createQuestion(
            @Valid @RequestBody QuestionRequestDTO request,
            @AuthenticationPrincipal User currentUser) {
        return ApiResponse.of(SuccessCode.CREATE_QUESTION_SUCCESS, questionService.createQuestion(request, currentUser));
    }

    @PutMapping("/{id}")
    public ApiResponse<QuestionResponseDTO> updateQuestion(
            @PathVariable UUID id,
            @Valid @RequestBody QuestionRequestDTO request,
            @AuthenticationPrincipal User currentUser) {
        return ApiResponse.of(SuccessCode.UPDATE_QUESTION_SUCCESS, questionService.updateQuestion(id, request, currentUser));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteQuestion(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        questionService.deleteQuestion(id, currentUser);
        return ApiResponse.of(SuccessCode.DELETE_QUESTION_SUCCESS, null);
    }
}