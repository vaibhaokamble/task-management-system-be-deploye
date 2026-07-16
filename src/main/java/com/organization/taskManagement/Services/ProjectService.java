package com.organization.taskManagement.Services;

import com.organization.taskManagement.DTO.Request.ProjectRequest;
import com.organization.taskManagement.DTO.Response.ProjectListResponse;
import com.organization.taskManagement.DTO.Response.ProjectResponse;
import com.organization.taskManagement.Enums.EmployeeRole;
import com.organization.taskManagement.Mappers.ProjectMapper;
import com.organization.taskManagement.Model.EmployeeRegisterModel;
import com.organization.taskManagement.Model.ProjectModel;
import com.organization.taskManagement.Repository.EmployeeRegisterRepository;
import com.organization.taskManagement.Repository.ProjectRepository;
import com.organization.taskManagement.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EmployeeRegisterRepository employeeRegisterRepository;

    public ProjectResponse createProject(ProjectRequest request) {

        List<EmployeeRegisterModel> employees = request.getEmployeeIds().stream()
                        .map(employeeId -> employeeRegisterRepository.findByEmployeeId(employeeId)
                                .orElseThrow(() -> new ResourceNotFoundException("Employee" , "employeeId", employeeId))).toList();

        ProjectModel project = ProjectMapper.toEntity(request, employees);

        String employeeId = SecurityContextHolder.getContext().getAuthentication().getName();

        EmployeeRegisterModel creator = employeeRegisterRepository.findByEmployeeId(employeeId).orElseThrow(() -> new ResourceNotFoundException("Employee" , "employeeId", employeeId));

        project.setCreatedBy(creator);
        ProjectModel savedProject = projectRepository.save(project);

        return ProjectMapper.toResponse(savedProject);
    }

    public ProjectListResponse getAllProjects() {

        String employeeId = SecurityContextHolder.getContext().getAuthentication().getName();
        EmployeeRegisterModel employee = employeeRegisterRepository.findByEmployeeId(employeeId).orElseThrow(() -> new ResourceNotFoundException("Employee" , "employeeId", employeeId));

        List<ProjectModel> projects;

        projects = projectRepository.findByCreatedByOrEmployeesContains(employee, employee);

        List<ProjectResponse> response = projects.stream().map(ProjectMapper::toResponse).toList();

        return new ProjectListResponse(response);
    }

    public ProjectResponse getProjectById(Long id) {

        ProjectModel project = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project", id));
        return ProjectMapper.toResponse(project);
    }

    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        ProjectModel project = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project", id));

        String employeeId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!project.getCreatedBy().getEmployeeId().equals(employeeId)) {
            throw new RuntimeException("Only the project creator can update the status.");
        }

        project.setStatus(request.getStatus());

        ProjectModel updatedProject = projectRepository.save(project);
        return ProjectMapper.toResponse(updatedProject);
    }

    public ProjectResponse addMemberToProject(Long projectId, String  employeeId){
        ProjectModel project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project" , projectId));

        EmployeeRegisterModel employee = employeeRegisterRepository.findByEmployeeId(employeeId).orElseThrow(() -> new ResourceNotFoundException("Employee" , "employeeId", employeeId));

        String currentEmployeeId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!project.getCreatedBy().getEmployeeId().equals(currentEmployeeId)) {
            throw new RuntimeException("Only the project creator can manage members.");
        }

        if (!project.getEmployees().contains(employee)) {
            project.getEmployees().add(employee);
        }
        return ProjectMapper.toResponse(projectRepository.save(project));
    }

    public ProjectResponse removeMemberFromProject(Long projectId, String employeeId) {
        ProjectModel project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        EmployeeRegisterModel employee = employeeRegisterRepository.findByEmployeeId(employeeId).orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeId", employeeId));

        String currentEmployeeId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!project.getCreatedBy().getEmployeeId().equals(currentEmployeeId)) {
            throw new RuntimeException("Only the project creator can manage members.");
        }

            project.getEmployees().remove(employee);

        return ProjectMapper.toResponse(projectRepository.save(project));
    }

    public void deleteProjectById(Long id) {
        ProjectModel project = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project", id));

        String employeeId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!project.getCreatedBy().getEmployeeId().equals(employeeId)) {
            throw new RuntimeException("You can only delete projects created by you.");
        }

        projectRepository.delete(project);

    }

}
