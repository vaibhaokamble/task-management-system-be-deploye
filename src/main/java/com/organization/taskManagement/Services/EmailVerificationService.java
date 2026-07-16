package com.organization.taskManagement.Services;

import com.organization.taskManagement.DTO.Request.ForgetPasswordRequest;
import com.organization.taskManagement.DTO.Request.ResetPasswordRequest;
import com.organization.taskManagement.Model.EmailOtpEntity;
import com.organization.taskManagement.Model.EmployeeRegisterModel;
import com.organization.taskManagement.Repository.EmailOtpRepository;
import com.organization.taskManagement.Repository.EmployeeRegisterRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmailVerificationService {
    private final EmailOtpRepository emailOtpRepository;
    private final EmployeeRegisterRepository employeeRegisterRepository;
    private final MailService mailService;
    private final Set<String> allowedDomains;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int OTP_EXPIRY_SECONDS = 300; // 5 minutes
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public EmailVerificationService(
            EmailOtpRepository emailOtpRepository,
            EmployeeRegisterRepository employeeRegisterRepository,
            MailService mailService,
            @Value("${app.allowed-domains}") String allowedDomains
    ) {
        this.emailOtpRepository = emailOtpRepository;
        this.employeeRegisterRepository = employeeRegisterRepository;
        this.mailService = mailService;
        this.allowedDomains = Arrays.stream(allowedDomains.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    @org.springframework.transaction.annotation.Transactional
    public String sendOtp(String email) throws MessagingException {
        validateDomain(email);

        String otp = generateOtp();

        EmailOtpEntity emailOtpEntity = emailOtpRepository.findByEmail(email)
                .orElse(new EmailOtpEntity());

        emailOtpEntity.setEmail(email);
        emailOtpEntity.setOtp(otp);
        emailOtpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        emailOtpEntity.setVerified(false);

        emailOtpRepository.save(emailOtpEntity);
        mailService.sendOtpMail(email, otp);

        return "OTP sent successfully to " + email;
    }

    public String verifyOtp(String email, String otp) {
        Optional<EmailOtpEntity> otpOptional = emailOtpRepository.findByEmail(email);

        if (otpOptional.isEmpty()) {
            return "OTP not found for email: " + email;
        }

        EmailOtpEntity emailOtpEntity = otpOptional.get();

        if (emailOtpEntity.isVerified()) {
            return "Email already verified";
        }

        if (LocalDateTime.now().isAfter(emailOtpEntity.getExpiryTime())) {
            return "OTP expired";
        }

        if (!emailOtpEntity.getOtp().equals(otp)) {
            return "Invalid OTP";
        }

        emailOtpEntity.setVerified(true);
        emailOtpRepository.save(emailOtpEntity);

        employeeRegisterRepository.findByEmail(email).ifPresent(employee -> {
            employee.setEmailVerified(true);
            employeeRegisterRepository.save(employee);
        });

        return "Email verified successfully";
    }

    public String forgetPassword(ForgetPasswordRequest request) throws MessagingException {
        EmployeeRegisterModel employee = employeeRegisterRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        if (!isEmailVerified(request.getEmail())) {
            return "Please verify email first";
        }

        String otp = generateOtp();
        employee.setResetOtp(otp);
        employee.setResetOtpCreatedAt(LocalDateTime.now());
        employeeRegisterRepository.save(employee);

        mailService.sendOtpMail(request.getEmail(), otp);

        return "Password reset OTP sent successfully to " + request.getEmail();
    }

    public String resetPassword(ResetPasswordRequest request) {
        EmployeeRegisterModel employee = employeeRegisterRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        if (employee.getResetOtp() == null || !employee.getResetOtp().equals(request.getOtp())) {
            return "Invalid OTP";
        }

        if (isOtpExpired(employee.getResetOtpCreatedAt())) {
            clearResetOtp(employee);
            employeeRegisterRepository.save(employee);
            return "OTP expired";
        }

        if (!isOtpValid(request.getOtp(), employee.getResetOtp())) {
            return "Invalid OTP";
        }

        if (encoder.matches(request.getNewPassword(), employee.getPassword())) {
            return "New password cannot be the same as the old password";
        }

        employee.setPassword(encoder.encode(request.getNewPassword()));
        clearResetOtp(employee);
        employeeRegisterRepository.save(employee);

        return "Password reset successfully";
    }

    public boolean isEmailVerified(String email) {
        Optional<EmailOtpEntity> otpOptional = emailOtpRepository.findByEmail(email);
        if (otpOptional.isPresent() && otpOptional.get().isVerified()) {
            return true;
        }

        return employeeRegisterRepository.findByEmail(email)
                .map(EmployeeRegisterModel::isEmailVerified)
                .orElse(false);
    }

    public void validateDomain(String email) {
        if (email == null || !email.contains("@")) {
            throw new RuntimeException("Invalid email format");
        }

        String domain = email.substring(email.lastIndexOf("@") + 1).toLowerCase();

        if (!allowedDomains.contains(domain)) {
            throw new RuntimeException("Email domain is not allowed");
        }
    }

    private String generateOtp() {
        return String.format("%06d",
                SECURE_RANDOM.nextInt(1_000_000));
    }

    private boolean isOtpExpired(LocalDateTime createdAt) {
        if (createdAt == null) {
            return true;
        }
        return Duration.between(createdAt, LocalDateTime.now())
                .getSeconds() > OTP_EXPIRY_SECONDS;
    }

    private void clearResetOtp(EmployeeRegisterModel employee) {
        employee.setResetOtp(null);
        employee.setResetOtpCreatedAt(null);
    }

    private boolean isOtpValid(String provided, String stored) {
        if (provided == null || stored == null) {
            return false;
        }
        byte[] providedBytes = provided.getBytes(StandardCharsets.UTF_8);
        byte[] storedBytes = stored.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(providedBytes, storedBytes);
    }
}
