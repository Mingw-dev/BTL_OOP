package com.example.ilms.controller;

import com.example.ilms.constant.SuccessCode;
import com.example.ilms.dto.response.ApiResponse;
import com.example.ilms.dto.user.*;
import com.example.ilms.entity.User;
import com.example.ilms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/user")
    UserResponseDTO createUser(@RequestBody @Valid UserCreateRequest response) {

        return userService.createUser(response);
    }

}
