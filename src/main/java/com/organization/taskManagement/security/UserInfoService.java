package com.organization.taskManagement.security;

import com.organization.taskManagement.DTO.Request.EmployeeRegistrationRequest;
import com.organization.taskManagement.Mappers.EmployeeMapper;
import com.organization.taskManagement.Model.EmployeeRegisterModel;
import com.organization.taskManagement.Repository.EmployeeRegisterRepository;
import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class UserInfoService implements UserDetailsService {

    private final EmployeeRegisterRepository employeeRegisterRepository;
    private final PasswordEncoder passwordEncoder;

    public UserInfoService(EmployeeRegisterRepository employeeRegisterRepository, PasswordEncoder passwordEncoder) {
        this.employeeRegisterRepository = employeeRegisterRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String employeeId) throws UsernameNotFoundException {
        Optional<EmployeeRegisterModel> employeeOpt = employeeRegisterRepository.findByEmployeeId(employeeId);
        if(employeeOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with employee ID: " + employeeId);
        }

        EmployeeRegisterModel employeeRegisterModel = employeeOpt.get();
        return new UserInfoDetails(employeeRegisterModel);
        }


        public String addEmployee(EmployeeRegistrationRequest employee) {
            if(!employeeRegisterRepository.findByEmployeeId(employee.getEmployeeId()).isEmpty()) {
                return "Email already exists";
            }
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
            employeeRegisterRepository.save(EmployeeMapper.toEntity(employee));
            return "Employee added successfully";
        }
}

