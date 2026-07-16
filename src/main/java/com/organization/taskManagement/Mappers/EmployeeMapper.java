package com.organization.taskManagement.Mappers;

import com.organization.taskManagement.DTO.Request.EmployeeRegistrationRequest;
import com.organization.taskManagement.DTO.Response.EmployeeRegistrationResponse;
import com.organization.taskManagement.Model.EmployeeRegisterModel;

public class EmployeeMapper {

    public static EmployeeRegisterModel toEntity(EmployeeRegistrationRequest request){

        if(request == null) return null;

        return EmployeeRegisterModel.builder()
                .name(request.getName())
                .email(request.getEmail())
                .employeeId(request.getEmployeeId())
                .password(request.getPassword())
                .role(request.getRole())
                .designation(request.getDesignation())
                .build();
    }

    public static EmployeeRegistrationResponse toResponse(EmployeeRegisterModel employee){

        if(employee == null) return  null;

        return EmployeeRegistrationResponse.builder()
                .id(employee.getId())
                .name(employee.getName())
                .email(employee.getEmail())
                .employeeId(employee.getEmployeeId())
                .role(employee.getRole())
                .designation(employee.getDesignation())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }

}
