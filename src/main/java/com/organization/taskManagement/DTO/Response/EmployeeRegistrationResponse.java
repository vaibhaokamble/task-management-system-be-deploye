package com.organization.taskManagement.DTO.Response;

import com.organization.taskManagement.Enums.Designation;
import com.organization.taskManagement.Enums.EmployeeRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRegistrationResponse {
    private Long id;
    private String name;
    private String email;
    private String employeeId;
    private EmployeeRole role;
    private Designation designation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
