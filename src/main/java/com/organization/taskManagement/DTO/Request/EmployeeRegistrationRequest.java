package com.organization.taskManagement.DTO.Request;

import com.organization.taskManagement.Enums.Designation;
import com.organization.taskManagement.Enums.EmployeeRole;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRegistrationRequest {
    @NotBlank(message = "Name is required")

    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$", message = "Email must be a valid @gmail.com address")
    private String email;

    @NotBlank(message = "Employee ID is required.")
    @Pattern(regexp = "^KDZ(00[1-9]|0[1-9]\\d|[1-9]\\d{2})$",
            message = "Employee ID must be in the format 'KDZ001' to 'KDZ999'.")
    private String employeeId;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Password must contain at least one digit, one lowercase, one uppercase letter, and one special character")
    private String password;

    @NotNull(message = "Role is required")
    private EmployeeRole role;

    @NotNull(message = "Designation is required")
    private Designation designation;

}
