package com.example.ilms.filter;

import com.example.ilms.dto.auth.AuthResult;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.UserRepository;
import com.example.ilms.security.TokenProvider;
import com.example.ilms.service.AuthService;
import com.example.ilms.util.CookieUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final HandlerExceptionResolver resolver;
    private final UserRepository userRepository;
    private final AuthService authService;

    public JwtAuthenticationFilter(
            TokenProvider tokenProvider,
            UserRepository userRepository,
            @Lazy AuthService authService,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver
    ) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.authService = authService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String accessToken = CookieUtils.getCookieValue(request, TokenProvider.ACCESS_TOKEN_COOKIE).orElse(null);
        final String refreshToken = CookieUtils.getCookieValue(request, TokenProvider.REFRESH_TOKEN_COOKIE).orElse(null);


        if (accessToken == null && refreshToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            boolean authenticated = false;

            if (accessToken != null) {
                try {
                    String idStr = tokenProvider.extractId(accessToken);
                    if (idStr != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        User user = userRepository.findById(UUID.fromString(idStr))
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                        if (tokenProvider.validateToken(accessToken, user)) {
                            setAuthentication(user, request);
                            authenticated = true;
                        }
                    }
                } catch (ExpiredJwtException ex) {
                    log.info("Access token đã hết hạn, chuyển sang kiểm tra refresh token...");
                }
            }

            if (!authenticated && refreshToken != null) {
                AuthResult authResult = authService.refreshFromRefreshToken(refreshToken);

                response.addHeader(HttpHeaders.SET_COOKIE, authResult.getAccessTokenCookie().toString());
                response.addHeader(HttpHeaders.SET_COOKIE, authResult.getRefreshTokenCookie().toString());

                User user = userRepository.findById(authResult.getUser().getId())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
                setAuthentication(user, request);
                authenticated = true;
            }

            if (!authenticated) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }

            filterChain.doFilter(request, response);

        } catch (AppException ex) {
            SecurityContextHolder.clearContext();
            resolver.resolveException(request, response, null, ex);
        } catch (JwtException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();
            resolver.resolveException(request, response, null, new AppException(ErrorCode.INVALID_TOKEN));
        } catch (Exception ex) {
            log.error("Lỗi xác thực hệ thống: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
            resolver.resolveException(request, response, null, new AppException(ErrorCode.UNAUTHORIZED));
        }
    }

    private void setAuthentication(User user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}