package com.organization.taskManagement.Mappers;

import com.organization.taskManagement.DTO.Request.ProjectRequest;
import com.organization.taskManagement.DTO.Response.ProjectResponse;
import com.organization.taskManagement.Enums.ProjectStatus;
import com.organization.taskManagement.Model.EmployeeRegisterModel;
import com.organization.taskManagement.Model.ProjectModel;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectMapper {

    public static ProjectModel toEntity(
            ProjectRequest request,
            List<EmployeeRegisterModel> employees
    ) {

        ProjectStatus finalStatus;

         try {
            finalStatus = request.getStatus();
        } catch (Exception ex) {
            finalStatus = ProjectStatus.ACTIVE;
        }

        return ProjectModel.builder()
                .projectName(request.getProjectName())
                .description(request.getDescription())
                .status(finalStatus)
                .employees(employees)
                .build();
    }
    public static ProjectResponse toResponse(ProjectModel project) {
        ProjectResponse response = new ProjectResponse();

        response.setId(project.getId());
        response.setProjectName(project.getProjectName());
        response.setDescription(project.getDescription());
        response.setStatus(project.getStatus());

        response.setEmployees(
                project.getEmployees()
                        .stream()
                        .map(EmployeeRegisterModel::getEmployeeId)
                        .collect(Collectors.toList())
        );

        response.setCreatedByName(
                project.getCreatedBy() != null
                        ? project.getCreatedBy().getName()
                        : null
        );
        response.setCreatedBy(
                project.getCreatedBy() != null
                        ? project.getCreatedBy().getEmployeeId()
                        : null
        );
        response.setCreatedById(
                project.getCreatedBy() != null
                        ? project.getCreatedBy().getId()
                        : null
        );
        return response;

    }
}