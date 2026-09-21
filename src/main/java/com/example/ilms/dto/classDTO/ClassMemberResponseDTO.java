package com.example.ilms.dto.classDTO;

import com.example.ilms.dto.user.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassMemberResponseDTO {

    private UserResponseDTO user;

    private LocalDateTime joinedAt;

    private String role;

    private String status;
}