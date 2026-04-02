package com.kt.mindLog.controller.report;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.mindLog.domain.report.Report;
import com.kt.mindLog.dto.report.request.ReportCreateRequest;
import com.kt.mindLog.global.annotation.Login;
import com.kt.mindLog.global.security.CustomUser;
import com.kt.mindLog.service.report.ReportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reports")
public class ReportController {
	private final ReportService reportService;

	@PostMapping("")
	public void createReport(@Login CustomUser user, @Valid @RequestBody ReportCreateRequest request) {
		reportService.createReport(user.getId(), request);
	}
}
