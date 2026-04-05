package com.kt.mindLog.controller.report;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.mindLog.domain.report.Report;
import com.kt.mindLog.dto.report.request.ReportCreateRequest;
import com.kt.mindLog.dto.report.response.EmotionGraphResponse;
import com.kt.mindLog.dto.report.response.ReportResponse;
import com.kt.mindLog.dto.report.response.SuggestionsResponse;
import com.kt.mindLog.global.annotation.Login;
import com.kt.mindLog.global.security.CustomUser;
import com.kt.mindLog.service.report.ReportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reports")
public class ReportController {
	private final ReportService reportService;

	@PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Object> createReport(@Login CustomUser user, @Valid @RequestBody ReportCreateRequest request) {
		return reportService.createReport(user.getId(), request);
	}

	@GetMapping("")
	public List<ReportResponse> getReports(@Login CustomUser user) {
		return reportService.getReports(user.getId());
	}

	@GetMapping("/{reportId}/graphs")
	public EmotionGraphResponse getEmotionGraphs(@PathVariable UUID reportId) {
		return reportService.getEmotionGraphs(reportId);
	}

	@GetMapping("/{reportId}/suggestions")
	public List<SuggestionsResponse> getSuggestions(@PathVariable UUID reportId) {
		return reportService.getSuggestions(reportId);
	}
}
