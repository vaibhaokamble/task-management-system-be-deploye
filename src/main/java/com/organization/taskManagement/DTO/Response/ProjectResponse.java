package com.organization.taskManagement.DTO.Response;

import com.organization.taskManagement.Enums.ProjectStatus;
import lombok.Data;

import java.util.List;

@Data
public class ProjectResponse {

    private Long id;

    private String projectName;

    private String description;

    private ProjectStatus status;

    private List<String> employees;

    private String createdByName;

    private String createdBy;

    private Long createdById;
}
