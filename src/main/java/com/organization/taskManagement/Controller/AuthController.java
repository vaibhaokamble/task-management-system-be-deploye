package com.organization.taskManagement.Controller;

import com.organization.taskManagement.DTO.Request.*;
import com.organization.taskManagement.DTO.Response.ApiResponse;
import com.organization.taskManagement.DTO.Response.AuthResponse;
import com.organization.taskManagement.DTO.Response.EmployeeRegistrationResponse;
import com.organization.taskManagement.Services.AuthService;
import com.organization.taskManagement.Services.EmailVerificationService;
import com.organization.taskManagement.Repository.RefreshTokenRepository;
import com.organization.taskManagement.Model.RefreshToken;
import com.organization.taskManagement.security.JwtService;
import com.organization.taskManagement.security.RefreshTokenService;
import com.organization.taskManagement.security.UserInfoService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserInfoService userInfoService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<EmployeeRegistrationResponse>> register(@Valid @RequestBody EmployeeRegistrationRequest request) {
        EmployeeRegistrationResponse response = authService.registerEmployee(request);
        return ResponseEntity.ok(ApiResponse.success("Employee Registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<RefreshTokenRequest> refreshToken(@RequestBody RefreshTokenRequest request) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(request.getRefreshToken());
        if (tokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        RefreshToken token = refreshTokenService.verifyExpiration(tokenOpt.get());
        UserDetails userDetails = userInfoService.loadUserByUsername(token.getEmployeeId());
        String newAccessToken = jwtService.generateToken(token.getEmployeeId(), userDetails);

        RefreshTokenRequest response = new RefreshTokenRequest();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(token.getToken());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
        if (request == null || request.getRefreshToken() == null || request.getRefreshToken().isEmpty()) {
            throw new IllegalArgumentException("Refresh token is required");
        }

        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(request.getRefreshToken());

        if (tokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        RefreshToken refreshToken = tokenOpt.get();
        String authenticatedUser = getAuthenticatedEmployeeId();
        if (authenticatedUser != null && !authenticatedUser.equals(refreshToken.getEmployeeId())) {
            throw new RuntimeException("Cannot logout another user's session");
        }

        refreshTokenRepository.delete(refreshToken);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logout successful");
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody EmailRequestDto request) {
        try {
            return ResponseEntity.ok(emailVerificationService.sendOtp(request.getEmail()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/register/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerifyRequest request) {
        return ResponseEntity.ok(emailVerificationService.verifyOtp(request.getEmail(), request.getOtp()));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestBody ForgetPasswordRequest request) {
        try {
            String response = emailVerificationService.forgetPassword(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", response,
                    "timestamp", LocalDateTime.now()
            ));
        } catch (RuntimeException e) {
            log.warn("Forget password attempt for non-existent email: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        } catch (MessagingException e) {
            log.error("Error sending forget password email: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("An error occurred while sending the email"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            String response = emailVerificationService.resetPassword(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", response,
                    "timestamp", LocalDateTime.now()
            ));
        } catch (RuntimeException e) {
            log.warn("Reset password attempt with invalid OTP for email: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    private String getAuthenticatedEmployeeId() {
        Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return null;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        error.put("timestamp", LocalDateTime.now());
        return error;
    }
}
