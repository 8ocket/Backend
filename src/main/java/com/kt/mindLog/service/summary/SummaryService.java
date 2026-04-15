package com.kt.mindLog.service.summary;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.domain.summary.EmotionCard;
import com.kt.mindLog.domain.summary.SessionContextSummary;
import com.kt.mindLog.domain.summary.SessionSummary;
import com.kt.mindLog.dto.summary.request.SummaryCardUpdateRequest;
import com.kt.mindLog.dto.summary.response.SessionEmotionResponse;
import com.kt.mindLog.dto.summary.response.SummaryCardListResponse;
import com.kt.mindLog.dto.summary.response.SummaryCardResponse;
import com.kt.mindLog.dto.summary.response.SummaryCardUpdateResponse;
import com.kt.mindLog.dto.summary.response.SummaryResponse;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.common.support.Preconditions;
import com.kt.mindLog.global.security.encryption.EncryptionConverter;
import com.kt.mindLog.repository.SessionMessageRepository;
import com.kt.mindLog.repository.session.SessionRepository;
import com.kt.mindLog.repository.summary.EmotionCardRepository;
import com.kt.mindLog.repository.summary.EmotionRepository;
import com.kt.mindLog.repository.summary.SummaryContextRepository;
import com.kt.mindLog.repository.summary.SummaryRepository;
import com.kt.mindLog.service.s3.S3Path;
import com.kt.mindLog.service.s3.S3Service;

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

	private final S3Service s3Service;

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
	public UUID saveSummary(final UUID sessionId, final SummaryResponse response) {
		var session = sessionRepository.findByIdOrThrow(sessionId, ErrorCode.NOT_FOUND_SESSION);

		var encryptFact = encryptionConverter.convertToDatabaseColumn(response.fact());
		var encryptEmotion = encryptionConverter.convertToDatabaseColumn(response.emotion());
		var encryptInsight = encryptionConverter.convertToDatabaseColumn(response.insight());

		var encryptSummary = new SummaryResponse(encryptFact, encryptEmotion, encryptInsight);

		var summary = SummaryResponse.to(encryptSummary, session, session.getUser());
		summaryRepository.save(summary);
		log.info("success to save summary about sessionId: {}", sessionId);
		return summary.getId();
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
			.backImageUrl(url)
			.user(session.getUser())
			.session(session)
			.build();

		emotionCardRepository.save(card);
	}

	@Transactional
	public void uploadSummaryCard(final UUID userId, final UUID summaryId, final MultipartFile cardFrontImage,
		final MultipartFile cardBackImage) {
		if (cardFrontImage == null || cardFrontImage.isEmpty() ||
			cardBackImage == null || cardBackImage.isEmpty()) {
			throw new CustomException(ErrorCode.INVALID_IMAGE_FILE);
		}

		SessionSummary summary = summaryRepository.findByIdOrThrow(summaryId, ErrorCode.NOT_FOUND_SUMMARY);

		Preconditions.validate(summary.getUser().getId().equals(userId), ErrorCode.NOT_SUMMARY_USER);
		log.info("success to get summary");
		Session session = summary.getSession();

		EmotionCard card = emotionCardRepository.findBySessionId(session.getId())
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CARD));
		log.info("success to get card");

		String frontImageUrl = s3Service.uploadImage(cardFrontImage, S3Path.SUMMARY);
		log.info("success to upload front image. summaryId={}, url={}", summaryId, frontImageUrl);

		String backImageUrl = s3Service.uploadImage(cardBackImage, S3Path.SUMMARY);
		log.info("success to upload back image. summaryId={}, url={}", summaryId, backImageUrl);

		card.updateImageUrl(frontImageUrl, backImageUrl);
	}

	@Transactional(readOnly = true)
	public SummaryCardResponse getSummaryCard(final UUID userId, final UUID summaryId) {
		SessionSummary summary = summaryRepository.findByIdOrThrow(summaryId, ErrorCode.NOT_FOUND_SUMMARY);

		Preconditions.validate(summary.getUser().getId().equals(userId), ErrorCode.NOT_SUMMARY_USER);

		return SummaryCardResponse.from(summary);
	}

	@Transactional
	public SummaryCardUpdateResponse updateSummaryCard(final UUID userId, final UUID summaryId,
		SummaryCardUpdateRequest request) {
		SessionSummary summary = summaryRepository.findByIdOrThrow(summaryId, ErrorCode.NOT_FOUND_SUMMARY);

		Preconditions.validate(summary.getUser().getId().equals(userId), ErrorCode.NOT_SUMMARY_USER);

		summary.updateSummaryCard(request.emotion(), request.fact(), request.insight());

		return SummaryCardUpdateResponse.from(summary);
	}

	@Transactional(readOnly = true)
	public Page<SummaryCardListResponse> getSummaryCardList(final UUID userId, Pageable pageable) {
		return summaryRepository.findAllByUserId(userId, pageable)
			.map(summary -> {
				EmotionCard card = emotionCardRepository.findBySessionId(summary.getSession().getId())
					.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CARD));
				return SummaryCardListResponse.of(summary, card);
				}
			);
	}
}
