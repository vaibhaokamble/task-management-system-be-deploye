package com.organization.taskManagement.DTO.Request;

import com.organization.taskManagement.Enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskPatchRequest {
    private String title;
    private String description;
    private String assignedToId;
    private LocalDate dueDate;
    private TaskStatus status;
    private Long projectId;
}

