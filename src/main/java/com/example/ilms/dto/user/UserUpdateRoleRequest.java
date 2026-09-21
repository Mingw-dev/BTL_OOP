package com.example.ilms.dto.user;

import com.example.ilms.entity.Role;
import lombok.Data;

@Data
public class UserUpdateRoleRequest {
    private String role;
}