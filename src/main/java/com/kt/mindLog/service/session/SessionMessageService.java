package com.kt.mindLog.service.session;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mindLog.domain.session.CrisisLogs;
import com.kt.mindLog.domain.session.SessionMessages;
import com.kt.mindLog.domain.session.SessionStatus;
import com.kt.mindLog.domain.user.Role;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.dto.sessionMessage.response.CrisisCheck;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.repository.SessionMessageRepository;
import com.kt.mindLog.repository.session.CrisisLogRepository;
import com.kt.mindLog.repository.session.SessionRepository;
import com.kt.mindLog.service.redis.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionMessageService {

	private final SessionMessageRepository sessionMessageRepository;
	private final SessionRepository sessionRepository;
	private final CrisisLogRepository crisisLogRepository;

	private final ObjectMapper objectMapper;

	private final RedisService redisService;

	@Transactional
	protected UUID saveContents(final Role role, final String contents, final UUID sessionId) {

		var session = sessionRepository.findByIdOrThrow(sessionId, ErrorCode.NOT_FOUND_SESSION);
		int sequence = redisService.pushMessage(sessionId, role, contents);
		//TODO 상담 메세지 암호화

		var message = sessionMessageRepository.saveAndFlush(SessionMessages.builder()
			.role(role)
			.content(contents)
			.session(session)
			.sequenceNum(sequence)
			.build());

		session.updateTime();

		log.info("success to save session message");
		return message.getId();
	}


	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void saveTitle(final UUID sessionId, final String contents) {

		var session = sessionRepository.findByIdOrThrow(sessionId, ErrorCode.NOT_FOUND_SESSION);
		String title = parseJson(contents, "title");
		session.updateTitle(title);

		sessionRepository.saveAndFlush(session);
		log.info("success to save session title");
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void saveCrisis(final UUID sessionId, final CrisisCheck crisisCheck, final User user, UUID messageId) {
		var session = sessionRepository.findByIdOrThrow(sessionId, ErrorCode.NOT_FOUND_SESSION);
		var messages = sessionMessageRepository.findByIdOrThrow(UUID.fromString(messageId.toString()),
			ErrorCode.NOT_FOUND_SESSION_MESSAGE);

		var crisis = CrisisLogs.builder()
			.level(crisisCheck.level())
			.keywords(crisisCheck.keywords())
			.message(crisisCheck.suggestedResponse())
			.session(session)
			.user(user)
			.messages(messages)
			.build();

		crisisLogRepository.saveAndFlush(crisis);
		log.info("success to save crisis log");
	}

	@Transactional
	protected void updateSessionStatus(final UUID sessionId, final SessionStatus sessionStatus) {
		var session =  sessionRepository.findByIdOrThrow(sessionId, ErrorCode.NOT_FOUND_SESSION);

		session.updateStatus(sessionStatus);
	}

	private String parseJson(final String contents, final String key) {
		return objectMapper.readTree(contents).get(key).asText();
	}
}
