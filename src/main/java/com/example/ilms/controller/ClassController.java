package com.example.ilms.controller;

import com.example.ilms.constant.SuccessCode;
import com.example.ilms.dto.classDTO.ClassCreateRequestDTO;
import com.example.ilms.dto.classDTO.ClassResponseDTO;
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
}
