package com.kt.mindLog.service.report;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.kt.mindLog.domain.report.Report;
import com.kt.mindLog.domain.report.ReportStatus;
import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.domain.session.SessionStatus;
import com.kt.mindLog.domain.summary.Emotion;
import com.kt.mindLog.domain.summary.NegativeEmotionType;
import com.kt.mindLog.domain.summary.PositiveEmotionType;
import com.kt.mindLog.dto.report.request.AiReportCreateRequest;
import com.kt.mindLog.dto.report.request.ReportCreateRequest;
import com.kt.mindLog.dto.report.request.EmotionScoresRequest;
import com.kt.mindLog.dto.report.response.AiReportTopicResponse;
import com.kt.mindLog.dto.report.response.EmotionGraphResponse;
import com.kt.mindLog.dto.report.response.GraphsResponse;
import com.kt.mindLog.dto.report.response.ReportResponse;
import com.kt.mindLog.dto.report.response.SuggestionsResponse;
import com.kt.mindLog.dto.report.response.TendencyResponse;
import com.kt.mindLog.dto.report.response.TopicsResponse;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.common.support.Preconditions;
import com.kt.mindLog.global.property.StreamProperties;
import com.kt.mindLog.global.security.encryption.EncryptionConverter;
import com.kt.mindLog.repository.UserRepository;
import com.kt.mindLog.repository.report.ReportAnalysisRepository;
import com.kt.mindLog.repository.report.ReportEmotionGraphRepository;
import com.kt.mindLog.repository.report.ReportRepository;
import com.kt.mindLog.repository.report.ReportSuggestionRepository;
import com.kt.mindLog.repository.report.ReportTopicRepository;
import com.kt.mindLog.repository.session.SessionRepository;
import com.kt.mindLog.repository.summary.EmotionRepository;
import com.kt.mindLog.service.credit.CreditService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

	private final ReportRepository reportRepository;
	private final SessionRepository sessionRepository;
	private final EmotionRepository emotionRepository;
	private final UserRepository userRepository;

	private final ReportEmotionGraphRepository reportGraphRepository;
	private final ReportSuggestionRepository reportSuggestionRepository;
	private final ReportAnalysisRepository reportAnalysisRepository;
	private final ReportTopicRepository reportTopicRepository;

	private final ReportStreamService reportStreamService;
	private final CreditService creditService;
	private final EncryptionConverter encryptionConverter;

	private final WebClient webClient;
	private final StreamProperties streamProperties;


	public Flux<Object> createReport(final UUID userId, final ReportCreateRequest request) {
		// 크레딧 검증
		creditService.validateCreditForReport(userId, request.reportType());

		var periodEnd = validatePeriodEnd(request);
		var sessions = validateReport(userId, request, periodEnd);
		var scores = calculateScore(sessions);

		var reportId = saveReport(userId, request, periodEnd, sessions.size());

		var reportRequest = AiReportCreateRequest.builder()
			.userId(userId.toString())
			.reportType(request.reportType().toString().toLowerCase())
			.periodStart(request.periodStart())
			.periodEnd(periodEnd)
			.emotionScores(scores)
			.build();

		return reportStreamService.receiveSSE(reportRequest, reportId);
	}

	private LocalDate validatePeriodEnd(final ReportCreateRequest request) {
		var endDate = LocalDate.now();

		if(ChronoUnit.DAYS.between(request.periodStart(), request.periodEnd()) > request.reportType().getMaxDays()) {
			endDate = request.periodStart().plusDays(request.reportType().getMaxDays());
		}

		if (endDate.isAfter(LocalDate.now())) {
			endDate = LocalDate.now();
		}

		return endDate;
	}

	//유효 기간 내 세션 목록 조회
	private List<Session> validateReport(final UUID userId, final ReportCreateRequest request, final LocalDate periodEnd) {

		var sessions = sessionRepository.findByUserIdAndStatusAndCreatedAtBetweenOrderByEndedAtAsc(userId,
			SessionStatus.SAVED, request.periodStart().atStartOfDay(), periodEnd.atStartOfDay().plusDays(1));

		Preconditions.validate(sessions.size() >= request.reportType().getMinSessions(),
			ErrorCode.INSUFFICIENT_SESSIONS);


		Preconditions.validate(!reportRepository.existsByUserIdAndStatusAndPeriodStartAndPeriodEnd(userId,
				ReportStatus.GENERATED, request.periodStart(), periodEnd), ErrorCode.REPORT_ALREADY_EXISTS);

		return sessions;
	}

	//감정 점수 계산
	private List<EmotionScoresRequest> calculateScore(final List<Session> sessions) {
		return sessions.stream()
			.map(session -> EmotionScoresRequest.from(session, calculateEmotionScore(session)))
			.toList();
	}

	private int calculateEmotionScore(final Session session) {
		return emotionRepository.findBySessionId(session.getId()).stream()
			.mapToInt(this::getSignedIntensity)
			.sum();
	}

	private int getSignedIntensity(final Emotion emotion) {
		if (PositiveEmotionType.contains(emotion.getEmotionType())) return emotion.getIntensity();
		if (NegativeEmotionType.contains(emotion.getEmotionType())) return -emotion.getIntensity();
		return 0;
	}


	@Transactional
	protected UUID saveReport(final UUID userId, final ReportCreateRequest request, final LocalDate periodEnd, final Integer sessionCount) {
		var user = userRepository.findByIdOrThrow(userId,  ErrorCode.NOT_FOUND_USER);

		var report = Report.builder()
			.reportType(request.reportType())
			.periodStart(request.periodStart())
			.periodEnd(periodEnd)
			.sessionCount(sessionCount)
			.user(user)
			.build();

		reportRepository.save(report);
		log.info("success to create ai-report");

		return report.getId();
	}

	public List<ReportResponse> getReports(final UUID userId) {
		return reportRepository.findByUserIdAndStatusIsNotOrderByCreatedAtDesc(userId, ReportStatus.FAILED)
			.stream()
			.map(ReportResponse::from)
			.toList();
	}

	@Transactional
	public EmotionGraphResponse getEmotionGraphs(final UUID reportId) {
		var report = reportRepository.findByIdOrThrow(reportId, ErrorCode.NOT_FOUND_REPORT);

		if (!report.isViewed()) report.updateIsViewed();

		var graphs = reportGraphRepository.findByReportId(reportId).stream()
			.map(GraphsResponse::from)
			.toList();

		var analysis = reportAnalysisRepository.findByReportIdOrThrow(reportId, ErrorCode.NOT_FOUND_REPORT);
		return new EmotionGraphResponse(graphs.size(), graphs, decrypt(analysis.getGraphEvaluation()));
	}

	public List<SuggestionsResponse> getSuggestions(final UUID reportId) {
		return  reportSuggestionRepository.findByReportIdOrderByPriorityAsc(reportId)
			.stream()
			.map(suggestion -> new SuggestionsResponse(
				decrypt(suggestion.getTitle()), decrypt(suggestion.getContent())
			)).toList();
	}

	public TopicsResponse getTopics(final UUID reportId) {
		var topics = reportTopicRepository.findByReportId(reportId).stream()
			.map(topic -> new AiReportTopicResponse(
				decrypt(topic.getName()), decrypt(topic.getCategory()), decrypt(topic.getPattern())
			)).toList();

		var analysis = reportAnalysisRepository.findByReportIdOrThrow(reportId, ErrorCode.NOT_FOUND_REPORT);
		return TopicsResponse.of(topics, decrypt(analysis.getTopicEvaluation()));
	}

	public TendencyResponse getTendency(final UUID reportId) {
		var analysis = reportAnalysisRepository.findByReportIdOrThrow(reportId, ErrorCode.NOT_FOUND_REPORT);
		return new TendencyResponse(decrypt(analysis.getCurrentStatus()), decrypt(analysis.getTendencySummary()));
	}

	private String decrypt(String value) {
		return encryptionConverter.convertToEntityAttribute(value);
	}

	@Scheduled(cron = "0 0 0 * * *")
	@Transactional
	protected void checkFailedReport() {
		var expiredReport = reportRepository.findByStatusIsNotAndCreatedAtBefore(
			ReportStatus.FAILED, LocalDateTime.now().minusDays(1));

		expiredReport.forEach(report -> report.updateReportStatus(ReportStatus.FAILED));
		log.info("check failed report");
	}

	@Transactional
	public void deleteReport(UUID userId, UUID reportId) {
		reportGraphRepository.deleteByReportId(reportId);
		reportTopicRepository.deleteByReportId(reportId);
		reportSuggestionRepository.deleteByReportId(reportId);
		reportAnalysisRepository.deleteByReportId(reportId);

		reportRepository.deleteById(reportId);

		deleteAIReport(reportId);

		log.info("success to delete report : userId = {}, sessionId = {}", userId, reportId);
	}

	private void deleteAIReport(UUID reportId) {
		webClient.delete()
			.uri(streamProperties.getReportUri()+"/{reportId}", reportId)
			.retrieve()
			.bodyToMono(Void.class)
			.block();
	}
}
