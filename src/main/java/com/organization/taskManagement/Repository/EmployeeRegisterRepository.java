package com.organization.taskManagement.Repository;

import com.organization.taskManagement.DTO.Response.EmployeeRegistrationResponse;
import com.organization.taskManagement.Enums.EmployeeRole;
import com.organization.taskManagement.Model.EmployeeRegisterModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
@Repository
public interface EmployeeRegisterRepository extends JpaRepository<EmployeeRegisterModel, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmployeeId(String employeeId);

    Optional<EmployeeRegisterModel> findByEmail(String email);

    Optional<EmployeeRegisterModel> findByEmployeeId(String employeeId);
    Page<EmployeeRegisterModel> findByRole(EmployeeRole role, Pageable pageable);
    Page<EmployeeRegisterModel> findByEmployeeIdNot(String employeeId, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT e FROM EmployeeRegisterModel e WHERE " +
           "LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<EmployeeRegisterModel> searchEmployees(@org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT e FROM EmployeeRegisterModel e WHERE " +
           "e.employeeId != :employeeId AND (" +
           "LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<EmployeeRegisterModel> searchEmployeesExceptCurrentTeamLead(@org.springframework.data.repository.query.Param("keyword") String keyword, @org.springframework.data.repository.query.Param("employeeId") String employeeId, Pageable pageable);
}
