package com.organization.taskManagement.Mappers;

import com.organization.taskManagement.DTO.Request.CommentRequest;
import com.organization.taskManagement.DTO.Response.CommentResponse;
import com.organization.taskManagement.Model.CommentModel;
import com.organization.taskManagement.Model.EmployeeRegisterModel;
import com.organization.taskManagement.Model.TaskModel;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CommentMapper {

    public static CommentModel toEntity(CommentRequest request, TaskModel task, EmployeeRegisterModel employee) {

        if (request == null) return null;

        return CommentModel.builder()
                .message(request.getText())
                .employeeId(employee)
                .taskModel(task)
                .build();
    }

    public static CommentResponse toResponse(CommentModel comment) {

        if (comment == null) return null;

        CommentResponse response = new CommentResponse();
        response.setId(comment.getId() != null ? String.valueOf(comment.getId()) : null);
        response.setUserId(comment.getEmployeeId() != null ? comment.getEmployeeId().getEmployeeId() : null);
        response.setUserName(comment.getEmployeeId() != null ? comment.getEmployeeId().getName() : null);
        response.setText(comment.getMessage());
        response.setTimestamp(
                comment.getCreatedAt() == null
                        ? null
                        : comment.getCreatedAt()
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );

        return response;
    }
}