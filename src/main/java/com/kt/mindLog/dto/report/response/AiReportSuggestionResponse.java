package com.kt.mindLog.dto.report.response;

public record AiReportSuggestionResponse(
	String type,
	String content,
	Integer priority
) {
}
