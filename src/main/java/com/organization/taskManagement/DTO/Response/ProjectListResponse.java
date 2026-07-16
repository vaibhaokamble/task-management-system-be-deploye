package com.organization.taskManagement.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProjectListResponse {

    private List<ProjectResponse> projects;
}
