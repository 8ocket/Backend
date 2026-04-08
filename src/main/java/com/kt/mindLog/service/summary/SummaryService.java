package com.kt.mindLog.service.summary;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.domain.summary.EmotionCard;
import com.kt.mindLog.domain.summary.SessionContextSummary;
import com.kt.mindLog.dto.summary.response.SessionEmotionResponse;
import com.kt.mindLog.dto.summary.response.SummaryResponse;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.security.encryption.EncryptionConverter;
import com.kt.mindLog.repository.SessionMessageRepository;
import com.kt.mindLog.repository.session.SessionRepository;
import com.kt.mindLog.repository.summary.EmotionCardRepository;
import com.kt.mindLog.repository.summary.EmotionRepository;
import com.kt.mindLog.repository.summary.SummaryContextRepository;
import com.kt.mindLog.repository.summary.SummaryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryService {
	private final SessionRepository sessionRepository;
	private final SessionMessageRepository sessionMessageRepository;
	private final EmotionRepository emotionRepository;
	private final SummaryRepository summaryRepository;
	private final SummaryContextRepository summaryContextRepository;
	private final EmotionCardRepository emotionCardRepository;

	private final ApplicationEventPublisher applicationEventPublisher;
	private final EncryptionConverter encryptionConverter;

	@Transactional
	public void saveEmotions(final UUID sessionId, List<SessionEmotionResponse> response) {
		var session = sessionRepository.findByIdOrThrow(sessionId, ErrorCode.NOT_FOUND_SESSION);

		response.forEach(emotionResponse -> {
			var message = sessionMessageRepository.findBySessionIdAndSequenceNumOrThrow(
				sessionId, emotionResponse.messageIndex(), ErrorCode.NOT_FOUND_SESSION_MESSAGE);

			var emotion = SessionEmotionResponse.to(
				emotionResponse,
				session.getUser(),
				session,
				message
			);

			emotionRepository.save(emotion);
		});

		applicationEventPublisher.publishEvent(session);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updatePrimaryEmotion(final Session session) {
		var emotionType = emotionRepository.findTopEmotionType(session);

		emotionRepository.findBySessionId(session.getId()).forEach(emotion -> {
			emotion.updatePrimary(emotion.getEmotionType().equals(emotionType));
		});
	}

	@Transactional
	public void saveSummary(final UUID sessionId, final SummaryResponse response) {
		var session = sessionRepository.findByIdOrThrow(sessionId, ErrorCode.NOT_FOUND_SESSION);

		var encryptFact = encryptionConverter.convertToDatabaseColumn(response.fact());
		var encryptEmotion = encryptionConverter.convertToDatabaseColumn(response.emotion());
		var encryptInsight = encryptionConverter.convertToDatabaseColumn(response.insight());

		var encryptSummary = new SummaryResponse(encryptFact, encryptEmotion, encryptInsight);

		var summary = SummaryResponse.to(encryptSummary, session, session.getUser());
		summaryRepository.save(summary);
		session.updateSummary(summary);
		log.info("success to save summary about sessionId: {}", sessionId);
	}

	@Transactional
	public void saveSessionContext(final UUID sessionId, String content) {
		var session = sessionRepository.findByIdOrThrow(sessionId, ErrorCode.NOT_FOUND_SESSION);

		var encryptContext = encryptionConverter.convertToDatabaseColumn(content);

		var context = SessionContextSummary.builder()
			.content(encryptContext)
			.user(session.getUser())
			.session(session)
			.build();

		summaryContextRepository.save(context);
	}

	@Transactional
	public void saveEmotionCard(final UUID sessionId, final String url) {
		var session = sessionRepository.findByIdOrThrow(sessionId, ErrorCode.NOT_FOUND_SESSION);

		var card = EmotionCard.builder()
			.imageUrl(url)
			.user(session.getUser())
			.session(session)
			.build();

		emotionCardRepository.save(card);
	}
}
