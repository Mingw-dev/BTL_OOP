package com.example.ilms.controller;

import com.example.ilms.dto.user.*;
import com.example.ilms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/user")
    UserResponseDTO createUser(@RequestBody @Valid UserCreateRequestDTO response) {

        return userService.createUser(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN' , 'TEACHER')")
    @GetMapping("/{id}")
    UserResponseDTO getUser(@PathVariable UUID id) {
        return userService.getUser(id);
    }
}
