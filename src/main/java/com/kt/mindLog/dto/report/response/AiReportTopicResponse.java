package com.kt.mindLog.dto.report.response;

import com.kt.mindLog.domain.report.ReportTopic;

public record AiReportTopicResponse(
	String name,
	String category,
	String pattern
) {
	public static AiReportTopicResponse from(final ReportTopic topic) {
		return new AiReportTopicResponse(
			topic.getName(),
			topic.getCategory(),
			topic.getPattern()
		);
	}
}
