package com.example.ilms.controller;

import com.example.ilms.constant.SuccessCode;
import com.example.ilms.dto.classDTO.AssignCourseToClassRequestDTO;
import com.example.ilms.dto.classDTO.BatchAssignCoursesRequestDTO;
import com.example.ilms.dto.classDTO.ClassCourseResponseDTO;
import com.example.ilms.dto.common.BatchOperationResponseDTO;
import com.example.ilms.dto.common.PageResponse;
import com.example.ilms.dto.response.ApiResponse;
import com.example.ilms.entity.User;
import com.example.ilms.service.ClassCourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/classes/{classId}/courses")
@RequiredArgsConstructor
public class ClassCourseController {

    private final ClassCourseService classCourseService;

//    @GetMapping
//    public ApiResponse<PageResponse<ClassCourseResponseDTO>> getCoursesByClass(
//            @PathVariable UUID classId,
//            @RequestParam(required = false) String status,
//            @RequestParam(required = false) String semester,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @AuthenticationPrincipal User currentUser) {
//
//        return ApiResponse.of(
//                SuccessCode.GET_CLASS_COURSES_SUCCESS,
//                classCourseService.getCoursesByClass(classId, status, semester, page, size, currentUser));
//    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClassCourseResponseDTO>> assignCourse(
            @PathVariable UUID classId,
            @RequestBody @Valid AssignCourseToClassRequestDTO request) {

        ClassCourseResponseDTO result = classCourseService.assignCourse(classId, request);
        return ResponseEntity
                .status(SuccessCode.ASSIGN_COURSE_TO_CLASS_SUCCESS.getStatus())
                .body(ApiResponse.of(SuccessCode.ASSIGN_COURSE_TO_CLASS_SUCCESS, result));
    }

//    @PostMapping("/batch")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<BatchOperationResponseDTO>> batchAssignCourses(
//            @PathVariable UUID classId,
//            @RequestBody @Valid BatchAssignCoursesRequestDTO request) {
//
//        BatchOperationResponseDTO result = classCourseService.batchAssignCourses(classId, request);
//        return ResponseEntity
//                .status(SuccessCode.BATCH_ASSIGN_COURSES_SUCCESS.getStatus())
//                .body(ApiResponse.of(SuccessCode.BATCH_ASSIGN_COURSES_SUCCESS, result));
//    }
}