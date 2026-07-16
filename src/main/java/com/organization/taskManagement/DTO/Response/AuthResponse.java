package com.organization.taskManagement.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private EmployeeRegistrationResponse employee;
    private String accessToken;
    private String refreshToken;
}
