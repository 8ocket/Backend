package com.kt.mindLog.dto.session.response;

import java.util.List;

import com.kt.mindLog.global.common.response.Pagination;

public record SessionListResponses(
	List<SessionListResponse> sessions,
	Pagination pagination
) {
	public static SessionListResponses from(List<SessionListResponse> sessions, Pagination pagination) {
		return new SessionListResponses(
			sessions,
			pagination
		);
	}
}
