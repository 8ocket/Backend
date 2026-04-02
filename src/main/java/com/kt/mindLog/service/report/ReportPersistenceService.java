package com.kt.mindLog.service.report;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mindLog.domain.report.ReportStatus;
import com.kt.mindLog.dto.report.response.ReportResponse;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.repository.report.ReportRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportPersistenceService {
	private final ReportRepository reportRepository;

	@Transactional
	public void saveReport(ReportResponse response, UUID reportId) {
		var report = reportRepository.findByIdOrThrow(reportId, ErrorCode.NOT_FOUND_REPORT);

		//TODO report response data 저장 (AI와 논의 후 진행)

		report.updateReportStatus(ReportStatus.GENERATED);
	}
}
