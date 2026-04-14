package com.kt.mindLog.dto.summary.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kt.mindLog.domain.summary.SessionSummary;

public record SummaryCardUpdateResponse(
	UUID summaryId,
	String fact,
	String emotion,
	String insight,
	boolean isEdited,
	LocalDateTime updatedAt
) {
	public static SummaryCardUpdateResponse from(SessionSummary summary) {
		return new SummaryCardUpdateResponse(
			summary.getId(),
			summary.getFact(),
			summary.getEmotion(),
			summary.getInsight(),
			summary.isEdited(),
			summary.getUpdatedAt()
		);
	}
}
