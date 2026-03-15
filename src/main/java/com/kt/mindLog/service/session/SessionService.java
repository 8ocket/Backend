package com.kt.mindLog.service.session;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.dto.session.request.SessionCreateRequest;
import com.kt.mindLog.dto.session.response.SessionMessageResponse;
import com.kt.mindLog.dto.session.response.SessionResponse;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.repository.PersonaRepository;
import com.kt.mindLog.repository.SessionMessageRepository;
import com.kt.mindLog.repository.SessionRepository;
import com.kt.mindLog.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {
	private final SessionRepository sessionRepository;
	private final UserRepository userRepository;
	private final PersonaRepository personaRepository;
	private final SessionMessageService sessionMessageService;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final SessionMessageRepository sessionMessageRepository;

	public SessionResponse saveSession(final UUID userId, final SessionCreateRequest request) {
		var session = createSession(userId, request);

		var messageId = sessionMessageService
			.receiveFirstMessage(request.firstContent(), session.getId(), userId);

		var message = sessionMessageRepository.findByIdOrThrow(UUID.fromString(messageId.toString()), ErrorCode.NOT_FOUND_SESSION_MESSAGE);

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
}
