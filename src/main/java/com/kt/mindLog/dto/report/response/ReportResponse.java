package com.kt.mindLog.dto.report.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReportResponse(

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
	List<ReportEmotionGraphResponse> emotionGraphs,

	List<ReportTopicResponse> topics,

	List<ReportSuggestionResponse> suggestions
) {
}
