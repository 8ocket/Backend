package com.kt.mindLog.dto.report.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TopicsResponse(
	List<AiReportTopicResponse> topics,
	@JsonProperty("topics_evaluation")
	String topicsEvaluation
) {
	public static TopicsResponse of(final List<AiReportTopicResponse> topics, final String evaluation) {
		return new TopicsResponse(
			topics,
			evaluation
		);
	}
}
