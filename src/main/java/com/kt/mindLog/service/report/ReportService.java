package com.kt.mindLog.service.report;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mindLog.domain.report.Report;
import com.kt.mindLog.domain.report.ReportStatus;
import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.domain.session.SessionStatus;
import com.kt.mindLog.domain.summary.Emotion;
import com.kt.mindLog.domain.summary.NegativeEmotionType;
import com.kt.mindLog.domain.summary.PositiveEmotionType;
import com.kt.mindLog.dto.report.request.AiReportCreateRequest;
import com.kt.mindLog.dto.report.request.ReportCreateRequest;
import com.kt.mindLog.dto.report.response.EmotionScoresResponse;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.common.support.Preconditions;
import com.kt.mindLog.repository.UserRepository;
import com.kt.mindLog.repository.report.ReportRepository;
import com.kt.mindLog.repository.session.SessionRepository;
import com.kt.mindLog.repository.summary.EmotionRepository;

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

	private final ReportStreamService reportStreamService;


	public Flux<Object> createReport(final UUID userId, final ReportCreateRequest request) {
		//TODO credit 검증

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
	private List<EmotionScoresResponse> calculateScore(final List<Session> sessions) {
		return sessions.stream()
			.map(session -> EmotionScoresResponse.from(session, calculateEmotionScore(session)))
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
}
