package com.example.ilms.service;

import com.example.ilms.dto.auth.*;
import com.example.ilms.dto.user.UserResponseDTO;
import com.example.ilms.entity.Role;
import com.example.ilms.entity.User;
import com.example.ilms.entity.VerificationToken;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.RoleRepository;
import com.example.ilms.repository.UserRepository;
import com.example.ilms.repository.VerificationTokenRepository;
import com.example.ilms.security.TokenProvider;
import com.example.ilms.type.TokenType;
import com.example.ilms.type.TokenStatus;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final TokenProvider tokenProvider;

    private final EmailService emailService;

    @Value("${app.frontend.reset-password-url:http://localhost:3000/reset-password}")
    private String resetPasswordBaseUrl;

    public AuthResult register(RegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        Role defaultRole = roleRepository.findByCode("ROLE_USER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        User user = modelMapper.map(dto, User.class);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(defaultRole);

        return buildAuthResult(userRepository.save(user));
    }

    public AuthResult login(LoginRequestDTO dto) {
        String email = dto.getEmail().trim();
        String password = dto.getPassword();

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (BadCredentialsException ex) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        } catch (DisabledException ex) {
            throw new AppException(ErrorCode.USER_DISABLED);
        }
        User user = (User) authentication.getPrincipal();
        return buildAuthResult(user);
    }

    public AuthResult logout() {
        ResponseCookie cleanAccessCookie = tokenProvider.getCleanAccessTokenCookie();
        ResponseCookie cleanRefreshCookie = tokenProvider.getCleanRefreshTokenCookie();

        return AuthResult.builder()
                .accessTokenCookie(cleanAccessCookie)
                .refreshTokenCookie(cleanRefreshCookie)
                .user(null)
                .build();
    }

    public AuthResult refreshFromRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        String idStr = tokenProvider.extractId(refreshToken);

        User user = userRepository.findById(UUID.fromString(idStr))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!tokenProvider.validateToken(refreshToken, user)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        return buildAuthResult(user);
    }
    @Transactional
    public void sendEmailForgotPassword(ForgotPasswordRequestDTO requestDTO) {
        int time = 15;
        String email = requestDTO.getEmail();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();

        verificationTokenRepository.revokeAllPendingTokens(email, TokenType.RESET_PASSWORD);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(token)
                .email(user.getEmail())
                .type(TokenType.RESET_PASSWORD)
                .status(TokenStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusMinutes(time))
                .build();

        verificationTokenRepository.save(verificationToken);

        String resetLink = resetPasswordBaseUrl + "?token=" + token;
        emailService.sendForgotPasswordEmail(email, resetLink);
    }

    @Transactional(readOnly = true)
    public void verifyResetToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository
                .findByTokenAndTypeAndStatus(token, TokenType.RESET_PASSWORD, TokenStatus.PENDING)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_OR_EXPIRED_TOKEN));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO requestDTO) {
        String token = requestDTO.getToken();
        String newPassword = requestDTO.getNewPassword();

        VerificationToken verificationToken = verificationTokenRepository
                .findByTokenAndTypeAndStatus(token, TokenType.RESET_PASSWORD, TokenStatus.PENDING)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_OR_EXPIRED_TOKEN));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            verificationToken.setStatus(TokenStatus.EXPIRED);
            verificationTokenRepository.save(verificationToken);
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }

        User user = userRepository.findByEmail(verificationToken.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_CANNOT_BE_OLD_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        verificationToken.setStatus(TokenStatus.VERIFIED);
        verificationTokenRepository.save(verificationToken);

        emailService.sendPasswordChangedAlert(user.getEmail());
    }

    public void changePassword(ChangePasswordRequestDTO requestDTO) {
        String oldPassword = requestDTO.getOldPassword();
        String newPassword = requestDTO.getNewPassword();
        UUID id = requestDTO.getId();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }
        if (oldPassword.equals(newPassword)) {
            throw new AppException(ErrorCode.PASSWORD_CANNOT_BE_OLD_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        emailService.sendPasswordChangedAlert(user.getEmail());
    }

    private AuthResult buildAuthResult(User user) {
        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);

        return AuthResult.builder()
                .user(modelMapper.map(user, UserResponseDTO.class))
                .accessTokenCookie(tokenProvider.createAccessTokenCookie(accessToken))
                .refreshTokenCookie(tokenProvider.createRefreshTokenCookie(refreshToken))
                .build();
    }
}