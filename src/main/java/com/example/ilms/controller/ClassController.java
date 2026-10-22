package com.example.ilms.controller;

import com.example.ilms.constant.SuccessCode;
import com.example.ilms.dto.classDTO.ClassCreateRequestDTO;
import com.example.ilms.dto.classDTO.ClassResponseDTO;
import com.example.ilms.dto.common.PageResponse;
import com.example.ilms.dto.response.ApiResponse;
import com.example.ilms.entity.User;
import com.example.ilms.repository.UserRepository;
import com.example.ilms.service.ClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;
    private final UserRepository userRepository;

    @PostMapping
    public ApiResponse<ClassResponseDTO> createClass(
            @RequestBody @Valid ClassCreateRequestDTO request,
            @AuthenticationPrincipal User currentUser){


        ClassResponseDTO createdClass = classService.createClass(request,currentUser);
        return ApiResponse.of(SuccessCode.CLASS_CREATED, createdClass);

    }

    @GetMapping("/listClasses")
    public ApiResponse<PageResponse<ClassResponseDTO>> getClasses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String visibility,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {

        PageResponse<ClassResponseDTO> result = classService.getClasses(search, visibility, page, size, currentUser);

        return ApiResponse.of(SuccessCode.GET_CLASSES_SUCCESS, result);
    }

    @GetMapping("/{classId}")
    public ApiResponse<ClassResponseDTO> getClassDetail(
            @PathVariable UUID classId,
            @AuthenticationPrincipal User currentUser) {

        ClassResponseDTO result = classService.getClassById(classId, currentUser);

        return ApiResponse.of(SuccessCode.GET_CLASS_SUCCESS, result);
    }

    @PatchMapping("/{classId}/status")
    public ApiResponse<Void> toggleClassStatus(
            @PathVariable UUID classId,
            @RequestParam boolean isClosed,
            @AuthenticationPrincipal User currentUser) {

        classService.updateClassStatus(classId, isClosed, currentUser);

        return ApiResponse.of(SuccessCode.UPDATE_CLASS_STATUS_SUCCESS, null);
    }
}
