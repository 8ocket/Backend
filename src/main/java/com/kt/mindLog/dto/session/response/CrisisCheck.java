package com.kt.mindLog.dto.session.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CrisisCheck (
	int level,
	List<String> keywords,
	@JsonProperty("suggested_response")
	String suggestedResponse
) {}
