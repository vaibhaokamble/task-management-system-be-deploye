package com.organization.taskManagement.Mappers;

import com.organization.taskManagement.DTO.Response.AuthResponse;
import com.organization.taskManagement.DTO.Response.EmployeeRegistrationResponse;

public class AuthMapper {

    public static AuthResponse toResponse(EmployeeRegistrationResponse employeeResponse, String accessToken, String refreshToken) {
        if (employeeResponse == null) return null;

        AuthResponse authResponse = new AuthResponse();
        authResponse.setEmployee(employeeResponse);
        authResponse.setAccessToken(accessToken);
        authResponse.setRefreshToken(refreshToken);
        return authResponse;
    }
}
