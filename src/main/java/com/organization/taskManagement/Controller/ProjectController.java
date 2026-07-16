package com.organization.taskManagement.Controller;


import com.organization.taskManagement.DTO.Request.ProjectRequest;
import com.organization.taskManagement.DTO.Response.ApiResponse;
import com.organization.taskManagement.DTO.Response.ProjectListResponse;
import com.organization.taskManagement.DTO.Response.ProjectResponse;
import com.organization.taskManagement.Services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/project")
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @RequestBody ProjectRequest request)
    {
        ProjectResponse response = projectService.createProject(request);
        return ResponseEntity.ok(ApiResponse.success("Project created successfully", response));
    }

    @GetMapping("/projects")
    public ResponseEntity<ApiResponse<ProjectListResponse>> getProjects()
    {
        return ResponseEntity.ok(ApiResponse.success("Projects fetched successfully", projectService.getAllProjects()));
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(
            @PathVariable Long id)
    {
        return ResponseEntity.ok(ApiResponse.success("Project fetched successfully", projectService.getProjectById(id)));
    }

    @PatchMapping("/projects/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable Long id,
            @RequestBody ProjectRequest request) {
        ProjectResponse response = projectService.updateProject(id, request);
        return ResponseEntity.ok(ApiResponse.success("Project updated successfully", response));
    }

    @PatchMapping("/projects/{projectId}/members/{employeeId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> addMember(
            @PathVariable Long projectId,
            @PathVariable String employeeId) {
        return ResponseEntity.ok(ApiResponse.success("Member added successfully" , projectService.addMemberToProject(projectId, employeeId)
        ));
    }

    @DeleteMapping("/projects/{projectId}/members/{employeeId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> removeMember(
            @PathVariable Long projectId,
            @PathVariable String employeeId) {
        return ResponseEntity.ok(ApiResponse.success("Member removed successfully" , projectService.removeMemberFromProject(projectId, employeeId)
        ));
    }

    @DeleteMapping("projects/{projectId}/delete")
    public ResponseEntity<String> deleteProjectById(
            @PathVariable Long projectId)
    {
        projectService.deleteProjectById(projectId);
        return ResponseEntity.ok("Project deleted successfully");
    }


}
