package com.organization.taskManagement.Controller;

import com.organization.taskManagement.DTO.Request.CommentRequest;
import com.organization.taskManagement.DTO.Response.ApiResponse;
import com.organization.taskManagement.DTO.Response.CommentCreateResponse;
import com.organization.taskManagement.DTO.Response.CommentResponse;
import com.organization.taskManagement.Services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping("/{id}/comments")
	public ResponseEntity<ApiResponse <CommentCreateResponse>> addComment(
			@PathVariable Long id,
			@RequestBody CommentRequest request
	) {
		CommentCreateResponse response = commentService.addComment(id, request);
		return ResponseEntity.ok(ApiResponse.success("Comment added successfully", response));
	}

	@GetMapping("/{id}/comments")
	public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable Long id) {
		List<CommentResponse> response = commentService.getCommentsByTaskId(id);
		return ResponseEntity.ok(ApiResponse.success("Comments fetched successfully", response));
	}
}
