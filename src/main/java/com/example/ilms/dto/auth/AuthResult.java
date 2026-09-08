package com.example.ilms.dto.auth;

import com.example.ilms.constant.SuccessCode;
import com.example.ilms.dto.user.UserResponseDTO;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseCookie;

@Getter
@Builder
public class AuthResult {
    private SuccessCode successCode;
    private UserResponseDTO user;
    private ResponseCookie accessTokenCookie;
    private ResponseCookie refreshTokenCookie;
}