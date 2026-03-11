package com.kt.mindLog.dto.session.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CrisisCheck (
	boolean detected,
	List<String> keywords,
	@JsonProperty("suggested_response")
	String suggestedResponse
) {}
