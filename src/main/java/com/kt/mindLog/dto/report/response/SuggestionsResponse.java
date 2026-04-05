package com.kt.mindLog.dto.report.response;

import com.kt.mindLog.domain.report.ReportSuggestion;

public record SuggestionsResponse(
	String title,
	String content
) {
	public static SuggestionsResponse from(ReportSuggestion reportSuggestion) {
		return new SuggestionsResponse(
			"null",
			reportSuggestion.getContent()
		);
	}
}
