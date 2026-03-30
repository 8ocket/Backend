package com.kt.mindLog.service.session;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.domain.session.SessionStatus;
import com.kt.mindLog.dto.session.request.SessionCreateRequest;
import com.kt.mindLog.dto.session.response.ActiveSessionResponse;
import com.kt.mindLog.dto.session.response.SessionDetailResponse;
import com.kt.mindLog.dto.session.response.SessionListResponse;
import com.kt.mindLog.dto.session.response.SessionListResponses;
import com.kt.mindLog.dto.sessionMessage.response.SessionMessageListResponse;
import com.kt.mindLog.dto.sessionMessage.response.SessionMessageResponse;
import com.kt.mindLog.dto.session.response.SessionResponse;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.common.response.Pagination;
import com.kt.mindLog.repository.PersonaRepository;
import com.kt.mindLog.repository.SessionMessageRepository;
import com.kt.mindLog.repository.session.SessionRepository;
import com.kt.mindLog.repository.UserRepository;
import com.kt.mindLog.repository.session.SessionRepositoryCustom;
import com.kt.mindLog.repository.summary.SummaryContextRepository;
import com.kt.mindLog.service.redis.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {
	private final SessionRepository sessionRepository;
	private final UserRepository userRepository;
	private final PersonaRepository personaRepository;
	private final SessionMessageRepository sessionMessageRepository;
	private final SummaryContextRepository summaryContextRepository;
	private final SessionRepositoryCustom sessionRepositoryCustom;

	private final SessionStreamService sessionStreamService;
	private final RedisService redisService;

	public SessionResponse saveSession(final UUID userId, final SessionCreateRequest request) {
		getHistory(userId);
		var newSession = createSession(userId, request);

		var messageId = sessionStreamService
			.receiveFirstMessage(request.firstContent(), newSession.getId(), userId);

		var message = sessionMessageRepository.findByIdOrThrow(UUID.fromString(messageId.toString()), ErrorCode.NOT_FOUND_SESSION_MESSAGE);
		var session = sessionRepository.findByIdOrThrow(newSession.getId(), ErrorCode.NOT_FOUND_SESSION);

		return SessionResponse.from(
			session,
			SessionMessageResponse.from(message
			));
	}

	@Transactional
	public Session createSession(final UUID userId, final SessionCreateRequest request) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);
		var persona = personaRepository.findByIdOrThrow(request.personaId(), ErrorCode.NOT_FOUND_PERSONA);

		var session = Session.builder()
			.user(user)
			.persona(persona)
			.build();

		sessionRepository.save(session);
		log.info("success to create session : userId = {}, sessionId = {}", userId, session.getId());

		return session;
	}

	public SessionListResponses getSessionList(final UUID userId, Pageable pageable,
		LocalDate startDate, LocalDate endDate, List<UUID>  personaIds) {
		userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		Page<SessionListResponse> sessions = sessionRepositoryCustom
			.findSessions(
				userId,
				startDate,
				endDate,
				personaIds,
				pageable)
			.map(SessionListResponse::from);

		Pagination pagination = Pagination.from(sessions);

		return SessionListResponses.from(sessions.toList(), pagination);
	}

	public SessionDetailResponse getSessionDetail(final UUID userId, final UUID sessionId) {
		userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);
		var session = sessionRepository.findByIdAndUserIdOrThrow(sessionId, userId, ErrorCode.NOT_FOUND_SESSION);
		var sessionMessages = sessionMessageRepository.findBySessionIdOrderByCreatedAtDesc(session.getId())
			.stream()
			.map(SessionMessageListResponse::from)
			.toList();

		var hasSummary = session.getSummary() != null;

		return SessionDetailResponse.from(session, sessionMessages, hasSummary);
	}

	public ActiveSessionResponse getActiveSession(final UUID userId) {
		userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		Optional<Session> session = sessionRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(userId, SessionStatus.ACTIVE);

		return session.map(ActiveSessionResponse::from).orElse(null);
	}

	private void getHistory(final UUID userId) {
		List<Session> sessions = sessionRepository.findTop15ByUserIdAndStatusOrderByCreatedAtDesc(userId, SessionStatus.SAVED);

		sessions.forEach(session -> {
			var summary = summaryContextRepository.findBySessionIdOrThrow(session.getId(), ErrorCode.NOT_FOUND_SUMMARY);
			redisService.pushHistory(userId, summary);
		});

		log.info("success to upload session history to redis : userId = {}", userId);
	}
}
