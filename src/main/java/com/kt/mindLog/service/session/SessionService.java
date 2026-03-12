package com.kt.mindLog.service.session;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.dto.session.request.SessionCreateRequest;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.repository.PersonaRepository;
import com.kt.mindLog.repository.SessionRepository;
import com.kt.mindLog.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {
	private final SessionRepository sessionRepository;
	private final UserRepository userRepository;
	private final PersonaRepository personaRepository;
	private final SessionMessageService sessionMessageService;

	@Transactional
	public Flux<ServerSentEvent<?>> createSession(final Long userId, final SessionCreateRequest request) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);
		var persona = personaRepository.findByIdOrThrow(request.personaId(), ErrorCode.NOT_FOUND_PERSONA);

		var session = Session.builder()
			.user(user)
			.persona(persona)
			.build();

		sessionRepository.save(session);
		log.info("success to create session : userId = {}, sessionId = {}", userId, session.getId());

		return sessionMessageService.receiveSSE(request.firstContent(), session.getId(), userId);
	}
}
