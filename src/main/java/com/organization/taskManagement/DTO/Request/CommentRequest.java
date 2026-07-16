package com.organization.taskManagement.DTO.Request;

import lombok.Data;

@Data
public class CommentRequest {
    private String text;
    private String userId;
    private String userName;
}
