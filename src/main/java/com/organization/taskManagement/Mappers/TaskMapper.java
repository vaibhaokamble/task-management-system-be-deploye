package com.organization.taskManagement.Mappers;

import com.organization.taskManagement.DTO.Request.TaskRequest;
import com.organization.taskManagement.DTO.Response.TaskResponse;
import com.organization.taskManagement.Enums.TaskStatus;
import com.organization.taskManagement.Model.EmployeeRegisterModel;
import com.organization.taskManagement.Model.ProjectModel;
import com.organization.taskManagement.Model.TaskModel;

import com.organization.taskManagement.DTO.Response.CommentResponse;
import java.util.List;

// Mapper for converting Task DTOs to/from entities
public class TaskMapper {

    public static TaskModel toEntity(TaskRequest request, EmployeeRegisterModel employee , ProjectModel project) {

        if (request == null) return null;

        TaskStatus finalStatus;
        if (employee == null) {
            finalStatus = TaskStatus.NEW;
        } else {
            finalStatus = request.getStatus() != null ? request.getStatus() : TaskStatus.ASSIGNED;
        }

        return TaskModel.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .teamId(request.getTeamId())
                .dueDate(request.getDueDate())
                .assignedTo(employee)
                .project(project)
                .status(finalStatus)
                .build();
    }

    public static TaskResponse toResponse(TaskModel task) {
        return toResponse(task, null);
    }

    public static TaskResponse toResponse(TaskModel task, List<CommentResponse> comments) {

        if (task == null) return null;

        TaskResponse response = new TaskResponse();
        response.setId(task.getId() != null ? String.valueOf(task.getId()) : null);
        response.setTitle(task.getTitle());
        response.setStatus(task.getStatus());
        response.setDescription(task.getDescription());
        response.setAssignedTo(task.getAssignedTo() != null ? task.getAssignedTo().getEmployeeId() : null);
        response.setProjectId(task.getProject() != null ? task.getProject().getId() : null);
        response.setProjectName(task.getProject() != null ? task.getProject().getProjectName() : null);
        response.setDueDate(task.getDueDate());
        response.setComments(comments);

        return response;
    }
}
