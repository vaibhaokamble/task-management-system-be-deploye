package com.organization.taskManagement.Repository;

import com.organization.taskManagement.Model.EmployeeRegisterModel;
import com.organization.taskManagement.Model.ProjectModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectModel, Long> {

    List<ProjectModel> findByEmployeesContains(EmployeeRegisterModel employee);

    List<ProjectModel> findByCreatedBy(EmployeeRegisterModel employee);

    List<ProjectModel> findByCreatedByOrEmployeesContains(EmployeeRegisterModel createdBy,
                                                          EmployeeRegisterModel employee);

}
