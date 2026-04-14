package com.kt.mindLog.dto.summary.request;

public record SummaryCardUpdateRequest(
	String fact,
	String emotion,
	String insight
) {
}
