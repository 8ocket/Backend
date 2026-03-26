package com.kt.mindLog.dto.summary.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import tools.jackson.databind.JsonNode;

public record SessionSummaryResponse(
	SummaryResponse summary,
	@JsonProperty("context_summary")
	String contextSummary,
	List<SessionEmotionResponse> emotions,
	JsonNode card
) {
}
