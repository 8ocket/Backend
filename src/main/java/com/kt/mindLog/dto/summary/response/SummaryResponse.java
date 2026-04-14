package com.kt.mindLog.dto.summary.response;

import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.domain.summary.SessionSummary;
import com.kt.mindLog.domain.user.User;

public record SummaryResponse(
	String fact,
	String emotion,
	String insight
) {
	public static SessionSummary to(final SummaryResponse response, Session session, User user) {
		return SessionSummary.builder()
			.fact(response.fact())
			.emotion(response.emotion())
			.insight(response.insight())
			.session(session)
			.user(user)
			.build();
	}
}
