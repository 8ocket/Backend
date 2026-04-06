package com.kt.mindLog.dto.report.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EmotionGraphResponse(
	@JsonProperty("graph_count")
	Integer graphCount,
	List<GraphsResponse> graphs
) {
}
