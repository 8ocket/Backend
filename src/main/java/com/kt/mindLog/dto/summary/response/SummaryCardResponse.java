package com.kt.mindLog.dto.summary.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kt.mindLog.domain.summary.SessionSummary;

public record SummaryCardResponse(
	UUID summaryId,
	UUID sessionId,
	String fact,
	String emotion,
	String insight,
	boolean isEdited,
	String visibility,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public static SummaryCardResponse from(SessionSummary summary) {
		return new SummaryCardResponse(
			summary.getId(),
			summary.getSession().getId(),
			summary.getFact(),
			summary.getEmotion(),
			summary.getInsight(),
			summary.isEdited(),
			summary.getVisibility(),
			summary.getCreatedAt(),
			summary.getUpdatedAt()
		);
	}
}
