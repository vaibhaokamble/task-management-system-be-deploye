package com.organization.taskManagement.Controller;

import com.organization.taskManagement.DTO.Response.AnalyticsResponse;
import com.organization.taskManagement.DTO.Response.ApiResponse;
import com.organization.taskManagement.Services.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

	private final AnalyticsService analyticsService;

	@GetMapping("/overview")
	public ResponseEntity<ApiResponse<AnalyticsResponse>> overview() {
		AnalyticsResponse analyticsResponse = analyticsService.getOverview();
		return ResponseEntity.ok(ApiResponse.success("Analytics overview retrieved successfully", analyticsService.getOverview()));
	}
}
