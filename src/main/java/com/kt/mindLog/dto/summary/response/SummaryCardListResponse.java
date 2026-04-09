package com.kt.mindLog.dto.summary.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kt.mindLog.domain.summary.EmotionCard;
import com.kt.mindLog.domain.summary.SessionSummary;

public record SummaryCardListResponse(
	UUID summaryId,
	UUID cardId,
	String frontImageUrl,
	String backImageUrl,
	LocalDateTime createdAt
) {
	public static SummaryCardListResponse of(SessionSummary summary, EmotionCard card) {
		return new SummaryCardListResponse(
			card.getId(),
			summary.getId(),
			card.getFrontImageUrl(),
			card.getBackImageUrl(),
			summary.getCreatedAt()
		);
	}
}
