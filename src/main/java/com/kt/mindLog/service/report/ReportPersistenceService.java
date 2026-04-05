package com.kt.mindLog.service.report;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mindLog.domain.report.Report;
import com.kt.mindLog.domain.report.ReportEmotionGraph;
import com.kt.mindLog.domain.report.ReportStatus;
import com.kt.mindLog.domain.report.ReportSuggestion;
import com.kt.mindLog.dto.report.response.ReportEmotionGraphResponse;
import com.kt.mindLog.dto.report.response.ReportResponse;
import com.kt.mindLog.dto.report.response.ReportSuggestionResponse;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.repository.report.ReportEmotionGraphRepository;
import com.kt.mindLog.repository.report.ReportRepository;
import com.kt.mindLog.repository.report.ReportSuggestionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportPersistenceService {
	private final ReportRepository reportRepository;
	private final ReportEmotionGraphRepository reportGraphRepository;
	private final ReportSuggestionRepository reportSuggestionRepository;

	@Transactional
	public void saveReport(ReportResponse response, UUID reportId) {
		var report = reportRepository.findByIdOrThrow(reportId, ErrorCode.NOT_FOUND_REPORT);

		//TODO report response data 저장 (AI와 논의 후 진행)
		saveGraphs(response.emotionGraphs(), report);
		saveSuggestions(response.suggestions(), report);

		report.updateReportStatus(ReportStatus.GENERATED);
		log.info("success to save ai-report");
	}


	private void saveGraphs(List<ReportEmotionGraphResponse> responses, Report report) {

		List<ReportEmotionGraph> reportEmotionGraphs = new ArrayList<>();

		responses.forEach(response -> {
			reportEmotionGraphs.add(ReportEmotionGraph.builder()
				.sessionId(response.sessionId())
				.avgScore(response.avgScore())
				.inflectionType(response.inflectionType())
				.recordedAt(LocalDateTime.parse(response.recordedAt()))
				.report(report)
				.build()
			);
		});

		reportGraphRepository.saveAll(reportEmotionGraphs);
		log.info("success to save ai-report graphs");
	}

	private void saveSuggestions(List<ReportSuggestionResponse> responses, Report report) {
		List<ReportSuggestion> reportSuggestions = new ArrayList<>();

		responses.forEach(response -> {
			reportSuggestions.add(ReportSuggestion.builder()
				.suggestionType(response.type())
				.content(response.content())
				.priority(response.priority())
				.report(report)
				.build()
			);
		});

		reportSuggestionRepository.saveAll(reportSuggestions);
		log.info("success to save ai-report suggestions");
	}
}
