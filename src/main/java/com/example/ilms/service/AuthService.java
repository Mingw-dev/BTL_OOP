package com.example.ilms.service;

import com.example.ilms.dto.auth.AuthResult;
import com.example.ilms.dto.auth.ForgotPasswordRequestDTO;
import com.example.ilms.dto.auth.LoginRequestDTO;
import com.example.ilms.dto.auth.RegisterRequestDTO;
import com.example.ilms.dto.user.UserResponseDTO;
import com.example.ilms.entity.Role;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.RoleRepository;
import com.example.ilms.repository.UserRepository;
import com.example.ilms.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final TokenProvider tokenProvider;

    private final EmailService emailService;

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