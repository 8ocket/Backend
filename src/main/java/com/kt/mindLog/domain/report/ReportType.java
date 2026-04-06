package com.kt.mindLog.domain.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportType {
	WEEKLY(7, 2),
	MONTHLY(31, 4);

	private final int maxDays;
	private final int minSessions;
}
