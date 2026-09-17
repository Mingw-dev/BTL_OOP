package com.example.ilms.controller;

import com.example.ilms.constant.SuccessCode;
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

        requestDTO.setOwnerId(currentUser.getId());

        return ApiResponse.of(
                SuccessCode.CREATE_COURSE_SUCCESS,
                courseService.createCourse(requestDTO)
        );
    }
}