package com.example.ilms.controller;

import com.example.ilms.constant.SuccessCode;
import com.example.ilms.dto.common.PageResponse;
import com.example.ilms.dto.course.CourseCreateRequestDTO;
import com.example.ilms.dto.course.CourseResponseDTO;
import com.example.ilms.dto.response.ApiResponse;
import com.example.ilms.entity.User;
import com.example.ilms.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CourseResponseDTO> createCourse(
            @RequestBody @Valid CourseCreateRequestDTO requestDTO,
            @AuthenticationPrincipal User currentUser) {

        courseService.createCourse(requestDTO, currentUser);
        return ApiResponse.of(
                SuccessCode.CREATE_COURSE_SUCCESS,
                null
        );
    }

    @GetMapping
    public ApiResponse<PageResponse<CourseResponseDTO>> getCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false, defaultValue = "false") boolean isArchived,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {

        PageResponse<CourseResponseDTO> result = courseService.getCourses(
                search, status, isArchived, page, size, currentUser);

        return ApiResponse.of(SuccessCode.GET_COURSES_SUCCESS, result);
    }

    @GetMapping("/{id}")
    public ApiResponse<CourseResponseDTO> getCourseById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {

        return ApiResponse.of(
                SuccessCode.GET_COURSE_SUCCESS,
                courseService.getCourseById(id, currentUser)
        );
    }
}