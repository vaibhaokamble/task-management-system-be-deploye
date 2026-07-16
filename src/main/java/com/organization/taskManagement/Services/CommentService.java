package com.organization.taskManagement.Services;

import com.organization.taskManagement.DTO.Request.CommentRequest;
import com.organization.taskManagement.DTO.Response.CommentCreateResponse;
import com.organization.taskManagement.DTO.Response.CommentResponse;
import com.organization.taskManagement.Mappers.CommentMapper;
import com.organization.taskManagement.Model.CommentModel;
import com.organization.taskManagement.Model.EmployeeRegisterModel;
import com.organization.taskManagement.Model.TaskModel;
import com.organization.taskManagement.Repository.CommentRepository;
import com.organization.taskManagement.Repository.EmployeeRegisterRepository;
import com.organization.taskManagement.Repository.TaskRepository;
import com.organization.taskManagement.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final TaskRepository taskRepo;
	private final EmployeeRegisterRepository employeeRegRepo;

	public CommentCreateResponse addComment(Long taskId, CommentRequest request) {
		TaskModel task = taskRepo.findById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

		EmployeeRegisterModel employee = employeeRegRepo.findByEmployeeId(request.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Employee", "ID", request.getUserId()));

		CommentModel comment = CommentMapper.toEntity(request, task, employee);
		CommentModel savedComment = commentRepository.save(comment);
		CommentResponse commentResponse = CommentMapper.toResponse(savedComment);

		return new CommentCreateResponse(commentResponse);
	}

	public List<CommentResponse> getCommentsByTaskId(Long taskId) {
		TaskModel task = taskRepo.findById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

		String teamLeadId = task.getProject() != null && task.getProject().getCreatedBy() != null
				? task.getProject().getCreatedBy().getEmployeeId()
				: null;
		String assignedEmployeeId = task.getAssignedTo() != null
				? task.getAssignedTo().getEmployeeId()
				: null;

		return commentRepository.findByTaskModelIdOrderByCreatedAtAsc(taskId)
				.stream()
				.filter(comment -> {
					String authorId = comment.getEmployeeId() != null ? comment.getEmployeeId().getEmployeeId() : null;
					return (authorId != null) && (authorId.equals(teamLeadId) || authorId.equals(assignedEmployeeId));
				})
				.map(CommentMapper::toResponse)
				.collect(Collectors.toList());
	}
}
