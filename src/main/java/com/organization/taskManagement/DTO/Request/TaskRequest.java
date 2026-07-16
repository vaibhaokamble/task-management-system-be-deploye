package com.organization.taskManagement.DTO.Request;

import com.organization.taskManagement.Enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
	private String title;
	private String description;
    private String teamId;
	private String assignedTo;
	private LocalDate dueDate;
	private TaskStatus status;
	private LocalDate updatedAt;
	private Long projectId;
}
