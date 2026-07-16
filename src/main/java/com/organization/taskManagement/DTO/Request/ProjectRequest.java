package com.organization.taskManagement.DTO.Request;

import com.organization.taskManagement.Enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

    private String projectName;

    private String description;

    private ProjectStatus status;

    private List<String> employeeIds;
}
