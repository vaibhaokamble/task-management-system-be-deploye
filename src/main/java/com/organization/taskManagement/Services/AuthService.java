package com.organization.taskManagement.Services;


import com.organization.taskManagement.DTO.Request.EmployeeRegistrationRequest;
import com.organization.taskManagement.DTO.Request.LoginRequest;
import com.organization.taskManagement.DTO.Response.AuthResponse;
import com.organization.taskManagement.DTO.Response.EmployeeRegistrationResponse;
import com.organization.taskManagement.Mappers.AuthMapper;
import com.organization.taskManagement.Mappers.EmployeeMapper;
import com.organization.taskManagement.Model.EmailOtpEntity;
import com.organization.taskManagement.Model.EmployeeRegisterModel;
import com.organization.taskManagement.Repository.EmailOtpRepository;
import com.organization.taskManagement.Repository.EmployeeRegisterRepository;
import com.organization.taskManagement.security.JwtService;
import com.organization.taskManagement.security.RefreshTokenService;
import com.organization.taskManagement.security.UserInfoDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRegisterRepository employeeRegisterRepository;
    private final EmailOtpRepository emailOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public EmployeeRegistrationResponse registerEmployee(@Valid EmployeeRegistrationRequest request) {
        if(employeeRegisterRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists");
        }

        if (employeeRegisterRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }


        EmployeeRegisterModel employeeRegisterModel = EmployeeMapper.toEntity(request);
        employeeRegisterModel.setPassword(passwordEncoder.encode(request.getPassword()));
        employeeRegisterModel.setEmailVerified(emailOtpRepository.findByEmail(request.getEmail())
                .map(EmailOtpEntity::isVerified)
                .orElse(false));
        EmployeeRegisterModel result = employeeRegisterRepository.save(employeeRegisterModel);

        return EmployeeMapper.toResponse(result);
    }

    public AuthResponse login(@Valid LoginRequest request) {
        EmployeeRegisterModel employeeRegisterModel = employeeRegisterRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (employeeRegisterModel.getRole() != request.getRole()) {
            throw new IllegalArgumentException("Invalid role");
        }

        if (!passwordEncoder.matches(request.getPassword(), employeeRegisterModel.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        UserInfoDetails userDetails = new UserInfoDetails(employeeRegisterModel);
        String accessToken = jwtService.generateToken(employeeRegisterModel.getEmployeeId(), userDetails);
        String refreshToken = refreshTokenService.createRefreshToken(employeeRegisterModel.getEmployeeId(), employeeRegisterModel.getRole().name()).getToken();

        return AuthMapper.toResponse(EmployeeMapper.toResponse(employeeRegisterModel), accessToken, refreshToken);
    }
}
