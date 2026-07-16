package com.organization.taskManagement.Services;

import com.organization.taskManagement.DTO.Response.AnalyticsResponse;
import com.organization.taskManagement.Enums.TaskStatus;
import com.organization.taskManagement.Model.TaskModel;
import com.organization.taskManagement.Repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

	private final TaskRepository taskRepo;

	public AnalyticsResponse getOverview() {
		List<TaskModel> tasks = taskRepo.findAll();

		long totalTasks = tasks.size();
		long completedTasks = tasks.stream().filter(task -> task.getStatus() == TaskStatus.DONE).count();
		long pendingTasks = tasks.stream().filter(task -> task.getStatus() != TaskStatus.DONE).count();

		Map<String, Long> teamDistribution = new LinkedHashMap<>();
		tasks.stream()
				.filter(task -> task.getTeamId() != null && !task.getTeamId().isBlank())
				.forEach(task -> {
					String team = task.getTeamId().trim().toLowerCase();
					teamDistribution.put(team, teamDistribution.getOrDefault(team, 0L) + 1);
				});

		AnalyticsResponse response = new AnalyticsResponse();
		response.setTotalTasks(totalTasks);
		response.setCompletedTasks(completedTasks);
		response.setPendingTasks(pendingTasks);
		response.setTeamDistribution(teamDistribution);
		return response;
	}
}
