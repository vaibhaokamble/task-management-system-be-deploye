package com.organization.taskManagement.DTO.Request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String newPassword;
    private String otp;

}
