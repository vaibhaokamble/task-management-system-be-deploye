package com.organization.taskManagement.Services;

import com.organization.taskManagement.DTO.Request.TaskPatchRequest;
import com.organization.taskManagement.DTO.Request.TaskRequest;
import com.organization.taskManagement.DTO.Response.TaskResponse;
import com.organization.taskManagement.DTO.Response.TaskUpdateResponse;
import com.organization.taskManagement.Enums.EmployeeRole;
import com.organization.taskManagement.Enums.TaskStatus;
import com.organization.taskManagement.Mappers.TaskMapper;
import com.organization.taskManagement.Model.EmployeeRegisterModel;
import com.organization.taskManagement.Model.ProjectModel;
import com.organization.taskManagement.Model.TaskModel;
import com.organization.taskManagement.Repository.EmployeeRegisterRepository;
import com.organization.taskManagement.Repository.ProjectRepository;
import com.organization.taskManagement.Repository.TaskRepository;
import com.organization.taskManagement.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Locale.filter;


// Service for managing task operations
@Service
@RequiredArgsConstructor
public class TaskService {

	private final TaskRepository taskRepo;

	private final EmployeeRegisterRepository employeeRegRepo;

	private final ProjectRepository projectRepository;

	private final CommentService commentService;

	public TaskResponse createTask(TaskRequest taskRequest) {
        EmployeeRegisterModel employeeRegModel = null;

		if(taskRequest.getAssignedTo() != null){
             employeeRegModel = employeeRegRepo.findByEmployeeId(taskRequest.getAssignedTo())
				.orElseThrow(() -> new ResourceNotFoundException("Employee", "ID", taskRequest.getAssignedTo()));
        }

		ProjectModel project = projectRepository.findById(taskRequest.getProjectId())
				.orElseThrow(() ->new ResourceNotFoundException("Project" , taskRequest.getProjectId()));

		TaskModel task = TaskMapper.toEntity(taskRequest, employeeRegModel , project);
		TaskModel savedTask = taskRepo.save(task);

		return TaskMapper.toResponse(savedTask);
	}

	public TaskUpdateResponse updateTask(Long id, TaskPatchRequest patchRequest) {
		TaskModel task = taskRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Task", id));

		if (patchRequest.getTitle() != null) {
			task.setTitle(patchRequest.getTitle());
		}
		if (patchRequest.getDescription() != null) {
			task.setDescription(patchRequest.getDescription());
		}
		if (patchRequest.getDueDate() != null) {
			task.setDueDate(patchRequest.getDueDate());
		}
		if (patchRequest.getStatus() != null) {
			task.setStatus(patchRequest.getStatus());
		}
		if (patchRequest.getAssignedToId() != null) {
			EmployeeRegisterModel employeeRegModel = employeeRegRepo.findByEmployeeId(patchRequest.getAssignedToId())
					.orElseThrow(() -> new ResourceNotFoundException("Employee", "ID", patchRequest.getAssignedToId()));
			task.setAssignedTo(employeeRegModel);
		}

		TaskModel savedTask = taskRepo.save(task);
		return new TaskUpdateResponse(TaskMapper.toResponse(savedTask));
	}

	public List<TaskResponse> getTasks(String team, String status) {
	     String employeeId = SecurityContextHolder.getContext().getAuthentication().getName();
		 EmployeeRegisterModel employee = employeeRegRepo.findByEmployeeId(employeeId).orElseThrow(() -> new ResourceNotFoundException("Employee", "ID",  employeeId));

		 List<ProjectModel> projects;

		 projects = projectRepository.findByCreatedByOrEmployeesContains(employee, employee);

		 List<Long> projectIds = projects.stream().map(ProjectModel::getId).collect(Collectors.toList());

		 String parsedTeam = (team == null || team.isBlank()) ? null : team;
		 TaskStatus parsedStatus = (status == null || status.isBlank()) ? null : TaskStatus.valueOf(status);

		 return taskRepo.findByProjectIdIn(projectIds)
				 .stream()
				 .filter(task -> parsedStatus == null || task.getStatus() == parsedStatus)
				.map(TaskMapper::toResponse)
				.collect(Collectors.toList());

	}

	public TaskResponse getTaskById(Long id) {
		TaskModel task = taskRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Task", id));
		return TaskMapper.toResponse(task, commentService.getCommentsByTaskId(id));
	}

	   public List<TaskResponse> getTasksByProject(Long projectId) {
		   String employeeId = SecurityContextHolder.getContext().getAuthentication().getName();
		   EmployeeRegisterModel employee = employeeRegRepo.findByEmployeeId(employeeId).orElseThrow(() -> new ResourceNotFoundException("Employee", "ID", employeeId));

		   ProjectModel project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project" , projectId));

		   boolean isOwner = project.getCreatedBy().getId().equals(employee.getId());

		   boolean isMember = project.getEmployees().stream().anyMatch(e -> e.getId().equals(employee.getId()));

		   if (employee.getRole() == EmployeeRole.TEAM_LEAD) {
			   if (!isOwner && !isMember){
				   throw new RuntimeException("Access denied");
			   }
		   } else {
			   if (!isMember){
				   throw new RuntimeException("Access denied");
			   }
		   }
		   return taskRepo.findByProjectId(projectId).stream()
				   .map(TaskMapper::toResponse).toList();
	   }
}
