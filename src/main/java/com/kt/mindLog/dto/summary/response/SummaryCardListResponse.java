package com.kt.mindLog.dto.summary.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.kt.mindLog.domain.summary.Emotion;
import com.kt.mindLog.domain.summary.EmotionType;
import com.kt.mindLog.domain.summary.SessionSummary;

public record SummaryCardListResponse(
	UUID summaryId,
	UUID sessionId,
	LocalDateTime createdAt,
	String fact,
	String emotion,
	String insight,
	List<EmotionInfo> emotions
) {
	public record EmotionInfo(
		EmotionType emotionType,
		Integer intensity
	) {
		public static EmotionInfo from(Emotion emotion) {
			return new EmotionInfo(
				emotion.getEmotionType(),
				emotion.getIntensity()
			);
		}
	}
	public static SummaryCardListResponse of(SessionSummary summary, List<Emotion> emotions) {
		return new SummaryCardListResponse(
			summary.getId(),
			summary.getSession().getId(),
			summary.getCreatedAt(),
			summary.getFact(),
			summary.getEmotion(),
			summary.getInsight(),
			emotions.stream().map(EmotionInfo::from).toList()
		);
	}
}
