package com.kt.mindLog.dto.report.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.report.ReportEmotionGraph;

public record GraphsResponse(
	@JsonProperty("session_id")
	UUID sessionId,

	@JsonProperty("avg_score")
	Integer avgScore,

	@JsonProperty("recorded_at")
	LocalDateTime recordedAt
) {
	public static GraphsResponse from(ReportEmotionGraph graph) {
		return new GraphsResponse(
			graph.getSessionId(),
			graph.getAvgScore(),
			graph.getRecordedAt()
		);
	}
}