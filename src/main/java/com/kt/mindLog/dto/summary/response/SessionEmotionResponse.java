package com.kt.mindLog.dto.summary.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.domain.session.SessionMessages;
import com.kt.mindLog.domain.summary.Emotion;
import com.kt.mindLog.domain.summary.EmotionTrajectory;
import com.kt.mindLog.domain.summary.EmotionType;
import com.kt.mindLog.domain.user.User;

public record SessionEmotionResponse(
	@JsonProperty("message_index")
	Integer messageIndex,

	@JsonProperty("emotion_type")
	String emotionType,

	Integer intensity,

	@JsonProperty("source_keyword")
	String sourceKeyword,

	@JsonProperty("trigger_context")
	String triggerContext,

	@JsonProperty("emotion_trajectory")
	String emotionTrajectory
) {
	public static Emotion to(SessionEmotionResponse response, User user, Session session, SessionMessages message) {
		return Emotion.builder()
			.emotionType(EmotionType.valueOf(response.emotionType.toUpperCase()))
			.intensity(response.intensity)
			.keyword(response.sourceKeyword())
			.trigger(response.triggerContext())
			.trajectory(EmotionTrajectory.valueOf(response.emotionTrajectory.toUpperCase()))
			.user(user)
			.session(session)
			.message(message)
			.build();
	}
}