package com.example.ilms.dto.user;

import com.example.ilms.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private UUID id;
    private String roleName;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
}
