package com.kt.mindLog.dto.report.response;

public record ReportSuggestionResponse(
	String type,
	String content,
	Integer priority
) {
}
