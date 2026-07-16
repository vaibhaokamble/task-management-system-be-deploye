package com.organization.taskManagement.DTO.Request;

import com.organization.taskManagement.Enums.EmployeeRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "EmployeeId is required")
    private String employeeId;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Role is required")
    private EmployeeRole role;
}
