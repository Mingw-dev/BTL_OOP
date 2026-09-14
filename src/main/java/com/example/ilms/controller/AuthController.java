package com.example.ilms.controller;

import com.example.ilms.constant.SuccessCode;
import com.example.ilms.dto.auth.*;
import com.example.ilms.dto.response.ApiResponse;
import com.example.ilms.dto.user.UserResponseDTO;
import com.example.ilms.entity.User;
import com.example.ilms.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final ModelMapper modelMapper;

    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO request) {
        AuthResult result = authService.register(request);
        return toAuthResponse(result, SuccessCode.REGISTER_SUCCESS);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResult result = authService.login(request);
        return toAuthResponse(result, SuccessCode.LOGIN_SUCCESS);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<UserResponseDTO>> logout() {
        AuthResult result = authService.logout();
        return toAuthResponse(result, SuccessCode.LOGOUT_SUCCESS);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getMe(
            @AuthenticationPrincipal User currentUser
    ) {
        UserResponseDTO responseDTO = modelMapper.map(currentUser, UserResponseDTO.class);
        return ResponseEntity.ok(ApiResponse.of(SuccessCode.GET_USER_SUCCESS, responseDTO));
    }

    @GetMapping("/send-email-forgot-password")
    public ResponseEntity<ApiResponse<Void>> sendEmailForgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO requestDTO) {
        authService.sendEmailForgotPassword(requestDTO);
        return ResponseEntity.ok(ApiResponse.of(SuccessCode.GET_USER_SUCCESS, null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        authService.sendEmailForgotPassword(request);
        return ResponseEntity.ok(ApiResponse.of(SuccessCode.SEND_EMAIL_RESET_PASSWORD_SUCCESS, null));
    }

    @GetMapping("/verify-reset-token")
    public ResponseEntity<ApiResponse<Void>> verifyToken(@RequestParam("token") String token) {
        authService.verifyResetToken(token);
        return ResponseEntity.ok(ApiResponse.of(SuccessCode.VERIFY_TOKEN_RESET_PASSWORD_SUCCESS, null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.of(SuccessCode.CHANGED_PASSWORD_SUCCESS, null));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@AuthenticationPrincipal User currentUser, @Valid @RequestBody ChangePasswordRequestDTO request) {
        request.setId(currentUser.getId());
        authService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.of(SuccessCode.CHANGED_PASSWORD_SUCCESS, null));
    }


    private ResponseEntity<ApiResponse<UserResponseDTO>> toAuthResponse(AuthResult result, SuccessCode successCode) {
        return ResponseEntity.status(successCode.getStatus())
                .header(HttpHeaders.SET_COOKIE, result.getAccessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, result.getRefreshTokenCookie().toString())
                .body(ApiResponse.of(successCode, result.getUser()));
    }
}