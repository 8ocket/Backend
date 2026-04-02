package com.kt.mindLog.service.report;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mindLog.domain.report.Report;
import com.kt.mindLog.domain.session.Session;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

	private final ReportRepository reportRepository;
	private final SessionRepository sessionRepository;
	private final EmotionRepository emotionRepository;
	private final UserRepository userRepository;


	public void createReport(final UUID userId, final ReportCreateRequest request) {
		var sessions = validateReport(userId, request);
		var scores = calculateScore(sessions);

		var reportRequest = AiReportCreateRequest.builder()
			.userId(userId.toString())
			.reportType(request.reportType().toString().toLowerCase())
			.periodStart(request.periodStart())
			.periodEnd(request.periodEnd())
			.emotionScores(scores)
			.build();

		// ai 에 요청
	}

	//유효 기간 내 세션 목록 조회
	private List<Session> validateReport(final UUID userId, final ReportCreateRequest request) {

		var endDate = LocalDate.now();

		if(ChronoUnit.DAYS.between(request.periodStart(), request.periodEnd()) > request.reportType().getMaxDays()) {
			endDate = request.periodStart().plusDays(request.reportType().getMaxDays());
		}

		if (endDate.isAfter(LocalDate.now())) {
			endDate = LocalDate.now();
		}

		var sessions = sessionRepository.findByUserIdAndCreatedAtBetweenOrderByEndedAtAsc(userId,
			request.periodStart().atStartOfDay(), endDate.atStartOfDay());

		Preconditions.validate(sessions.size() >= request.reportType().getMinSessions(),
			ErrorCode.INSUFFICIENT_SESSIONS);


		Preconditions.validate(!reportRepository.existsByUserIdAndPeriodStartAndPeriodEnd(userId, request.periodStart(), endDate),
			ErrorCode.REPORT_ALREADY_EXISTS);

		return sessions;
	}

	//감정 점수 계산
	private List<EmotionScoresResponse> calculateScore(final List<Session> sessions) {
		List<EmotionScoresResponse> scores = new ArrayList<>();

		sessions.forEach(session -> {
			var score = emotionRepository.sumIntensityBySessionId(session.getId());

			scores.add(EmotionScoresResponse.from(session,  score));
		});

		return scores;
	}


	@Transactional
	protected void saveReport(final UUID userId, final ReportCreateRequest request, final LocalDate periodEnd, final Integer sessionCount) {
		var user = userRepository.findByIdOrThrow(userId,  ErrorCode.NOT_FOUND_USER);

		var report = Report.builder()
			.reportType(request.reportType())
			.periodStart(request.periodStart())
			.periodEnd(periodEnd)
			.sessionCount(sessionCount)
			.user(user)
			.build();

		reportRepository.save(report);
		log.info("success to save ai-report");

		//TODO 리포트 성공적으로 생성 후 + 크레딧 차감
	}
}
