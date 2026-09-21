package com.example.ilms.dto.classDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcceptInvitationRequestDTO {

    @NotBlank(message = "Token lời mời không được để trống")
    private String token;
}
