package com.organization.taskManagement.Services;

import com.organization.taskManagement.DTO.Request.EmployeeRegistrationRequest;
import com.organization.taskManagement.DTO.Request.LoginRequest;
import com.organization.taskManagement.DTO.Response.EmployeeRegistrationResponse;
import com.organization.taskManagement.Enums.EmployeeRole;
import com.organization.taskManagement.Mappers.EmployeeMapper;
import com.organization.taskManagement.Model.EmployeeRegisterModel;
import com.organization.taskManagement.Repository.EmployeeRegisterRepository;
import com.organization.taskManagement.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeRegisterService {

    private final EmployeeRegisterRepository employeeRegisterRepository;
    private final PasswordEncoder passwordEncoder;


    public EmployeeRegistrationResponse registerEmployee(EmployeeRegistrationRequest request) {

        if (employeeRegisterRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new IllegalArgumentException("Employee ID already exists");
        }

        if (employeeRegisterRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        EmployeeRegisterModel employeeRegisterModel = EmployeeMapper.toEntity(request);
        employeeRegisterModel.setPassword(passwordEncoder.encode(employeeRegisterModel.getPassword()));
        EmployeeRegisterModel result = employeeRegisterRepository.save(employeeRegisterModel);

        return EmployeeMapper.toResponse(result);
    }

    public EmployeeRegistrationResponse login(LoginRequest request) {
        EmployeeRegisterModel employee = employeeRegisterRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));


        if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return EmployeeMapper.toResponse(employee);
    }

    public void deleteEmployee (Long id){
        EmployeeRegisterModel employee = employeeRegisterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        employeeRegisterRepository.delete(employee);
    }

    public Page<EmployeeRegistrationResponse> getAllEmployees(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return employeeRegisterRepository.searchEmployees(keyword.trim(), pageable)
                    .map(EmployeeMapper::toResponse);
        }
        return employeeRegisterRepository.findAll(pageable)
                .map(EmployeeMapper::toResponse);
    }

    public Page<EmployeeRegistrationResponse> getAllEmployeesExceptCurrentTeamLead(String keyword, Pageable pageable) {
        String employeeId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (keyword != null && !keyword.trim().isEmpty()) {
            return employeeRegisterRepository.searchEmployeesExceptCurrentTeamLead(keyword.trim(), employeeId, pageable)
                    .map(EmployeeMapper::toResponse);
        }
        return employeeRegisterRepository.findByEmployeeIdNot(employeeId, pageable)
                .map(EmployeeMapper::toResponse);
    }
}
