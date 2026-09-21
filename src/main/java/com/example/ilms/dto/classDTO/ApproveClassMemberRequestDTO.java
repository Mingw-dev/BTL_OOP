package com.example.ilms.dto.classDTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveClassMemberRequestDTO {

    @NotNull(message = "User ID không được để trống")
    private UUID userId;
}