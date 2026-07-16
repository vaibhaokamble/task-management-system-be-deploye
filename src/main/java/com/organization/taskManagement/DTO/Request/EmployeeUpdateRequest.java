package com.organization.taskManagement.DTO.Request;

import com.organization.taskManagement.Enums.Designation;
import com.organization.taskManagement.Enums.EmployeeRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeUpdateRequest {


    private String name;

    @Email(message = "Invalid email format")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@(koderztech\\.com|gmail\\.com)$")
    private String email;

    private EmployeeRole role;

    private Designation designation;
}
