package com.organization.taskManagement.DTO.Response;

import com.organization.taskManagement.Enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TaskResponse {
    private String id;
    private String title;
    private String description;
    private TaskStatus status;
    private String assignedTo;
    private LocalDate dueDate;
    private Long projectId;
    private String projectName;
    private List<CommentResponse> comments;
}
