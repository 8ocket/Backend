package com.kt.mindLog.service.report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mindLog.domain.report.Report;
import com.kt.mindLog.domain.report.ReportAnalysis;
import com.kt.mindLog.domain.report.ReportEmotionGraph;
import com.kt.mindLog.domain.report.ReportStatus;
import com.kt.mindLog.domain.report.ReportSuggestion;
import com.kt.mindLog.domain.report.ReportTopic;
import com.kt.mindLog.dto.report.response.AiReportEmotionGraphResponse;
import com.kt.mindLog.dto.report.response.AiReportResponse;
import com.kt.mindLog.dto.report.response.AiReportSuggestionResponse;
import com.kt.mindLog.dto.report.response.AiReportTopicResponse;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.repository.report.ReportAnalysisRepository;
import com.kt.mindLog.repository.report.ReportEmotionGraphRepository;
import com.kt.mindLog.repository.report.ReportRepository;
import com.kt.mindLog.repository.report.ReportSuggestionRepository;
import com.kt.mindLog.repository.report.ReportTopicRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportPersistenceService {
	private final ReportRepository reportRepository;
	private final ReportEmotionGraphRepository reportGraphRepository;
	private final ReportSuggestionRepository reportSuggestionRepository;
	private final ReportTopicRepository reportTopicRepository;
	private final ReportAnalysisRepository reportAnalysisRepository;

	@Transactional
	public void saveReport(AiReportResponse response, UUID reportId) {
		var report = reportRepository.findByIdOrThrow(reportId, ErrorCode.NOT_FOUND_REPORT);

		saveGraphs(response.emotionGraphs(), report);
		saveTopics(response.topics(), report);
		saveSuggestions(response.suggestions(), report);
		saveAnalysis(response, report);

		report.updateReportStatus(ReportStatus.GENERATED);
		log.info("success to save ai-report");
	}


	private void saveGraphs(List<AiReportEmotionGraphResponse> responses, Report report) {

		var graphs = responses.stream()
				.map(response -> ReportEmotionGraph.builder()
							.sessionId(response.sessionId())
							.avgScore(response.avgScore())
							.inflectionType(response.inflectionType())
							.recordedAt(LocalDateTime.parse(response.recordedAt()))
							.report(report)
							.build()
				).toList();

		reportGraphRepository.saveAll(graphs);
		log.info("success to save ai-report graphs");
	}

	private void saveTopics(List<AiReportTopicResponse> responses, Report report) {

		var topics = responses.stream()
			.map(response -> ReportTopic.builder()
				.name(response.name())
				.category(response.category())
				.pattern(response.pattern())
				.report(report)
				.build()
			).toList();

		reportTopicRepository.saveAll(topics);
		log.info("success to save ai-report topics");
	}

	private void saveSuggestions(List<AiReportSuggestionResponse> responses, Report report) {

		var suggestions = responses.stream()
			.map(response -> ReportSuggestion.builder()
				.suggestionType(response.type())
				.title(response.title())
				.content(response.content())
				.priority(response.priority())
				.report(report)
				.build()
			).toList();

		reportSuggestionRepository.saveAll(suggestions);
		log.info("success to save ai-report suggestions");
	}

	private void saveAnalysis(AiReportResponse response, Report report) {

		var analysis = ReportAnalysis.builder()
			.currentStatus(response.currentStatus())
			.tendencySummary(response.tendency())
			.graphEvaluation(response.graphEvaluation())
			.topicEvaluation(response.topicEvaluation())
			.report(report)
			.build();

		reportAnalysisRepository.save(analysis);
		log.info("success to save ai-report analysis");
	}

}
