package com.organization.taskManagement.Controller;


import com.organization.taskManagement.DTO.Response.ApiResponse;
import com.organization.taskManagement.DTO.Response.EmployeeRegistrationResponse;
import com.organization.taskManagement.Services.EmployeeRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmployeeRegisterController {

    private final EmployeeRegisterService employeeRegService;


    @GetMapping
    public ResponseEntity<Page<EmployeeRegistrationResponse>> getAllEmployees(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, sortBy);

        return ResponseEntity.ok(employeeRegService.getAllEmployees(keyword, pageable));
    }

    @GetMapping("/except-current-teamlead")
    public ResponseEntity<Page<EmployeeRegistrationResponse>> getAllEmployeesExceptCurrentTeamLead(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, sortBy);

        return ResponseEntity.ok(employeeRegService.getAllEmployeesExceptCurrentTeamLead(keyword, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteEmployee(@PathVariable Long id){
        employeeRegService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deleted successfully", null));
    }

}
