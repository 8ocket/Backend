package com.kt.mindLog.dto.report.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiReportResponse(

	@JsonProperty("session_count")
	Integer sessionCount,

	@JsonProperty("graph_evaluation")
	String graphEvaluation,

	@JsonProperty("topics_evaluation")
	String topicEvaluation,

	@JsonProperty("current_status")
	String currentStatus,

	String tendency,

	@JsonProperty("emotion_graphs")
	List<AiReportEmotionGraphResponse> emotionGraphs,

	List<AiReportTopicResponse> topics,

	List<AiReportSuggestionResponse> suggestions
) {
}
