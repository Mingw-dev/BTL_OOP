package com.example.ilms.dto.classDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeClassMemberRoleRequestDTO {

    @NotBlank(message = "Vai trò không được để trống")
    @Pattern(
            regexp = "^(STUDENT|TEACHER)$",
            message = "Vai trò chỉ được phép là STUDENT hoặc TEACHER"
    )
    private String role;
}